;;----------------------------------------------------------------------
;; File protocols.cljs
;; Written by Chris Frisz
;; 
;; Created 27 Aug 2013
;; Last modified 28 Aug 2013
;; 
;; Protocols used for the game
;;----------------------------------------------------------------------

(ns chaser-cljs.protocols)

(defprotocol PRender (render [this context game-env]))