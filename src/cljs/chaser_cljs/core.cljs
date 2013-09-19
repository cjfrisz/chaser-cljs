;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 18 Sep 2013
;; 
;; Entrypoint for the game.
;;----------------------------------------------------------------------

(ns chaser-cljs.core
  (:require-macros [chaser-cljs.macros :refer (get-2d-context)])
  (:require [dommy.core :as dommy]
            [chaser-cljs.dom :as dom]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.game :as game-render]
            [chaser-cljs.rules :as rules]
            [chaser-cljs.system :as system]))

(defn key-down!
  [system-atom key-event]
  (let [dir (case (. key-event -keyCode)
                37 :left
                38 :up
                39 :right
                40 :down
                nil)]
    (when dir
      (let [game-env (system/get-game-env @system-atom)
            player+ (rules/start-player-movement 
                      (game-env/get-player game-env)
                      dir)]
        (swap! system-atom system/update-game-env 
          (game-env/update-player game-env player+)))
      (.preventDefault key-event))))

(defn key-up! 
  [system-atom _]
  (let [game-env (system/get-game-env @system-atom)]
    (swap! system-atom 
      (constantly 
       (system/update-game-env @system-atom
         (game-env/update-player game-env
           (-> (game-env/get-player game-env)
             (player/update-moving? false)
             (player/update-cur-speed 0))))))))
  

(def request-animation-frame
  (or (. js/window -requestAnimationFrame)
      (. js/window -webkitRequestAnimationFrame)
      (. js/window -mozRequestAnimationFrame)
      #(js/setInterval % (/ 1000 60))))

(defn start
  [system-atom]
  (let [game-env (system/get-game-env @system-atom)
        renderer (system/get-renderer @system-atom)]
    (dom/init-canvas! game-env renderer)
    (dommy/listen! js/document :keydown (partial key-down! system-atom))
    (dommy/listen! js/document :keyup (partial key-up! system-atom))
    (let [ctx (get-2d-context dom/game-canvas-id)]
      (letfn [(animate! []
                (request-animation-frame animate!)
                (rules/update-game! system-atom)
                (proto/render! (system/get-renderer @system-atom)
                  (system/get-game-env @system-atom)
                  ctx))]
        (animate!)))))

(set! (.-onload js/window) #(start (atom (system/make-system))))
