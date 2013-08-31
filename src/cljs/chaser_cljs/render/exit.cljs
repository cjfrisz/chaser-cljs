;;----------------------------------------------------------------------
;; File exit.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 31 Aug 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.exit
  (:require [chaser-cljs.exit :as exit]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.protocols :as proto]))

;; NB: shamelessly copy/pasted from player
(defrecord ExitRenderer [fill-color stroke-color stroke-width]
  proto/PRender
  (render [this context game-env]
    ;; NB: pulling out the associate object this way prevents directly
    ;;     reusing the renderer for multiple object types
    (let [exit (game-env/get-exit game-env)
          map-renderer (game-env/get-board-renderer game-env)
          ;; NB: direct record member access alert
          space-width (:space-width map-renderer)
          half-space (/ space-width 2)
          ;; NB: direct record member access alert
          outer-border-size (:outer-border-size map-renderer)]
      (.beginPath context)
      (.arc context
        (+ (* (exit/get-x exit) space-width)
           half-space 
           outer-border-size)
        (+ (* (exit/get-y exit) space-width)
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

(let [default-fill-color "green"
      default-stroke-color "#000000"
      default-stroke-width 2]
  (defn make-renderer
    ([] (make-renderer default-fill-color
          default-stroke-color
          default-stroke-width))
    ([fill-color stroke-color stroke-width] 
     (ExitRenderer. fill-color stroke-color stroke-width))))
