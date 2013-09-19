;;----------------------------------------------------------------------
;; File dom.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 18 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.dom
  (:require-macros [dommy.macros :refer (node sel1)])
  (:require [dommy.core :as dommy]
            [chaser-cljs.board :as board]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.render.board :as board-render]
            [chaser-cljs.render.game :as game-render]
            [chaser-cljs.render.room :as room-render]))

;; NB: hate that this couples the HTML of the page and code, but there 
;;     doesn't seem to be a better way
(def game-div-id :#game)
(def game-canvas-id :#gameCanvas)

(defn reset-canvas! 
  [game-env game-renderer]
  (letfn [(board-max-coord [coord-getter]
            (reduce (fn [cur-max coord]
                      (max cur-max (coord-getter coord)))
              0
              (board/get-room* (game-env/get-board game-env))))
          (dimension-pxs [max-coord space-dim border-size]
            (+ max-coord space-dim (* border-size 2)))]
    (let [game-canvas (sel1 game-canvas-id)
          ;; NB: this is really gross
          room-renderer (first (-> game-renderer 
                                 game-render/get-board-renderer
                                 board-render/get-room-renderer*))]
      (set! (.-width game-canvas) 
        (dimension-pxs (board-max-coord proto/get-x)
          (room-render/get-width room-renderer)
          (game-render/get-border-width game-renderer)))
      (set! (.-height game-canvas)
        (dimension-pxs (board-max-coord proto/get-y)
          (room-render/get-height room-renderer)
          (game-render/get-border-height game-renderer))))))
