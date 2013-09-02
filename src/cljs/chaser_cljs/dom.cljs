;;----------------------------------------------------------------------
;; File dom.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified  2 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.dom
  (:require-macros [dommy.macros :refer (node sel1)])
  (:require [dommy.core :as dommy]
            [chaser-cljs.board :as board]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.render.game :as game-render]))

;; NB: need to parameterize these somehow; dom environment?
(def game-div-id :#game)
(def game-canvas-id :#gameCanvas)
(def game-canvas-id-str (subs (name game-canvas-id) 1))

(defn reset-canvas! 
  [game-env game-renderer]
  (letfn [(board-max-coord [coord-getter]
            (reduce (fn [cur-max coord]
                      (max cur-max (coord-getter coord)))
              0
              (board/get-coord* (game-env/get-board game-env))))
          (dimension-pxs [max-coord space-dim border-size]
            (+ (* (inc max-coord) space-dim) (* border-size 2)))]
    (let [game-canvas (sel1 game-canvas-id)
          board-renderer (:board-renderer game-renderer)]
      (set! (.-width game-canvas) 
        (dimension-pxs (board-max-coord coords/get-x)
          ;; NB: direct access to foreign record member! yuck!
          (:space-width board-renderer)
          (:border-width game-renderer)))
      (set! (.-height game-canvas)
        (dimension-pxs (board-max-coord coords/get-y)
        ;; NB: direct access to foreign record member! yuck!
        (:space-height board-renderer)
        (:border-height game-renderer))))))

(defn init-canvas!
  "Initialize the game canvas."
  [game-env game-renderer]
  (dommy/append! (sel1 game-div-id) (node [:canvas {:id game-canvas-id-str}]))
  (reset-canvas! game-env game-renderer))
