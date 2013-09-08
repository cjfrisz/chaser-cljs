;;----------------------------------------------------------------------
;; File exit.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified  7 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.exit
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.protocols :as proto]))

(defrecord+ ExitRenderer [width
                          fill-color
                          stroke-color stroke-width]
  proto/PRender
  (render! [this exit ctx]
      (set! (. ctx -fillStyle) (:fill-color this))
      (set! (. ctx -lineWidth) (:stroke-width this))
      (set! (. ctx -strokeStyle) (:stroke-color this))
      (let [width (:width this)]
        (doto ctx
          .beginPath
          (.rect (/ (- width) 2) (/ (- width) 2) width width)
          .closePath
          .fill
          .stroke))))

(let [default-width        50
      default-fill-color   "#669900"
      default-stroke-color "#000000"
      default-stroke-width 5]
  (defn make-renderer
    ([] (make-renderer default-width
          default-fill-color
          default-stroke-color
          default-stroke-width))
    ([width fill-color stroke-color stroke-width] 
     (->ExitRenderer width fill-color stroke-color stroke-width))))
