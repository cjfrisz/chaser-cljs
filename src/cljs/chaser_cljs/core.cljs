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
  (:require [dommy.core :as dommy]
            [chaser-cljs.board :as board]
            [chaser-cljs.board-generator :as board-generator]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.js-utils :as js-utils]
            [chaser-cljs.key-stream :as key-stream]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.board :as board-render]))

;; NB: still need to de-globalify these
(def board-size 15)
(def game-div-id :#game)
(def game-canvas-id :#gameCanvas)
(def game-canvas-id-str (subs (name game-canvas-id) 1))

(def global-game-env (atom nil))

(defn reset-canvas! 
  [game-env]
  (let [board (game-env/get-board game-env)
        board-renderer (game-env/get-board-renderer game-env)]
    (letfn [(board-max-coord [coord-getter]
              (reduce (fn [cur-max coord]
                        (max cur-max (coord-getter coord)))
                0
                board))
            (dimension-pxs [max-coord]
              ;; NB: direct access to record member! yuck!
              (+ (* (inc max-coord) (:space-width board-renderer)) 
                 ;; NB: again with the direct record member access!
                 (* (:outer-border-size board-renderer) 2)))]
      (let [game-canvas (sel1 game-canvas-id)
            width (dimension-pxs (board-max-coord coords/get-x))
            height (dimension-pxs (board-max-coord coords/get-y))]
        (set! (.-width game-canvas) width)
        (set! (.-height game-canvas) height)))))

(defn init-canvas!
  "Initialize the game canvas."
  [game-env]
  (dommy/append! (sel1 game-div-id) (node [:canvas {:id game-canvas-id-str}]))
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
  [player dir board]
  (assert (some #{dir} [:left :down :right :up]))
  (let [target-x ((case dir :right inc :left dec identity)
                   (player/get-x player))
        target-y ((case dir :up inc :down dec identity) 
                   (player/get-y player))]
    (if (board/get-space board target-x target-y)
        (as-> player player
          (player/update-x player target-x)
          (player/update-y player target-y))
        player)))

(def player-start-coords rand-nth)

(defn exit-start-coords
  [board player]
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
                       board)]
    (if (nil? (seq exit-coords*))
      (rand-nth (filter #(or (not (= (coords/get-x %) (player/get-x player)))
                             (not (= (coords/get-y %) (player/get-y player))))
                  board))
      (rand-nth exit-coords*))))
             
(defn init-game-env []
  (let [board (board-generator/build-board board-size)
        player (player/make-player (player-start-coords board))]
    (game-env/make-game-env 
      board
      player
      (exit/make-exit (exit-start-coords board player))
      (board-render/make-renderer)
      (player/make-renderer)
      (exit/make-renderer)
      (key-stream/make-key-stream))))

(defn game-loop []
  (let [game-env @global-game-env
        board (game-env/get-board game-env)
        player (game-env/get-player game-env)
        exit (game-env/get-exit game-env)
        ctx (js-utils/get-2d-context game-canvas-id)]
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
                (proto/render (game-env/get-board-renderer game-env)
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
              (move-player player (first key*) board)))))))

(set! (.-onload js/window) 
  #(let [game-env (init-game-env)]
     (swap! global-game-env (constantly game-env))
     (init-canvas! game-env)
     (dommy/listen! js/document :keydown key-handler)
     (js/setInterval game-loop (/ 1000 30))))
