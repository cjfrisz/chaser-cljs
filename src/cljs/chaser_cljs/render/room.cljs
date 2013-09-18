;;----------------------------------------------------------------------
;; File room.cljs
;; Written by Chris Frisz
;; 
;; Created 11 Sep 2013
;; Last modified 15 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.room
  (:require-macros [chaser-cljs.macros
                    :refer (defrecord+ set-attributes! make-path!)])
  (:require [chaser-cljs.room :as room]
            [chaser-cljs.protocols :as proto]))

(defrecord+ RoomRenderer [width height fill-color]
  proto/PRender
  (render! [this _ ctx]
    (doto ctx
      (make-path! (.rect 0 0 (:width this) (:height this)))
      (set-attributes! [fillStyle (:fill-color this)])
      .fill)))

;; NB: width should get replaced with object's size->pixel
(let [default-width      50
      default-height     default-width
      default-fill-color "#B0B0B0"]
  (defn make-renderer
    ([] (make-renderer default-width default-height default-fill-color))
    ([fill-color] (make-renderer default-width default-height 
                    fill-color))
    ([width height fill-color] 
     (->RoomRenderer width height fill-color))))
                          
