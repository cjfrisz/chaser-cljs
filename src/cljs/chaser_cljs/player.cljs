;;----------------------------------------------------------------------
;; File player.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified 18 Sep 2013
;; 
;; Representation for player characters.
;;----------------------------------------------------------------------

(ns chaser-cljs.player
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.protocols :as proto]))

(defrecord+ Player [x y dir moving? cur-speed top-speed]
  proto/PCoords
  (get-x [player] (:x player))
  (get-y [player] (:y player))
  (update-x [player new-x] (assoc player :x new-x))
  (update-y [player new-y] (assoc player :y new-y)))

(defn update-cur-speed
  [player new-speed]
  (assoc player :cur-speed (min new-speed (:top-speed player))))

(defn make-player
  "Creates a player at the given coordinates."
  ([x y] (make-player x y :up false 0 5))
  ([x y dir moving? cur-speed top-speed] 
   (->Player x y dir moving? cur-speed top-speed)))
