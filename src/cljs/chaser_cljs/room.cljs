;;----------------------------------------------------------------------
;; File room.cljs
;; Written by Chris Frisz
;; 
;; Created 11 Sep 2013
;; Last modified 20 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.room
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.protocols :as proto]))

(defrecord+ Room [x y width height
                  top-wall
                  right-wall
                  bottom-wall
                  left-wall]
  proto/PCoords
  (get-x [room] (:x room))
  (get-y [room] (:y room))
  (update-x [room new-x] (assoc room :x new-x))
  (update-y [room new-y] (assoc room :y new-y)))

(defn make-room
  ([x y width height] 
   (make-room x y width height false true false false))
  ([x y width height
    top-wall
    right-wall
    bottom-wall
    left-wall]
   (->Room x y width height top-wall right-wall bottom-wall left-wall)))
