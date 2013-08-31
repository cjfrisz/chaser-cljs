;;----------------------------------------------------------------------
;; File exit.cljs
;; Written by Chris Frisz
;; 
;; Created 29 Aug 2013
;; Last modified 31 Aug 2013
;; 
;; Represents the map exit for getting to a new floor
;;----------------------------------------------------------------------

(ns chaser-cljs.exit
  ;; NB: shamelessly stealing the represenation from the player for now
  (:require [chaser-cljs.player :as player]))

(def make-exit player/make-player)

(def get-x player/get-x)
(def get-y player/get-y)

(def update-x player/update-x)
(def update-y player/update-y)
