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
  (:require [dommy.core :as dommy]
            [chaser-cljs.board :as board]
            [chaser-cljs.dom :as dom]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.js-utils :as js-utils]
            [chaser-cljs.key-stream :as key-stream]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]))

(def global-game-env (atom nil))

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

(defn game-loop []
  (let [game-env @global-game-env
        board (game-env/get-board game-env)
        player (game-env/get-player game-env)
        exit (game-env/get-exit game-env)
        ctx (js-utils/get-2d-context dom/game-canvas-id)]
    (if (and (= (player/get-x player) (exit/get-x exit))
             (= (player/get-y player) (exit/get-y exit)))
        (let [new-game-env (game-env/make-fresh-game-env)]
          (dom/reset-canvas! new-game-env)
          (swap! global-game-env (constantly new-game-env)))
        (loop [key* (key-stream/dequeue-batch 
                      (game-env/get-key-stream game-env))
               player player]
          (if (nil? (seq key*))
              ;; NB: this operation needs to go in a new game-renderer
              (do
                (proto/render! (game-env/get-board-renderer game-env)
                  board
                  ctx)
                (proto/render! (game-env/get-player-renderer game-env)
                  player
                  ctx)
                (proto/render! (game-env/get-exit-renderer game-env)
                  exit
                  ctx)
                (swap! global-game-env
                  (comp #(game-env/update-player % player)
                    #(game-env/update-key-stream % [])))) 
            (recur (next key*)
              (move-player player (first key*) board)))))))

(set! (.-onload js/window) 
  #(let [game-env (game-env/make-fresh-game-env)]
     (swap! global-game-env (constantly game-env))
     (dom/init-canvas! game-env)
     (dommy/listen! js/document :keydown key-handler)
     (js/setInterval game-loop (/ 1000 30))))
