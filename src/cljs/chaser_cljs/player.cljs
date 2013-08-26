;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified 25 Aug 2013
;; 
;; Representation for player characters.
;;----------------------------------------------------------------------

(ns chaser-cljs.player
  (:require [chaser-cljs.coords
             :refer (make-coords 
                     coords-get-x coords-get-y
                     coords-update-x coords-update-y)]))

(defn make-player
  "Creates a player at the given coordinates."
  ([coords] {:coords coords})
  ([x y] (make-player (make-coords x y))))

(def player-get-x (comp coords-get-x :coords))
(def player-get-y (comp coords-get-y :coords))
(defn player-update-x
  [player new-x]
  (assoc player :coords (coords-update-x (:coords player) new-x)))
(defn player-update-y
  [player new-y]
  (assoc player :coords (coords-update-y (:coords player) new-y)))

