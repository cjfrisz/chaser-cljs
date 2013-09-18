;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 16 Sep 2013
;; 
;; Entrypoint for the game.
;;----------------------------------------------------------------------

(ns chaser-cljs.core
  (:require-macros [chaser-cljs.macros :refer (get-2d-context)])
  (:require [dommy.core :as dommy]
            [chaser-cljs.dom :as dom]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.game :as game-render]
            [chaser-cljs.rules :as rules]
            [chaser-cljs.system :as system]))

(defn key-handler
  [system-atom key-event]
  (let [dir (case (. key-event -keyCode)
                37 :left
                38 :up
                39 :right
                40 :down
                nil)]
    (when dir
      (let [game-env+ (rules/move-player
                        (system/get-env @system-atom)
                        dir)]
        (if (rules/exit-reached? game-env+)
            (let [new-system (system/make-system)]
              (dom/reset-canvas! 
                (system/get-env new-system)
                (system/get-renderer new-system))
              (swap! system-atom (constantly new-system)))
            (swap! system-atom system/update-env game-env+)))
      (.preventDefault key-event))))

(def request-animation-frame
  (or (. js/window -requestAnimationFrame)
      (. js/window -webkitRequestAnimationFrame)
      (. js/window -mozRequestAnimationFrame)
      #(js/setInterval % (/ 1000 60))))

(defn start
  [system-atom]
  (let [env (system/get-env @system-atom)
        renderer (system/get-renderer @system-atom)]
    (dom/init-canvas! env renderer)
    (dommy/listen! js/document :keydown (partial key-handler system-atom))
    (let [ctx (get-2d-context dom/game-canvas-id)]
      (letfn [(animate! []
                (let [renderer (system/get-renderer @system-atom)
                      env (system/get-env @system-atom)]
                  (request-animation-frame animate!)
                  (proto/render! renderer env ctx)))]
        (animate!)))))

(set! (.-onload js/window) #(start (atom (system/make-system))))
