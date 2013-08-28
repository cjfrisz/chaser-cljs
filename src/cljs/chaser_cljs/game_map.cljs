;;----------------------------------------------------------------------
;; File game_map.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 28 Aug 2013
;; 
;; In-game map representation
;;----------------------------------------------------------------------

(ns chaser-cljs.game-map
  (:require [chaser-cljs.coords :refer (coords-get-x coords-get-y)]
            [chaser-cljs.game-env :refer (game-env-get-game-map)]
            [chaser-cljs.protocols :refer (PRender)]))

(defn game-map-get-space
  [game-map target-x target-y]
  (some #(and (= (coords-get-x %) target-x) 
              (= (coords-get-y %) target-y))
    game-map))

(defrecord GameMapRenderer [space-width space-height 
                            space-fill-color
                            space-stroke-width space-stroke-color
                            outer-border-size]
  PRender
  (render [this context game-env]
    (let [game-map (game-env-get-game-map game-env)]
      (doseq [space game-map
              :let [space-px-x (+ (:outer-border-size this)
                                  (* (coords-get-x space) 
                                     (:space-width this)))
                    space-px-y (+ (:outer-border-size this) 
                                  (* (coords-get-y space) 
                                     (:space-width this)))]]
        (.beginPath context)
        (.rect context space-px-x space-px-y space-width space-width)
        (set! (. context -fillStyle) (:space-fill-color this))
        (.fill context)
        (set! (. context -lineWidth) (:space-stroke-width this))
        (set! (. context -strokeStyle) (:space-stroke-color this))
        (.stroke context)))))

(let [default-space-width        50
      default-space-height       default-space-width
      default-space-fill-color   "#B0B0B0"
      default-space-stroke-width 5
      default-space-stroke-color "#000000"
      default-outer-border-size  default-space-width]
  (defn make-game-map-renderer
    ([] (make-game-map-renderer default-space-width default-space-height
          default-space-fill-color
          default-space-stroke-width default-space-stroke-color
          default-outer-border-size))
    ([space-width space-height 
      space-fill-color
      space-stroke-width space-stroke-color
      outer-border-size]
     (GameMapRenderer. space-width space-height 
       space-fill-color
       space-stroke-width space-stroke-color
       outer-border-size))))
