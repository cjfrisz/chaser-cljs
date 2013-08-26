;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified 25 Aug 2013
;; 
;; Representation for player characters.
;;----------------------------------------------------------------------

(ns chaser-cljs.player
  (:require [chaser-cljs.coords
             :refer (make-coords 
                     coords-get-x coords-get-y
                     coords-update-x coords-update-y)]
            [chaser-cljs.js-utils :refer (get-2d-context)]))

;; NB: pull these out to params.cljs
(def player-color "#FF0000")
(def player-border-color "#000000")
(def player-border-width 2)
;; NB: COPIES!!!
(def space-width 50)
(def canvas-border-width space-width)

(defn make-player
  "Creates a player at the given coordinates."
  ([coords] {:coords coords})
  ([x y] (make-player (make-coords x y))))

(def player-get-x (comp coords-get-x :coords))
(def player-get-y (comp coords-get-y :coords))
(defn player-update-x
  [player new-x]
  (assoc player :coords (coords-update-x (:coords player) new-x)))
(defn player-update-y
  [player new-y]
  (assoc player :coords (coords-update-y (:coords player) new-y)))

(defn render-player
  [player]
  ;; NB: grabbing canvas context should be a helper
  (let [ctx (get-2d-context)
        half-space (/ space-width 2)]
    (.beginPath ctx)
    (.arc ctx
      (+ (* (player-get-x player) space-width)
         half-space 
         canvas-border-width)
      (+ (* (player-get-y player) space-width)
         half-space 
         canvas-border-width)
      half-space
      0
      (* 2 (. js/Math -PI))
      false)
    (set! (. ctx -fillStyle) player-color)
    (.fill ctx)
    (set! (. ctx -lineWidth) player-border-width)
    (set! (. ctx -strokeStyle) player-border-color)
    (.stroke ctx)))
