;;----------------------------------------------------------------------
;; File coords.cljs
;; Written by Chris Frisz
;; 
;; Created 18 Aug 2013
;; Last modified 20 Aug 2013
;; 
;; Simple coordinate representation.
;;----------------------------------------------------------------------

(ns chaser-cljs.coords)

;; NB: this is entirely equivalent to (def make-coords vector) for 
;;     ClojureScript since there's no arity enforcement, but it would
;;     feel dirty not to do it this way.
(defn make-coords
  "Creates a new coords structure for use in the map generator."
  [x y]
  ;; yes, yes...could have been (defn make-coords [x y] [x y]), but that
  ;; looks too weird
  (vector x y))

(def ^{:doc "Returns the x value for the given coords structure."}
  coords-get-x first)
(def ^{:doc "Returns the y value for the given coords structure."}
  coords-get-y fnext)

(defn coords-update-x 
  "Updates the x value for the given coords structure to be new-x."
  [coords new-x]
  (assoc coords 0 new-x))
(defn coords-update-y 
  "Updates the x value for the given coords structure to be new-x."
  [coords new-y]
  (assoc coords 1 new-y))
