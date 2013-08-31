;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified 31 Aug 2013
;; 
;; Representation for player characters.
;;----------------------------------------------------------------------

(ns chaser-cljs.player
  (:require [chaser-cljs.coords :as coords]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.protocols :as proto]))

(defn make-player
  "Creates a player at the given coordinates."
  ([coords] {:coords coords})
  ([x y] (make-player (coords/make-coords x y))))

(def get-x (comp coords/get-x :coords))
(def get-y (comp coords/get-y :coords))
(defn update-x
  [player new-x]
  (assoc player :coords (coords/update-x (:coords player) new-x)))
(defn update-y
  [player new-y]
  (assoc player :coords (coords/update-y (:coords player) new-y)))

(defrecord PlayerRenderer [fill-color stroke-color stroke-width]
  proto/PRender
  (render [this context game-env]
    (let [player (game-env/get-player game-env)
          map-renderer (game-env/get-board-renderer game-env)
          ;; NB: direct record member access alert
          space-width (:space-width map-renderer)
          half-space (/ space-width 2)
          ;; NB: direct record member access alert
          outer-border-size (:outer-border-size map-renderer)]
      (.beginPath context)
      (.arc context
        (+ (* (get-x player) space-width)
           half-space 
           outer-border-size)
        (+ (* (get-y player) space-width)
           half-space 
           outer-border-size)
        half-space
        0
        (* 2 (. js/Math -PI))
        false)
      (set! (. context -fillStyle) (:fill-color this))
      (.fill context)
      (set! (. context -lineWidth) (:stroke-width this))
      (set! (. context -strokeStyle) (:stroke-color this))
      (.stroke context))))

(let [default-fill-color "#FF0000"
      default-stroke-color "#000000"
      default-stroke-width 2]
  (defn make-renderer
    ([] (make-renderer default-fill-color
          default-stroke-color
          default-stroke-width))
    ([fill-color stroke-color stroke-width] 
     (PlayerRenderer. fill-color stroke-color stroke-width))))
     
