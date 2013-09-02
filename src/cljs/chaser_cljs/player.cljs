;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified  2 Sep 2013
;; 
;; Representation for player characters.
;;----------------------------------------------------------------------

(ns chaser-cljs.player
  (:require-macros [cljs.core.typed :refer (ann def-alias)])
  (:require [chaser-cljs.coords :as coords]))

(def-alias PlayerT (HMap :mandatory {:coords CoordsT}
                         :complete? true))
(ann make-player (Fn [CoordsT -> PlayerT]
                     [number number -> PlayerT]))
(defn make-player
  "Creates a player at the given coordinates."
  ([coords] {:coords coords})
  ([x y] (make-player (coords/make-coords x y))))

(ann get-x [CoordsT -> number])
(def get-x (comp coords/get-x :coords))

(ann get-y [CoordsT -> number])
(def get-y (comp coords/get-y :coords))

(ann update-x [PlayerT number -> PlayerT])
(defn update-x
  [player new-x]
  (assoc player :coords (coords/update-x (:coords player) new-x)))

(ann update-y [PlayerT number -> PlayerT])
(defn update-y
  [player new-y]
  (assoc player :coords (coords/update-y (:coords player) new-y)))
     
