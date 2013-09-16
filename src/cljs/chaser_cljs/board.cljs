;;----------------------------------------------------------------------
;; File board.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 15 Sep 2013
;; 
;; In-game board representation
;;----------------------------------------------------------------------

(ns chaser-cljs.board
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.protocols :as proto]))

(defrecord+ Board [room* room-map size])

(defn make-board
  [room*]
  (->Board room*
    (reduce (fn [room-map room]
              (assoc-in room-map 
                ((juxt proto/get-x proto/get-y) room)
                room))
      {}
      room*)
    (count room*)))

(defn get-space
  [board target-x target-y]
  (get-in (:room-map board) [target-x target-y]))
