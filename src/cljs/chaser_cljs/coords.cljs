;;----------------------------------------------------------------------
;; File coords.cljs
;; Written by Chris Frisz
;; 
;; Created 18 Aug 2013
;; Last modified  2 Sep 2013
;; 
;; Simple coordinate representation.
;;----------------------------------------------------------------------

(ns chaser-cljs.coords
  (:require-macros [cljs.core.typed :refer (ann def-alias)]))

;; NB: this is entirely equivalent to (def make-coords vector) for 
;;     ClojureScript since there's no arity enforcement, but it would
;;     feel dirty not to do it this way.
(def-alias CoordValT number)
(def-alias CoordsT (Vector* CoordValT CoordValT))
(ann make-coords [number number -> CoordsT])
(defn make-coords
  "Creates a new coords structure for use in the map generator."
  [x y]
  ;; yes, yes...could have been (defn make-coords [x y] [x y]), but that
  ;; looks too weird
  (vector x y))

(ann get-x [CoordsT -> CoordValT])
(def ^{:doc "Returns the x value for the given coords structure."}
  get-x first)
(ann get-y [CoordsT -> CoordValT])
(def ^{:doc "Returns the y value for the given coords structure."}
  get-y fnext)

(ann update-x [CoordsT CoordValT -> CoordsT])
(defn update-x 
  "Updates the x value for the given coords structure to be new-x."
  [coords new-x]
  (assoc coords 0 new-x))
(ann update-y [CoordsT CoordValT -> CoordsT])
(defn update-y 
  "Updates the x value for the given coords structure to be new-x."
  [coords new-y]
  (assoc coords 1 new-y))
