;;----------------------------------------------------------------------
;; File game.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified  1 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.game
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.board :as board-render]
            [chaser-cljs.render.exit :as exit-render]
            [chaser-cljs.render.player :as player-render]))

(defrecord GameRenderer [board-renderer exit-renderer player-renderer
                         border-width border-height]
  proto/PRender
  (render! [this game-env ctx]
    (.save ctx)
    (.translate ctx (:border-width this) (:border-height this))
    (let [space-width (board-render/get-space-width 
                        (:board-renderer this))
          space-height (board-render/get-space-height
                         (:board-renderer this))]
      (.save ctx)
      (proto/render! (:board-renderer this) 
        (game-env/get-board game-env)
        ctx)
      (.restore ctx)
      ;; NB: exit/player rendering very similar; either comonize into a
      ;;     helper or optimize rendering by translating with respect to
      ;;     the other (probably dangerous).
      (let [exit (game-env/get-exit game-env)]
        (.save ctx)
        (.translate ctx 
          (* space-width (exit/get-x exit))
          (* space-height (exit/get-y exit)))
        (proto/render! (:exit-renderer this) exit ctx)
        (.restore ctx))
      (let [player (game-env/get-player game-env)]
        (.save ctx)
        (.translate ctx 
          (* space-width (player/get-x player))
          (* space-height (player/get-y player)))
        (proto/render! (:player-renderer this) player ctx)
        (.restore ctx)))
    (.restore ctx)))

(let [default-border-width  50
      default-border-height default-border-width]
  (defn make-game-renderer 
    ([] (make-game-renderer 
          (board-render/make-renderer)
          (exit-render/make-renderer)
          (player-render/make-renderer)
          default-border-width
          default-border-height))
    ([board-renderer exit-renderer player-renderer
      border-width border-height]
     (GameRenderer. board-renderer exit-renderer player-renderer
       border-width border-height))))
