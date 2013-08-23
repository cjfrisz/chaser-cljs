;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 22 Aug 2013
;; 
;; Entrypoint for the game.
;;----------------------------------------------------------------------

(ns chaser-cljs.core
  (:require-macros [dommy.macros :refer (node sel1)])
  (:require [dommy.core :refer (append!)]
            [chaser-cljs.coords :refer (coords-get-x coords-get-y)]
            [chaser-cljs.map-generator :refer (build-map)]))

(def map-size 15)

(def space-width 50)
(def space-height space-width)
(def space-border-width 5)
(def space-border-color "#FF0000")
(def space-interior-color "#00FFFF")

(def border-width 25)
(def canvas-height (+ (* map-size space-height) (* border-width 2)))
(def canvas-width (+ (* map-size space-width) (* border-width 2)))

(defn init-canvas
  "Initialize the game canvas."
  []
  (append! (sel1 :#game)
           (node 
            [:canvas#gameCanvas
             {:width canvas-height
              :height canvas-width
              :style "border:1px solid #000000;"}])))

(defn render-space
  [space-px-x space-px-y]
  (let [ctx (.getContext (sel1 :#gameCanvas) "2d")]
    (set! (. ctx -fillStyle) space-border-color)
    (.fillRect ctx space-px-x space-px-y space-width space-width)
    (set! (. ctx -fillStyle) space-interior-color)
    (.fillRect ctx 
      (+ space-px-x space-border-width)
      (+ space-px-y space-border-width)
      (- space-width (* space-border-width 2))
      (- space-height (* space-border-width 2)))))

(defn render-map
  [game-map]
  (letfn [(game-map-min-px [canvas-dim coord-getter]
            (/ (- canvas-dim
                  (* (reduce (fn [cur-max coord]
                               (max cur-max (coord-getter coord)))
                             0
                             game-map)
                     space-width))
               2))]
    (let [map-min-px-x (game-map-min-px canvas-width coords-get-x)
          map-min-px-y (game-map-min-px canvas-height coords-get-y)]
      (doseq [space game-map]
        (render-space
          (+ map-min-px-x (* (coords-get-x space) space-width))
          (+ map-min-px-y (* (coords-get-y space) space-width)))))))
             
(set! (.-onload js/window) 
  (fn []
    (init-canvas)
    (render-map (build-map map-size))))




