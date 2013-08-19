;;--------------------------------------------------
;; File map_generator.cljs
;; Written by Chris Frisz
;; Created 12 Aug 2013
;; Last updated 17 Aug 2013
;;
;; Map generation functions.
;;--------------------------------------------------

(ns chaser-cljs.map-generator
  (:require [clojure.set :refer (union difference)]))

(defn make-coords [x y] (vector x y))
(def coords-get-x first)
(def coords-get-y fnext)
(defn coords-update-x [coords new-x] (assoc coords 0 new-x))
(defn coords-update-y [coords new-y] (assoc coords 1 new-y))

(defn adjacent-coords
  [coords]
  (let [x (coords-get-x coords)
        y (coords-get-y coords)]
    #{(make-coords x (inc y))    ;; up
      (make-coords (inc x) y)    ;; right
      (make-coords x (dec y))    ;; down
      (make-coords (dec x) y)})) ;; left

(defn normalize-coords
  [coords*]
  (let [min-x (apply min (map coords-get-x coords*))
        min-y (apply min (map coords-get-y coords*))]
    (if (and (zero? min-x) (zero? min-y))
        (vec coords*)
        (mapv #(as-> % coords
                 (coords-update-x coords (- (coords-get-x coords) min-x))
                 (coords-update-y coords (- (coords-get-y coords) min-y)))
          coords*))))

(defn build-map
  [target-size]
  (let [origin (make-coords 0 0)]
    (loop [coords* #{origin}
           adjacent* (adjacent-coords origin)]
      (if (= (count coords*) target-size)
          (normalize-coords coords*)
          (let [new-coords (rand-nth adjacent*)]
            (recur (conj coords* new-coords)
              (union (difference (adjacent-coords new-coords) coords*)
                (disj adjacent* new-coords))))))))
