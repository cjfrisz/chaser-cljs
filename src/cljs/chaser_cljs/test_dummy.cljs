;;----------------------------------------------------------------------
;; File test_dummy.cljs
;; Written by Chris Frisz
;; 
;; Created 18 Aug 2013
;; Last modified 21 Aug 2013
;; 
;; Dummy test file for checking out maps produced by build-map. This 
;; will go away very quickly.
;;----------------------------------------------------------------------

(ns chaser-cljs.test-dummy
  (:require [seedrandom :as srand]
            [chaser-cljs.coords :refer (coords-get-x coords-get-y)]
            [chaser-cljs.map-generator :refer (build-map)]
            [chaser-cljs.map-printer :refer (print-to-console)]))

(defn sort-game-map
  [game-map]
  (sort (fn [a b]
          (let [x-a (coords-get-x a)
                y-a (coords-get-y a)
                x-b (coords-get-x b)
                y-b (coords-get-y b)]
            (or (< y-a y-b)
                (and (< y-a y-b) (< x-a x-b))
                (and (= y-a y-b) (< x-a x-b)))))
    game-map))

(.seedrandom js/Math "foop")
(let [game-map (build-map 15)]
  (.log js/console (pr-str (sort-game-map game-map)))
  (print-to-console game-map))
