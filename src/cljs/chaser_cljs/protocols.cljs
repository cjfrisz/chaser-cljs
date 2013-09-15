;;----------------------------------------------------------------------
;; File protocols.cljs
;; Written by Chris Frisz
;; 
;; Created 27 Aug 2013
;; Last modified 13 Sep 2013
;; 
;; Protocols used for the game
;;----------------------------------------------------------------------

(ns chaser-cljs.protocols)

(defprotocol PRender (render! [this target context]))

(defprotocol PCoords 
  (get-x [this])
  (get-y [this])
  (update-x [this new-x])
  (update-y [this new-y]))
