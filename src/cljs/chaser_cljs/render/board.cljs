;;----------------------------------------------------------------------
;; File board.cljs
;; Written by Chris Frisz
;; 
;; Created 31 Aug 2013
;; Last modified 13 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.render.board
  (:require-macros [chaser-cljs.macros
                    :refer (defrecord+ set-attributes! make-path!)])
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.protocols :as proto]))

(defrecord+ BoardRenderer []
  proto/PRender
  (render! [this board ctx]
    (doseq [space (board/get-coord* board)]
      ;; NB: get to solve the "place an arbitrary tile in space" problem
      ;;     here
      (.translate ctx 
      (doto ctx
        (make-path! (.rect (* (coords/get-x space) (:space-width this))
                      (* (coords/get-y space) (:space-width this))
                      space-width
                      space-width))
        .fill
        .stroke))))
