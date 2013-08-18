;;--------------------------------------------------
;; File map_generator.cljs
;; Written by Chris Frisz
;; Created 12 Aug 2013
;; Last updated 17 Aug 2013
;;
;; Map generation functions.
;;--------------------------------------------------

(ns chaser-cljs.map-generator)

(def dir* [:left :right :up :down])

;;------------------------------
;; Map spaces
;;------------------------------
(defn make-space
  "Returns a new space with the given x and y attributes."
  [x y]
  {:x x :y y})

(def space-get-x :x)

(def space-get-y :y)

;; NB: uncomfortable that this returns undecorated vector, but
;;     returning as a map isn't useful
(defn get-adjacent-coords
  "Returns a vector of the x and y values, respectively, for the space
   given by dir. Valid values for dir:
       :left
       :right
       :up
       :down"
  [space dir]
  (let [x (space-get-x space)
        y (space-get-y space)]
    (case dir
      :left  [(dec x) y]
      :right [(inc x) y]
      :up    [x (inc y)]
      :down  [x (dec y)]
      :else  (throw (js/Error (str "invalid dir argument " dir))))))

;;----------------------------------------
;; Map definition, accessors, updaters
;;----------------------------------------
(def make-game-map hash-map)

(defn game-map-get-space
  "Returns the space within the game-map given by the x and y values."
  [game-map x y]
  (get-in game-map [x y]))

(defn game-map-update-space
  "Updates the space within the game-map given by the x and y values of
   the space to match the given space."
  [game-map space]
  (assoc-in game-map [(space-get-x space) (space-get-y space)] space))

;;----------------------------------------
;; Map builder
;;----------------------------------------
(defn expand-around-space
  "Returns a vector representing a randomized (not necessarily proper)
   subset of unoccupied spaces cardinally adjacent to the given space
   according to game-map."
  [game-map space]
  (remove false?
    (for [dir (shuffle dir*)
          :let [[adj-x adj-y] (get-adjacent-coords space dir)]]
      (and (nil? (game-map-get-space game-map adj-x adj-y))
           (= (rand-int 2) 1)
           (make-space adj-x adj-y)))))

(defn num-surrounding-spaces
  "Returns the number of spaces cardinally adjacent to space according
   to game-map."
  [game-map space]
  (reduce (fn [total dir]
            (if (nil? (apply game-map-get-space
                        game-map
                        (get-adjacent-coords space dir)))
                total
                (inc total)))
    0
    dir*))

(defn build-map
  "Returns a procedurally-generated game-map of with target-size number
   of spaces. It generates the map by starting from a single space at
   (0, 0), and for each space with at least one unused adjacent space,
   there is a 50% chance that a new space is added. The algorithm
   continues until the target-size is reached."
  [target-size]
  (let [init-space (make-space 0 0)]
    (loop [game-map (game-map-update-space (make-game-map) init-space)
           work-list* [init-space]
           remaining-spaces (dec target-size)]
      (if (zero? remaining-spaces)
          game-map
          (let [new-space* (take (min remaining-spaces (count dir*))
                             (expand-around-space game-map (first work-list*)))]
            (recur (reduce game-map-update-space game-map new-space*)
              (shuffle (remove (comp (partial = 4)
                                     (partial num-surrounding-spaces game-map))
                         (concat work-list* new-space*)))
              (- remaining-spaces (count new-space*))))))))
