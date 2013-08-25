;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified 24 Aug 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.player)

(defn make-player
  "Creates a player at the given coordinates."
  [coords]
  {:coords coords})

(def player-get-coords :coords)
(defn player-update-coords 
  [player new-coords]
  (assoc player :coords new-coords))
