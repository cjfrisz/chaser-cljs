;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 28 Aug 2013
;; 
;; Entrypoint for the game.
;;----------------------------------------------------------------------

(ns chaser-cljs.core
  (:require-macros [dommy.macros :refer (node sel1)])
  ;; NB: this number of requires might indicate something fishy; either
  ;;     some work needs to be offloaded to other files or namespace
  ;;     resolution should be done at the call site
  (:require [dommy.core :refer (append! listen!)]
            [chaser-cljs.coords :refer (make-coords
                                        coords-get-x coords-get-y
                                        coords-update-x 
                                        coords-update-y)]
            [chaser-cljs.game-env :refer (make-game-env
                                          game-env-get-game-map
                                          game-env-get-game-map-renderer
                                          game-env-get-player
                                          game-env-get-player-renderer
                                          game-env-get-key-stream
                                          game-env-update-game-map
                                          game-env-update-player
                                          game-env-update-key-stream)]
            [chaser-cljs.game-map :refer (game-map-get-space
                                          make-game-map-renderer)]
            [chaser-cljs.js-utils :refer (get-2d-context)]
            [chaser-cljs.key-stream :refer (make-key-stream 
                                            key-stream-enqueue
                                            key-stream-dequeue-batch)]
            [chaser-cljs.map-generator :refer (build-map)]
            [chaser-cljs.player :refer (make-player 
                                        player-get-x player-get-y
                                        player-update-x player-update-y
                                        make-player-renderer)]
            [chaser-cljs.protocols :refer (render)]))

;; NB: still need to de-globalify these
(def map-size 15)
(def game-div-id :#game)
(def game-canvas-id "gameCanvas")

(def global-game-env (atom nil))

(defn init-canvas!
  "Initialize the game canvas."
  [game-env]
  (let [game-map (game-env-get-game-map game-env)
        game-map-renderer (game-env-get-game-map-renderer game-env)]
    (letfn [(game-map-max-coord [coord-getter]
              (reduce (fn [cur-max coord]
                        (max cur-max (coord-getter coord)))
                0
                game-map))
            (dimension-pxs [max-coord]
              ;; NB: direct access to record member! yuck!
              (+ (* (inc max-coord) (:space-width game-map-renderer)) 
                 ;; NB: again with the direct record member access!
                 (* (:outer-border-size game-map-renderer) 2)))]
      (let [max-x (game-map-max-coord coords-get-x)
            max-y (game-map-max-coord coords-get-y)]
        (append! (sel1 game-div-id)
          (node 
           [:canvas
            {:id game-canvas-id 
             :width (dimension-pxs max-x)
             :height (dimension-pxs max-y)
             :style "border:1px solid #000000;"}]))))))

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
        game-map (game-env-get-game-map game-env)
        ctx (get-2d-context)]
    (loop [key* (key-stream-dequeue-batch 
                  (game-env-get-key-stream game-env))
           player (game-env-get-player game-env)]
      (if (nil? (seq key*))
          (do
            (render (game-env-get-game-map-renderer game-env) 
              ctx
              game-env)
            (render (game-env-get-player-renderer game-env)
              ctx
              game-env)
            (swap! global-game-env
              (comp #(game-env-update-player % player)
                #(game-env-update-key-stream % [])))) 
          (recur (next key*)
            ;; NB: warning! leaky key-stream abstraction!
            (move-player player (first key*) game-map))))))

(def player-start-coords rand-nth)

(defn init-game-env []
  (let [game-map (build-map map-size)]
    (make-game-env game-map (make-game-map-renderer)
      (make-player (player-start-coords game-map))
      (make-player-renderer)
      (make-key-stream))))
             
(set! (.-onload js/window) 
  #(let [game-env (init-game-env)]
     (swap! global-game-env (constantly game-env))
     (init-canvas! game-env)
     (listen! js/document :keydown key-handler)
     (js/setInterval game-loop (/ 1000 30))))
