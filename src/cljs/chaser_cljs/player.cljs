;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified  3 Sep 2013
;; 
;; Representation for player characters.
;;----------------------------------------------------------------------

(ns chaser-cljs.player
  (:require [chaser-cljs.coords :as coords]))

(defn make-player
  "Creates a player at the given coordinates."
  ([coords] (make-player coords :up))
  ([coords dir] {:coords coords
                 :dir dir}))

(def get-x (comp coords/get-x :coords))
(def get-y (comp coords/get-y :coords))
(def get-dir :dir)
(defn update-x
  [player new-x]
  (assoc player :coords (coords/update-x (:coords player) new-x)))
(defn update-y
  [player new-y]
  (assoc player :coords (coords/update-y (:coords player) new-y)))
(defn update-dir [player new-dir] (assoc player :dir new-dir))
     
