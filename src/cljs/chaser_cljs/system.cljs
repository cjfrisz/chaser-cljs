;;----------------------------------------------------------------------
;; File system.cljs
;; Written by Chris Frisz
;; 
;; Created 10 Sep 2013
;; Last modified 17 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.system
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.game-env :as game-env]
            [chaser-cljs.render.board :as board-render]
            [chaser-cljs.render.game :as game-render]))

(defrecord+ System [game-env renderer])

(defn make-system [] 
  (let [game-env (game-env/make-game-env)]
    (->System game-env
      (game-render/make-game-renderer 
        (board-render/make-renderer (game-env/get-board game-env)))))) 
