;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 25 Aug 2013
;; 
;; Entrypoint for the game.
;;----------------------------------------------------------------------

(ns chaser-cljs.core
  (:require-macros [dommy.macros :refer (node sel1)])
  (:require [dommy.core :refer (append! listen!)]
            [chaser-cljs.coords :refer (make-coords
                                        coords-get-x coords-get-y
                                        coords-update-x 
                                        coords-update-y)]
            [chaser-cljs.game-env :refer (make-game-env
                                          game-env-get-game-map
                                          game-env-get-player
                                          game-env-get-key-stream
                                          game-env-update-game-map
                                          game-env-update-player
                                          game-env-update-key-stream)]
            [chaser-cljs.game-map :refer (game-map-get-space
                                          render-map render-space)]
            [chaser-cljs.key-stream :refer (make-key-stream 
                                            key-stream-enqueue
                                            key-stream-dequeue-batch)]
            [chaser-cljs.player :refer (make-player 
                                        player-get-x player-get-y
                                        player-update-x player-update-y
                                        render-player)]
            [chaser-cljs.map-generator :refer (build-map)]))

;; NB: move these into params.cljs
(def map-size 15)
(def space-width 50)
(def canvas-border-width space-width)

(def global-game-env (atom nil))

(defn init-canvas!
  "Initialize the game canvas."
  [game-map]
  (letfn [(game-map-max-coord [coord-getter]
            (reduce (fn [cur-max coord]
                      (max cur-max (coord-getter coord)))
              0
              game-map))
          (dimension-pxs [max-coord]
            (+ (* (inc max-coord) space-width) 
               (* canvas-border-width 2)))]
    (let [max-x (game-map-max-coord coords-get-x)
          max-y (game-map-max-coord coords-get-y)]
      ;; NB: id for game div should probably go in params.cljs
      (append! (sel1 :#game)
        (node 
         ;; NB: lift gameCanvas tag out to params.cljs
         [:canvas#gameCanvas
          {:width (dimension-pxs max-x)
           :height (dimension-pxs max-y)
           :style "border:1px solid #000000;"}])))))

(defn key-handler
    [key-event]
    (let [dir (case (. key-event -keyCode)
                37 :left
                38 :down
                39 :right
                40 :up
                nil)]
      (when dir
        (swap! global-game-env
          (fn [game-env]
            (game-env-update-key-stream game-env
              (key-stream-enqueue (game-env-get-key-stream game-env)
                dir))))
        (.preventDefault key-event))))

(defn move-player
  [player dir game-map]
  (assert (some #{dir} [:left :down :right :up]))
  (let [target-x ((case dir :right inc :left dec identity)
                   (player-get-x player))
        target-y ((case dir :up inc :down dec identity) 
                   (player-get-y player))]
    (if (game-map-get-space game-map target-x target-y)
        (as-> player player
          (player-update-x player target-x)
          (player-update-y player target-y))
        player)))

(defn game-loop []
  (let [game-env @global-game-env
        game-map (game-env-get-game-map game-env)]
    (loop [key* (key-stream-dequeue-batch 
                  (game-env-get-key-stream game-env))
           player (game-env-get-player game-env)]
      (if (nil? (seq key*))
          (do
            (render-map game-map)
            (render-player player)
            (swap! global-game-env
              (comp #(game-env-update-player % player)
                #(game-env-update-key-stream % [])))) 
          (recur (next key*)
            ;; NB: warning! leaky key-stream abstraction!
            (move-player player (first key*) game-map))))))

(def player-start-coords rand-nth)

(defn init-game-env []
  (let [game-map (build-map map-size)]
    (make-game-env game-map
      (make-player (player-start-coords game-map))
      (make-key-stream))))
             
(set! (.-onload js/window) 
  #(let [game-env (init-game-env)]
     (swap! global-game-env (constantly game-env))
     (init-canvas! (game-env-get-game-map game-env))
     (listen! js/document :keydown key-handler)
     (js/setInterval game-loop (/ 1000 30))))
