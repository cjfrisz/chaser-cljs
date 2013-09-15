;;----------------------------------------------------------------------
;; File game.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 14 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.game
  (:require-macros [chaser-cljs.macros
                    :refer (defrecord+ with-protected-context)])
  (:require [chaser-cljs.exit :as exit]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.board :as board-render]
            [chaser-cljs.render.exit :as exit-render]
            [chaser-cljs.render.player :as player-render]
            [chaser-cljs.render.room :as room-render]))

(defrecord+ GameRenderer [board-renderer exit-renderer player-renderer
                          border-width border-height]
  proto/PRender
  (render! [this game-env ctx]
    (with-protected-context ctx
      (.translate ctx (:border-width this) (:border-height this))
      (let [board-renderer (:board-renderer this)
            room-renderer (board-render/get-room-renderer 
                            board-renderer)
            ;; NB: this will eventually be more complicated
            space-width (room-render/get-width room-renderer)
            space-height (room-render/get-height room-renderer)]
        (with-protected-context ctx
          (proto/render! board-renderer 
            (game-env/get-board game-env)
            ctx))
        (let [exit (game-env/get-exit game-env)]
          (with-protected-context ctx
            (.translate ctx 
              (+ (exit/get-x exit) (/ space-width 2))
              (+ (exit/get-y exit) (/ space-height 2)))
            (proto/render! (:exit-renderer this) exit ctx)))
        (let [player (game-env/get-player game-env)]
          (with-protected-context ctx
            (.translate ctx 
              (+ (player/get-x player) (/ space-width 2))
              (+ (player/get-y player) (/ space-height 2)))
            (proto/render! (:player-renderer this) player ctx)))))))

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
     (->GameRenderer board-renderer exit-renderer player-renderer
       border-width border-height))))
