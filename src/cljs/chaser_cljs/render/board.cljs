;;----------------------------------------------------------------------
;; File board.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified  7 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.board
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.protocols :as proto]))

(defrecord+ BoardRenderer [space-width space-height 
                           space-fill-color
                           space-stroke-width space-stroke-color]
  proto/PRender
  (render! [this board context]
    (doseq [space (board/get-coord* board)
            :let [space-px-x (* (coords/get-x space) 
                                (:space-width this))
                  space-px-y (* (coords/get-y space) 
                                (:space-width this))]]
      (.beginPath context)
      (.rect context space-px-x space-px-y space-width space-width)
      (set! (. context -fillStyle) (:space-fill-color this))
      (.fill context)
      (set! (. context -lineWidth) (:space-stroke-width this))
      (set! (. context -strokeStyle) (:space-stroke-color this))
      (.stroke context))))

(let [default-space-width        50
      default-space-height       default-space-width
      default-space-fill-color   "#B0B0B0"
      default-space-stroke-width 5
      default-space-stroke-color "#000000"]
  (defn make-renderer
    ([] (make-renderer default-space-width default-space-height
          default-space-fill-color
          default-space-stroke-width default-space-stroke-color))
    ([space-width space-height 
      space-fill-color
      space-stroke-width space-stroke-color]
     (->BoardRenderer space-width space-height 
       space-fill-color
       space-stroke-width space-stroke-color))))
