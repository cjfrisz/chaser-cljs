;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified  7 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.player
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]))

;; NB: nasty hack to support nasty hack of shared player/exit renderer
(declare PlayerRenderer)

;; NB: lifted for gross sharing with chaser-cljs.render.exit
(defn render-player+exit!
  [this target ctx]
  (let [radius (:radius this)]
    ;; draw circle 
    (.beginPath ctx)
    (.arc ctx radius radius radius 0 (* 2 (. js/Math -PI))
      false)
    (set! (. ctx -fillStyle) (:fill-color this))
    (.fill ctx)
    (set! (. ctx -lineWidth) (:stroke-width this))
    (set! (. ctx -strokeStyle) (:stroke-color this))
    (.stroke ctx)
    ;; draw pointer
    (when (instance? PlayerRenderer this)
      (let [dir (:dir target)]
        (.translate ctx radius radius)
        (.rotate ctx 
          (/ (* (case (:dir target)
                  :up 0
                  :right 90
                  :down 180
                  :left 270)
                (. js/Math -PI))
             180))
        (.beginPath ctx)
        (.arc ctx 0 0 radius 
          (* (. js/Math -PI) 1.25)
          (* (. js/Math -PI) 1.75))
        
                (.moveTo ctx 0 0)
        (let [length (* (:radius this) (/ (.sqrt js/Math 2) 2))]
          (.lineTo ctx length (- length))
          (.lineTo ctx (- length) (- length)))
        (set! (. ctx -strokeStyle) (:pointer-color this))        
        (set! (. ctx -fillStyle) (:pointer-color this))
        (.stroke ctx)
        (.fill ctx)))))

(defrecord+ PlayerRenderer [radius
                            fill-color
                            stroke-color stroke-width
                            pointer-color pointer-width]
  proto/PRender
  (render! [this player ctx]
    (render-player+exit! this player ctx)))

(let [default-radius        25
      default-fill-color    "#FFF333"
      default-stroke-color  "#000000"
      default-stroke-width  2
      default-pointer-color "#B0B0B0"
      default-pointer-width 5]
  (defn make-renderer
    ([] (make-renderer default-radius default-fill-color
          default-stroke-color
          default-stroke-width
          default-pointer-color
          default-pointer-width))
    ([radius fill-color stroke-color stroke-width pointer-color 
      pointer-width] 
     (PlayerRenderer. radius fill-color stroke-color stroke-width 
       pointer-color
       pointer-width))))
