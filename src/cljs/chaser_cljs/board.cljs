;;----------------------------------------------------------------------
;; File board.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified  2 Sep 2013
;; 
;; In-game board representation
;;----------------------------------------------------------------------

(ns chaser-cljs.board
  (:require-macros [cljs.core.typed :refer (ann def-alias)])
  (:require [clojure.set :as set]
            [chaser-cljs.coords :as coords]))

;; NB: these aliases likely invalid in ClojureScript; currently unknown
;;     because def-alias not supported :-)
(def-alias CoordsVecT (clojure.lang.IPersistenVector CoordsT))
(def-alias BoardT (HMap :mandatory {:coord* CoordsVecT
                                     :coord-map 
                                     (clojure.lang.APersistentMap 
                                       CoordValT
                                       (clojure.lang.APersistentMap 
                                         CoordValT
                                         CoordT))
                                     :size CoordValT}
                         :complete? true))

(ann make-board [CoordsVecT -> BoardT])
(defn make-board
  [coord*]
  {:coord* coord*
   :coord-map (reduce (fn [coord-map coord]
                        (assoc-in coord-map coord coord))
                {}
                coord*)
   :size (count coord*)})

;; NB: should typed as a function type; don't know how to express
(ann get-coord* (Value :coord*))
(def get-coord* :coord*)
(ann get-size (Value :size))
(def get-size :size)

(ann get-space [BoardT CoordValT CoordValT -> CoordsT])
(defn get-space
  [board target-x target-y]
  (get-in (:coord-map board) [target-x target-y]))


;;--------------------------------------------------
;; Procedural map generation functions
;;--------------------------------------------------

;; NB: type is imprecise because set contains exactly 4 items
(ann adjacent-coords [CoordsT -> (clojure.lang.IPersistentSet CoordsT)])
(defn adjacent-coords
  "Given a coords structure, returns a set of coords structures 
   representing the cardinally-adjacent coordinates."
  [coords]
  (let [x (coords/get-x coords)
        y (coords/get-y coords)]
    ;; NB: this doesn't inherently need to be a set, but is always used 
    ;;     as one, so we do it anyway
    #{(coords/make-coords x (inc y))    ;; up
      (coords/make-coords (inc x) y)    ;; right
      (coords/make-coords x (dec y))    ;; down
      (coords/make-coords (dec x) y)})) ;; left

(ann normalize-coords [CoordsVecT -> CoordsVecT])
(defn normalize-coords
  "Takes a sequence of coords structures, coords*, and returns a vector
   of coords structures representing the original coordinates translated
   by subtracting the minimum x and y values from each x and y value,
   respectively. In effect, this translates the coordinates as close to
   the origin as possible, all within the first quadrant of the 
   cartesian plane."
  [coords*]
  (let [min-x (apply min (map coords/get-x coords*))
        min-y (apply min (map coords/get-y coords*))]
    (if (and (zero? min-x) (zero? min-y))
        (vec coords*)
        (mapv #(as-> % coords
                 (coords/update-x coords (- (coords/get-x coords) min-x))
                 (coords/update-y coords (- (coords/get-y coords) min-y)))
          coords*))))

(ann generate-coord* [number -> CoordsVecT])
(defn generate-coord*
  "Takes an integer, target-size, and returns a vector of target-size
   number of coords structures such that the x and y value of each
   coords structure is >= 0. The vector is generated by starting with a
   single point, randomly selecting a point adjacent to it, and 
   repeating the process with each additional point until the 
   target-size is reached."
  [target-size]
  (let [origin (coords/make-coords 0 0)]
    (loop [coords* #{origin}
           adjacent* (adjacent-coords origin)]
      (if (= (count coords*) target-size)
          (normalize-coords coords*)
          (let [new-coords (rand-nth adjacent*)]
            (recur (conj coords* new-coords)
              (set/union (set/difference (adjacent-coords new-coords) 
                           coords*)
                (disj adjacent* new-coords))))))))

(ann make-randomized-board [number -> BoardT])
(def make-randomized-board (comp make-board generate-coord*))
