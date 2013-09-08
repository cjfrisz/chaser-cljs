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
  (:require-macros [chaser-cljs.macros 
                    :refer (defrecord+ set-attributes! make-path!)])
  (:require [chaser-cljs.protocols :as proto]))

(defrecord+ ExitRenderer [width
                          fill-color
                          stroke-color stroke-width]
  proto/PRender
  (render! [this exit ctx]
      (let [width (:width this)]
        (doto ctx
          (make-path!
            (.rect (/ (- width) 2) (/ (- width) 2) width width))
          (set-attributes!
            [fillStyle (:fill-color this)
             lineWidth (:stroke-width this)
             strokeStyle (:stroke-color this)])
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
