;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 24 Aug 2013
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
(def space-border-color "#000000")
(def space-interior-color "#B0B0B0")

(def canvas-border-width space-width)

(defn init-canvas
  "Initialize the game canvas."
  [game-map]
  (letfn [(game-map-max-coord [coord-getter]
            (reduce (fn [cur-max coord]
                      (max cur-max (coord-getter coord)))
              0
              game-map))
          (dimension-pxs [max-coord]
            (+ (* (inc max-coord) space-width) 
               (* canvas-border-width 2)))]
    (let [max-x (game-map-max-coord coords-get-x)
          max-y (game-map-max-coord coords-get-y)]
      (append! (sel1 :#game)
        (node 
         [:canvas#gameCanvas
          {:width (dimension-pxs max-x)
           :height (dimension-pxs max-y)
           :style "border:1px solid #000000;"}])))))

(defn render-space
  [space-px-x space-px-y]
  (let [ctx (.getContext (sel1 :#gameCanvas) "2d")]
    (. ctx beginPath)
    (.rect ctx space-px-x space-px-y space-width space-width)
    (set! (. ctx -fillStyle) space-interior-color)
    (. ctx fill)
    (set! (. ctx -lineWidth) space-border-width)
    (set! (. ctx -strokeStyle) space-border-color)
    (. ctx stroke)))

(defn render-map
  [game-map]
  (doseq [space game-map]
    (render-space
     (+ canvas-border-width (* (coords-get-x space) space-width))
     (+ canvas-border-width (* (coords-get-y space) space-width)))))
             
(set! (.-onload js/window) 
  (fn []
    (let [game-map (build-map map-size)]
      (init-canvas game-map)
      (render-map game-map))))




