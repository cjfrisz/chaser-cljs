;;----------------------------------------------------------------------
;; File coords.cljs
;; Written by Chris Frisz
;; 
;; Created 18 Aug 2013
;; Last modified 10 Sep 2013
;; 
;; Simple coordinate representation.
;;----------------------------------------------------------------------

(ns chaser-cljs.coords
  (:require-macros [cljs.core.typed :refer (ann def-alias)]))

(ann ^:no-check cljs.core/assoc number)
(def-alias EmptySeqable (TFn [[x :variance :covariant]] (I (cljs.core/ISeqable x) (ExactCount 0))))
(def-alias NonEmptySeqable (TFn [[x :variance :covariant]] (I (cljs.core/ISeqable x) (CountRange 1))))
(ann ^:no-check cljs.core/vector (All [x y z a b c] 
                       (Fn
                        [-> '[]]
                        [x -> '[x]]
                        [x y -> '[x y]]
                        [x y z -> '[x y z]]
                        [x y z a -> '[x y z a]]
                        [x y z a b -> '[x y z a b]]
                        [x y z a b c -> '[x y z a b c]]
                        [x * -> (cljs.core/IVector x)])))
(ann ^:no-check cljs.core/first (All [x]
                (Fn [(U nil (EmptySeqable x)) -> nil]
                    [(NonEmptySeqable x) -> x]
                    [(U nil (cljs.core/ISeqable x)) -> (U nil x)])))
(ann ^:no-check cljs.core/fnext (All [x]
                [(U nil (cljs.core/ISeqable (U nil (cljs.core/ISeqable x)))) -> (U nil x)]))
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

(ann ^:no-check get-x [CoordsT -> CoordValT])
(defn get-x [[x y]] x)
#_(def ^{:doc "Returns the x value for the given coords structure."}
  get-x first)
(ann ^:no-check get-y [CoordsT -> CoordValT])
(defn get-y [[x y]] y)
#_(def ^{:doc "Returns the y value for the given coords structure."}
  get-y fnext)

(ann ^:no-check update-x [CoordsT CoordValT -> CoordsT])
(defn update-x [[x y] new-x] [new-x y])
#_(defn update-x 
  "Updates the x value for the given coords structure to be new-x."
  [coords new-x]
  (assoc coords 0 new-x))
(ann update-y [CoordsT CoordValT -> CoordsT])
(defn update-y [[x y] new-y] [x new-y])
(defn update-y 
  "Updates the x value for the given coords structure to be new-x."
  [coords new-y]
  (assoc coords 1 new-y))

(ann update-first [(Vector* number number) number -> (Vector* number number)])
(defn update-first [my-vec new-first] (assoc my-vec 1 new-first))
