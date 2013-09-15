;;----------------------------------------------------------------------
;; File board.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 14 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.board
  (:require-macros [chaser-cljs.macros
                    :refer (defrecord+ with-protected-context)])
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.room :as room-render]))

(defrecord+ BoardRenderer [room-renderer]
  proto/PRender
  (render! [this board ctx]
    (doseq [room (board/get-room* board)]
      (with-protected-context ctx
        (.translate ctx (proto/get-x room) (proto/get-y room))
        (proto/render! (:room-renderer this) room ctx)))))

;; NB: totally cheating because all room rendered the same
(defn make-renderer [] (->BoardRenderer (room-render/make-renderer)))
