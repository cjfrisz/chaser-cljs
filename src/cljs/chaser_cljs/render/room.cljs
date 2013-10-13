;;----------------------------------------------------------------------
;; File room.cljs
;; Written by Chris Frisz
;; 
;; Created 11 Sep 2013
;; Last modified 20 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.room
  (:require-macros [chaser-cljs.macros
                    :refer (defrecord+ set-attributes! make-path!)])
  (:require [chaser-cljs.room :as room]
            [chaser-cljs.protocols :as proto]))

(defrecord+ RoomRenderer [width height fill-color
                          wall-color
                          wall-width]
  proto/PRender
  (render! [this room ctx]
    (doto ctx
      (make-path! (.rect 0 0 (:width this) (:height this)))
      (set-attributes! [fillStyle (:fill-color this)])
      .fill)
    (set-attributes! ctx
      [lineWidth (:wall-width this)
       strokeStyle (:wall-color this)])
    ;; NB: find the abstraction here
    (when (room/get-top-wall room)
      (doto ctx
        (make-path!
         (.moveTo 0 (:height this))
         (.lineTo (:width this) (:height this)))
        .stroke))
    (when (room/get-right-wall room)
      (doto ctx
        (make-path!
         (.moveTo (:width this) (:height this))
         (.lineTo (:width this) 0))
        .stroke))
    (when (room/get-bottom-wall room)
      (doto ctx
        (make-path!
         (.moveTo (:width this) 0)
         (.lineTo 0 0))
        .stroke))
    (when (room/get-left-wall room)
      (doto ctx
        (make-path!
         (.moveTo 0 0)
         (.lineTo 0 (:height this)))
        .stroke))))

;; NB: width should get replaced with object's size->pixel
(let [default-width      50
      default-height     default-width
      default-fill-color "#B0B0B0"
      default-wall-color "#000000"
      default-wall-width 1]
  (defn make-renderer
    ([] (make-renderer default-width default-height default-fill-color
          default-wall-color
          default-wall-width))
    ([fill-color] (make-renderer default-width default-height 
                    fill-color
                    default-wall-color
                    default-wall-width))
    ([width height fill-color wall-color wall-width] 
     (->RoomRenderer width height fill-color wall-color wall-width))))
                          
