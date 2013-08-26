;;----------------------------------------------------------------------
;; File game_map.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 25 Aug 2013
;; 
;; In-game map representation
;;----------------------------------------------------------------------

(ns chaser-cljs.game-map
  (:require [chaser-cljs.coords :refer (coords-get-x coords-get-y)]
            [chaser-cljs.js-utils :refer (get-2d-context)]))

;; NB: push these out to params.cljs
(def space-width 50)
(def space-height space-width)
(def space-border-width 5)
(def space-border-color "#000000")
(def space-interior-color "#B0B0B0")
(def canvas-border-width space-width)

(defn game-map-get-space
  [game-map target-x target-y]
  (some #(and (= (coords-get-x %) target-x) 
              (= (coords-get-y %) target-y))
    game-map))

(defn render-space
  [space-px-x space-px-y]
  (let [ctx (get-2d-context)]
    (.beginPath ctx)
    (.rect ctx space-px-x space-px-y space-width space-width)
    (set! (. ctx -fillStyle) space-interior-color)
    (.fill ctx)
    (set! (. ctx -lineWidth) space-border-width)
    (set! (. ctx -strokeStyle) space-border-color)
    (.stroke ctx)))

(defn render-map
  [game-map]
  (doseq [space game-map]
    (render-space
     (+ canvas-border-width (* (coords-get-x space) space-width))
     (+ canvas-border-width (* (coords-get-y space) space-width)))))
