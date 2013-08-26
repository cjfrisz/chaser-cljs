;;----------------------------------------------------------------------
;; File game_params.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 25 Aug 2013
;; 
;; Data structure containing game parameter information.
;;----------------------------------------------------------------------

(ns chaser-cljs.game-params)

(def default-map-size 15)

(def default-space-width 50)
(def default-space-height default-space-width)
(def default-space-border-width 5)
(def default-space-border-color "#000000")
(def default-space-interior-color "#B0B0B0")

(def default-canvas-border-width default-space-width)

(def default-player-color "#FF0000")
(def default-player-border-color "#000000")
(def default-player-border-width 2)

(defn make-game-params []
  {:map-size 15
   :space-width 50
   :space-height default-space-width
   :space-border-width 5
   :space-border-color "#000000"
   :space-interior-color "#B0B0B0"
   :canvas-border-width default-space-width
   :player-color "#FF0000"
   :player-border-color "#000000"
   :player-border-width 2})
   
(def game-params-get-param get)

(defn game-params-update-param
  [game-params param new-val]
  (when (contains? game-params param)
    (assoc game-params param new-val)))
  
