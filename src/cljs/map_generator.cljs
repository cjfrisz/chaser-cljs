;;--------------------------------------------------
;; File map_generator.cljs
;; Written by Chris Frisz
;; Created 12 Aug 2013
;; Last updated 12 Aug 2013
;;
;; Core file for chaser game. Doesn't do much yet
;;--------------------------------------------------

(ns map-generator)

(def make-map {})

(defn map-get-space
  [map x y]
  (get-in map [x y]))

(defn map-set-space
  [map x y space]
  (assoc-in [x y] space))

(defn get-adjacent-space
  [map x y dir]
  (case dir
    :left  (map-get-space map (dec x) y)
    :right (map-get-space map (inc x) y)
    :up    (map-get-space map x (inc y))
    :down  (map-get-space map x (dec y))))

(defn get-all-adjacent-spaces
  [map x y]
  (let [dir* [:left :right :up :down]]
    (zipmap dir* (mapv (partial get-adjacent-space x y) dir*))))

