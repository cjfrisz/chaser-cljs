;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 15 Sep 2013
;; 
;; Entrypoint for the game.
;;----------------------------------------------------------------------

(ns chaser-cljs.core
  (:require-macros [chaser-cljs.macros :refer (get-2d-context)])
  (:require [dommy.core :as dommy]
            [chaser-cljs.dom :as dom]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.rules :as rules]
            [chaser-cljs.system :as system]))

(defn key-handler
  [game-env renderer key-event]
  (let [dir (case (. key-event -keyCode)
                37 :left
                38 :up
                39 :right
                40 :down
                nil)]
    (when dir
      (let [game-env* (rules/move-player @game-env dir)]
        (if (rules/exit-reached? game-env*)
            (let [new-game-env (game-env/make-game-env)]
              (dom/reset-canvas! new-game-env renderer)
              (swap! game-env (constantly new-game-env)))
            (swap! game-env (constantly game-env*))))
      (.preventDefault key-event))))

(def request-animation-frame
  (or (. js/window -requestAnimationFrame)
      (. js/window -webkitRequestAnimationFrame)
      (. js/window -mozRequestAnimationFrame)
      #(js/setInterval % (/ 1000 60))))

(defn start
  [system]
  (let [env (system/get-env system)
        renderer (system/get-renderer system)]
    (dom/init-canvas! @env renderer)
    (dommy/listen! js/document :keydown (partial key-handler env renderer))
    (let [ctx (get-2d-context dom/game-canvas-id)]
      (letfn [(animate! []
                (request-animation-frame animate!)
                (proto/render! renderer @env ctx))]
        (animate!)))))

(set! (.-onload js/window) #(start (system/make-system)))
