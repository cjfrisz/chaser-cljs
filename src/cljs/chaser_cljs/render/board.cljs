;;----------------------------------------------------------------------
;; File board.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 17 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.board
  (:require-macros [chaser-cljs.macros
                    :refer (defrecord+ with-protected-context)])
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.room :as room-render]
            [chaser-cljs.room :as room]))

(defrecord+ BoardRenderer [room-renderer*]
  proto/PRender
  (render! [this board ctx]
    (doseq [[room renderer] (map vector
                              (board/get-room* board)
                              (:room-renderer* this))]
      (with-protected-context ctx
        (.translate ctx (proto/get-x room) (proto/get-y room))
        (proto/render! renderer room ctx)))))

(let [default-exit-fill-color "#669900"]
  (defn make-renderer 
    [board]
    (let [exit-room (board/get-exit-room board)]
      (->BoardRenderer
        (mapv (fn [room]
                (if (= room exit-room)
                    (room-render/make-renderer default-exit-fill-color)
                    (room-render/make-renderer)))
          (board/get-room* board))))))
