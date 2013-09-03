;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified  2 Sep 2013
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
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.game :as game-render]))

;; NB: these need a new home in a containing data structure that also
;;     stores the rendering context so it's not looked up each tick
(def global-game-env (atom nil))
(def global-game-renderer (atom nil))

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
          (as-> (game-env/get-player game-env) player
            (game-env/move-player player dir 
              (game-env/get-board game-env))
            (game-env/update-player game-env player))))
      (.preventDefault key-event))))

(defn game-loop []
  (let [game-env @global-game-env
        player (game-env/get-player game-env)
        exit (game-env/get-exit game-env)
        ctx (js-utils/get-2d-context dom/game-canvas-id)]
    (when (and (= (player/get-x player) (exit/get-x exit))
               (= (player/get-y player) (exit/get-y exit)))
      (let [new-game-env (game-env/make-fresh-game-env)]
        (dom/reset-canvas! new-game-env @global-game-renderer)
        (swap! global-game-env (constantly new-game-env))))
    (proto/render! @global-game-renderer @global-game-env ctx)))

(set! (.-onload js/window) 
  #(let [game-env (game-env/make-fresh-game-env)
         game-renderer (game-render/make-game-renderer)]
     (swap! global-game-env (constantly game-env))
     (swap! global-game-renderer (constantly game-renderer))
     (dom/init-canvas! game-env game-renderer)
     (dommy/listen! js/document :keydown key-handler)
     (js/setInterval game-loop (/ 1000 30))))
