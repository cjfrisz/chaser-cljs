;;----------------------------------------------------------------------
;; File board.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 31 Aug 2013
;; 
;; In-game board representation
;;----------------------------------------------------------------------

(ns chaser-cljs.board
  (:require [chaser-cljs.coords :as coords]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.protocols :as proto]))

(defn get-space
  [board target-x target-y]
  (some #(and (= (coords/get-x %) target-x) 
              (= (coords/get-y %) target-y))
    board))
