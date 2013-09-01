;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 31 Aug 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.player
  (:require [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]))

;; NB: lifted for gross sharing with chaser-cljs.render.exit
(defn render-player+exit!
  [this player context]
  ;; NB: these goes away when rendering uses matrix transforms
  (let [space-width 50
        outer-border-size 50]
      (.beginPath context)
      (.arc context
        (+ (* (player/get-x player) space-width)
           (:radius this)
           outer-border-size)
        (+ (* (player/get-y player) space-width)
           (:radius this)
           outer-border-size)
        (:radius this)
        0
        (* 2 (. js/Math -PI))
        false)
      (set! (. context -fillStyle) (:fill-color this))
      (.fill context)
      (set! (. context -lineWidth) (:stroke-width this))
      (set! (. context -strokeStyle) (:stroke-color this))
      (.stroke context)))

(defrecord PlayerRenderer [radius
                           fill-color
                           stroke-color stroke-width]
  proto/PRender
  (render! [this player context]
    (render-player+exit! this player context)))

(let [default-radius       25
      default-fill-color   "#FF0000"
      default-stroke-color "#000000"
      default-stroke-width 2]
  (defn make-renderer
    ([] (make-renderer default-radius default-fill-color
          default-stroke-color
          default-stroke-width))
    ([radius fill-color stroke-color stroke-width] 
     (PlayerRenderer. radius fill-color stroke-color stroke-width))))
