;;----------------------------------------------------------------------
;; File game.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 16 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.game
  (:require-macros [chaser-cljs.macros
                    :refer (defrecord+ with-protected-context)])
  (:require [chaser-cljs.game-env :as game-env]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.board :as board-render]
            [chaser-cljs.render.player :as player-render]
            [chaser-cljs.render.room :as room-render]))

(defrecord+ GameRenderer [board-renderer player-renderer
                          border-width border-height]
  proto/PRender
  (render! [this game-env ctx]
    (with-protected-context ctx
      (.translate ctx (:border-width this) (:border-height this))
      (let [board-renderer (:board-renderer this)
            ;; NB: didn't stop being gross since it showed up in dom
            room-renderer (first (board-render/get-room-renderer*
                                   board-renderer))
            ;; NB: this will eventually be more complicated
            space-width (room-render/get-width room-renderer)
            space-height (room-render/get-height room-renderer)]
        (with-protected-context ctx
          (proto/render! board-renderer 
            (game-env/get-board game-env)
            ctx))
        (let [player (game-env/get-player game-env)]
          (with-protected-context ctx
            (.translate ctx 
              (+ (proto/get-x player) (/ space-width 2))
              (+ (proto/get-y player) (/ space-height 2)))
            (proto/render! (:player-renderer this) player ctx)))))))

(let [default-border-width  50
      default-border-height default-border-width]
  (defn make-game-renderer 
    ([board-renderer] (make-game-renderer
                        board-renderer
                        (player-render/make-renderer)
                        default-border-width
                        default-border-height))
    ([board-renderer player-renderer border-width border-height]
     (->GameRenderer board-renderer player-renderer
       border-width 
       border-height))))
