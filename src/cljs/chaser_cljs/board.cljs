;;----------------------------------------------------------------------
;; File board.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 17 Sep 2013
;; 
;; In-game board representation
;;----------------------------------------------------------------------

(ns chaser-cljs.board
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.protocols :as proto]
            [chaser-cljs.room :as room]))

(defrecord+ Board [room* exit-room])

(defn make-board
  ([room*] (make-board room* nil))
  ([room* exit-room] (->Board room* exit-room)))

(defn set-exit-room
  [board exit-x exit-y]
  (loop [room* (get-room* board)
         idx 0]
    (let [[room & room-rest*] room*]
      (if (= ((juxt proto/get-x proto/get-y) room) [exit-x exit-y])
          (update-exit-room board idx)
          (recur room-rest* (inc idx))))))

;; NB: seems inconsistent to only use keyword accessor here
(defn get-exit-room [board] (get (get-room* board) (:exit-room board)))

(defn get-room
  [board target-x target-y]
  (let [room* (get-room* board)]
    (get (zipmap (map (comp (partial = [target-x target-y])
                        (juxt proto/get-x proto/get-y)) 
                   room*)
            room*)
      true)))
