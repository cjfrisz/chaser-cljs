;;----------------------------------------------------------------------
;; File map_printer.cljs
;; Written by Chris Frisz
;; 
;; Created 18 Aug 2013
;; Last modified 18 Aug 2013
;; 
;; Game map printer. Pretty much only (semi-)useful for feeling out game
;; map generation. Will (very) likely disappear in the (near) future.
;;----------------------------------------------------------------------

(ns map-printer
  (:require [coords :refer (coords-get-x coords-get-y)]))

(defn print-to-console
  [game-map]
  (doseq [y (reverse (range (inc (apply max 
                                   (map coords-get-y game-map)))))]
    (loop [point* (sort-by coords-get-x
                    (filter (comp (partial = y) coords-get-y) 
                      game-map))
           last-point-x 0
           cur-line ""]
      (if (nil? (seq point*))
        (.log js/console cur-line)
        (let [cur-point-x (inc (coords-get-x (first point*)))]
          (recur (next point*)
            cur-point-x
            (str cur-line 
              (apply str 
                (repeat (dec (- cur-point-x last-point-x)) " "))
              ".")))))))
