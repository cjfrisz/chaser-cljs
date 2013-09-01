;;----------------------------------------------------------------------
;; File dom.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 31 Aug 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.dom
  (:require-macros [dommy.macros :refer (node sel1)])
  (:require [dommy.core :as dommy]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.game-env :as game-env]))

;; NB: need to parameterize these somehow; dom environment?
(def game-div-id :#game)
(def game-canvas-id :#gameCanvas)
(def game-canvas-id-str (subs (name game-canvas-id) 1))

(defn reset-canvas! 
  [game-env]
  (let [board (game-env/get-board game-env)
        board-renderer (game-env/get-board-renderer game-env)]
    (letfn [(board-max-coord [coord-getter]
              (reduce (fn [cur-max coord]
                        (max cur-max (coord-getter coord)))
                0
                board))
            (dimension-pxs [max-coord]
              ;; NB: direct access to record member! yuck!
              (+ (* (inc max-coord) (:space-width board-renderer)) 
                 ;; NB: again with the direct record member access!
                 (* (:outer-border-size board-renderer) 2)))]
      (let [game-canvas (sel1 game-canvas-id)
            width (dimension-pxs (board-max-coord coords/get-x))
            height (dimension-pxs (board-max-coord coords/get-y))]
        (set! (.-width game-canvas) width)
        (set! (.-height game-canvas) height)))))

(defn init-canvas!
  "Initialize the game canvas."
  [game-env]
  (dommy/append! (sel1 game-div-id) (node [:canvas {:id game-canvas-id-str}]))
  (reset-canvas! game-env))
