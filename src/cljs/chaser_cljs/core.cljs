;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 10 Sep 2013
;; 
;; Entrypoint for the game.
;;----------------------------------------------------------------------

(ns chaser-cljs.core
  (:require-macros [chaser-cljs.macros :refer (get-2d-context)])
  (:require [dommy.core :as dommy]
            [chaser-cljs.dom :as dom]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.game :as game-render]))

;; NB: cute, but gross; should communicate key event to game-env
(defn key-handler
  [game-env-atom game-renderer]
  (fn [key-event]
    (let [dir (case (. key-event -keyCode)
                37 :left
                38 :up
                39 :right
                40 :down
                nil)]
      (when dir
        (swap! game-env-atom
          (fn [game-env]
            (as-> (game-env/get-player game-env) player
              (game-env/move-player player dir 
                (game-env/get-board game-env))
              (player/update-dir player dir)
              (let [exit (game-env/get-exit game-env)]
                (if (and (= (player/get-x player) (exit/get-x exit))
                         (= (player/get-y player) (exit/get-y exit)))
                    (let [new-game-env (game-env/make-fresh-game-env)]
                      (dom/reset-canvas! new-game-env
                        game-renderer)
                      (swap! game-env-atom (constantly new-game-env)))
                    (game-env/update-player game-env player))))))
        (.preventDefault key-event)))))

(def request-animation-frame
  (or (. js/window -requestAnimationFrame)
      (. js/window -webkitRequestAnimationFrame)
      (. js/window -mozRequestAnimationFrame)
      #(js/setInterval % (/ 1000 60))))

(let [game-env (atom (game-env/make-fresh-game-env))
      game-renderer (game-render/make-game-renderer)]
  (set! (.-onload js/window) 
    (fn []
      (dom/init-canvas! @game-env game-renderer)
      (dommy/listen! js/document :keydown 
        (key-handler game-env game-renderer))
      (let [ctx (get-2d-context dom/game-canvas-id)]
        (letfn [(animate []
                  (request-animation-frame animate)
                  (proto/render! game-renderer @game-env ctx))]
          (animate))))))
