;;----------------------------------------------------------------------
;; File exit.cljs
;; Written by Chris Frisz
;; 
;; Created 29 Aug 2013
;; Last modified 29 Aug 2013
;; 
;; Represents the map exit for getting to a new floor
;;----------------------------------------------------------------------

(ns chaser-cljs.exit
  ;; NB: shamelessly stealing the represenation from the player for now
  (:require [chaser-cljs.game-env :as genv]
            [chaser-cljs.player :refer (make-player
                                        player-get-x player-get-y
                                        player-update-x player-update-y)]
            [chaser-cljs.protocols :refer (PRender)]))

(def make-exit make-player)

(def exit-get-x player-get-x)
(def exit-get-y player-get-y)

(def exit-update-x player-update-x)
(def exit-update-y player-update-y)

;; NB: shamelessly copy/pasted from player
(defrecord ExitRenderer [fill-color stroke-color stroke-width]
  PRender
  (render [this context game-env]
    ;; NB: pulling out the associate object this way prevents directly
    ;;     reusing the renderer for multiple object types
    (let [exit (genv/game-env-get-exit game-env)
          map-renderer (genv/game-env-get-game-map-renderer game-env)
          ;; NB: direct record member access alert
          space-width (:space-width map-renderer)
          half-space (/ space-width 2)
          ;; NB: direct record member access alert
          outer-border-size (:outer-border-size map-renderer)]
      (.beginPath context)
      (.arc context
        (+ (* (exit-get-x exit) space-width)
           half-space 
           outer-border-size)
        (+ (* (exit-get-y exit) space-width)
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

(let [default-exit-fill-color "green"
      default-exit-stroke-color "#000000"
      default-exit-stroke-width 2]
  (defn make-exit-renderer
    ([] (make-exit-renderer default-exit-fill-color
          default-exit-stroke-color
          default-exit-stroke-width))
    ([fill-color stroke-color stroke-width] 
     (ExitRenderer. fill-color stroke-color stroke-width))))
