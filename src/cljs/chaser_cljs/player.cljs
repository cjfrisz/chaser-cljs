;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified 28 Aug 2013
;; 
;; Representation for player characters.
;;----------------------------------------------------------------------

(ns chaser-cljs.player
  (:require [chaser-cljs.coords :refer (make-coords 
                                        coords-get-x
                                        coords-get-y
                                        coords-update-x
                                        coords-update-y)]
            [chaser-cljs.game-env :refer (game-env-get-game-map-renderer
                                          game-env-get-player)]
            [chaser-cljs.game-map :refer (get-outer-border-size)]
            [chaser-cljs.protocols :refer (PRender)]))

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

(defrecord PlayerRenderer [fill-color stroke-color stroke-width]
  PRender
  (render [this context game-env]
    (let [player (game-env-get-player game-env)
          map-renderer (game-env-get-game-map-renderer game-env)
          ;; NB: direct record member access alert
          space-width (:space-width map-renderer)
          half-space (/ space-width 2)
          ;; NB: direct record member access alert
          outer-border-size (:outer-border-size map-renderer)]
      (.beginPath context)
      (.arc context
        (+ (* (player-get-x player) space-width)
           half-space 
           outer-border-size)
        (+ (* (player-get-y player) space-width)
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

(let [default-player-fill-color "#FF0000"
      default-player-stroke-color "#000000"
      default-player-stroke-width 2]
  (defn make-player-renderer
    ([] (make-player-renderer default-player-fill-color
          default-player-stroke-color
          default-player-stroke-width))
    ([fill-color stroke-color stroke-width] 
     (PlayerRenderer. fill-color stroke-color stroke-width))))
     
