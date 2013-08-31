;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 31 Aug 2013
;; 
;; Entrypoint for the game.
;;----------------------------------------------------------------------

(ns chaser-cljs.core
  (:require-macros [dommy.macros :refer (node sel1)])
  ;; NB: this number of requires might indicate something fishy; either
  ;;     some work needs to be offloaded to other files or namespace
  ;;     resolution should be done at the call site
  (:require [dommy.core :as dommy]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.game-map :as game-map]
            [chaser-cljs.js-utils :as js-utils]
            [chaser-cljs.key-stream :as key-stream]
            [chaser-cljs.map-generator :as map-generator]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]))

;; NB: still need to de-globalify these
(def map-size 15)
(def game-div-id :#game)
;; NB: should change this to :#gameCanvas and replace where raw symbol
;;     is used
(def game-canvas-id "gameCanvas")

(def global-game-env (atom nil))

(defn reset-canvas! 
  [game-env]
  (let [game-map (game-env/get-game-map game-env)
        game-map-renderer (game-env/get-game-map-renderer game-env)]
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
      (let [game-canvas (sel1 :#gameCanvas)
            width (dimension-pxs (game-map-max-coord coords/get-x))
            height (dimension-pxs (game-map-max-coord coords/get-y))]
        (set! (.-width game-canvas) width)
        (set! (.-height game-canvas) height)))))

(defn init-canvas!
  "Initialize the game canvas."
  [game-env]
  (dommy/append! (sel1 game-div-id) (node [:canvas {:id game-canvas-id}]))
  (reset-canvas! game-env))

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
            (game-env/update-key-stream game-env
              (key-stream/enqueue (game-env/get-key-stream game-env)
                dir))))
        (.preventDefault key-event))))

(defn move-player
  [player dir game-map]
  (assert (some #{dir} [:left :down :right :up]))
  (let [target-x ((case dir :right inc :left dec identity)
                   (player/get-x player))
        target-y ((case dir :up inc :down dec identity) 
                   (player/get-y player))]
    (if (game-map/get-space game-map target-x target-y)
        (as-> player player
          (player/update-x player target-x)
          (player/update-y player target-y))
        player)))

(def player-start-coords rand-nth)

(defn exit-start-coords
  [game-map player]
  (let [exit-coords* (filter (fn [coords]
                               (> (.sqrt js/Math 
                                         (+ (.pow js/Math
                                              (- (player/get-x player)
                                                 (coords/get-x coords))
                                              2)
                                            (.pow js/Math
                                              (- (player/get-y player)
                                                 (coords/get-y coords))
                                              2)))
                                   (quot map-size 3)))
                       game-map)]
    (if (nil? (seq exit-coords*))
      (rand-nth (filter #(or (not (= (coords/get-x %) (player/get-x player)))
                             (not (= (coords/get-y %) (player/get-y player))))
                  game-map))
      (rand-nth exit-coords*))))
             
(defn init-game-env []
  (let [game-map (map-generator/build-map map-size)
        player (player/make-player (player-start-coords game-map))]
    (game-env/make-game-env 
      game-map
      player
      (exit/make-exit (exit-start-coords game-map player))
      (game-map/make-renderer)
      (player/make-renderer)
      (exit/make-renderer)
      (key-stream/make-key-stream))))

(defn game-loop []
  (let [game-env @global-game-env
        game-map (game-env/get-game-map game-env)
        player (game-env/get-player game-env)
        exit (game-env/get-exit game-env)
        ;; NB: lift :#gameCanvas into a constant
        ctx (js-utils/get-2d-context :#gameCanvas)]
    (if (and (= (player/get-x player) (exit/get-x exit))
             (= (player/get-y player) (exit/get-y exit)))
        (let [new-game-env (init-game-env)]
          (reset-canvas! new-game-env)
          (swap! global-game-env (constantly new-game-env)))
        (loop [key* (key-stream/dequeue-batch 
                      (game-env/get-key-stream game-env))
               player player]
          (if (nil? (seq key*))
              (do
                (proto/render (game-env/get-game-map-renderer game-env)
                   ctx
                   game-env)
                (proto/render (game-env/get-player-renderer game-env)
                  ctx
                  game-env)
                (proto/render (game-env/get-exit-renderer game-env)
                  ctx
                  game-env)
                (swap! global-game-env
                  (comp #(game-env/update-player % player)
                    #(game-env/update-key-stream % [])))) 
            (recur (next key*)
              ;; NB: warning! leaky key-stream abstraction!
              (move-player player (first key*) game-map)))))))

(set! (.-onload js/window) 
  #(let [game-env (init-game-env)]
     (swap! global-game-env (constantly game-env))
     (init-canvas! game-env)
     (dommy/listen! js/document :keydown key-handler)
     (js/setInterval game-loop (/ 1000 30))))
