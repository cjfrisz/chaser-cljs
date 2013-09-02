;;----------------------------------------------------------------------
;; File exit.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified  1 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.exit
  (:require [chaser-cljs.protocols :as proto]
            ;; NB: required for shameless copying
            [chaser-cljs.render.player :as player-render]))

;; NB: shamelessly copy/pasted from player
(defrecord ExitRenderer [radius
                         fill-color
                         stroke-color stroke-width]
  proto/PRender
  (render! [this exit context]
    (player-render/render-player+exit! this exit context)))

(let [default-radius       25
      default-fill-color   "#669900"
      default-stroke-color "#000000"
      default-stroke-width 2]
  (defn make-renderer
    ([] (make-renderer default-radius
          default-fill-color
          default-stroke-color
          default-stroke-width))
    ([radius fill-color stroke-color stroke-width] 
     (ExitRenderer. radius fill-color stroke-color stroke-width))))
