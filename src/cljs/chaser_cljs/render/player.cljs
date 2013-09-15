;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 14 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.player
  (:require-macros [chaser-cljs.macros
                    :refer (defrecord+ set-attributes! make-path!)])
  (:require [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]))

(defrecord+ PlayerRenderer [size
                            fill-color
                            stroke-color stroke-width]
  proto/PRender
  (render! [this player ctx]
    (let [size (:size this)]
      (doto ctx
        (.rotate (/ (* (case (player/get-dir player)
                         :down 0
                         :left 90
                         :up 180
                         :right 270)
                       (. js/Math -PI))
                    180))
        (make-path!
          (.moveTo 0 0)
          (.lineTo (- size) (- size))
          (.lineTo 0 size)
          (.lineTo size (- size)))
        (set-attributes!
          [lineWidth (:stroke-width this)
           strokeStyle (:stroke-color this)
           fillStyle (:fill-color this)])
        .fill
        .stroke))))

(let [default-size          15
      default-fill-color    "#FFF333"
      default-stroke-color  "#000000"
      default-stroke-width  2]
  (defn make-renderer
    ([] (make-renderer default-size default-fill-color
          default-stroke-color
          default-stroke-width))
    ([size fill-color stroke-color stroke-width] 
     (->PlayerRenderer size fill-color stroke-color stroke-width))))
