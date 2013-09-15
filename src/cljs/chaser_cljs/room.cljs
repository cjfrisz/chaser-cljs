;;----------------------------------------------------------------------
;; File room.cljs
;; Written by Chris Frisz
;; 
;; Created 11 Sep 2013
;; Last modified 14 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.room
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.protocols :as proto]))

(defrecord+ Room [x-coord y-coord width height]
  proto/PCoords
  (get-x [room] (get room :x-coord))
  (get-y [room] (:y-coord room))
  (update-x [room new-x] (assoc room :x new-x))
  (update-y [room new-y] (assoc room :y new-y)))

(defn make-room [x y width height] (->Room x y width height))
