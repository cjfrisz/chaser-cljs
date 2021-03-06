;;----------------------------------------------------------------------
;; File system.cljs
;; Written by Chris Frisz
;; 
;; Created 10 Sep 2013
;; Last modified 10 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.system
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.game-env :as game-env]
            [chaser-cljs.render.game :as game-render]))

(defrecord+ System [env renderer])

(defn make-system []
  (->System
    (atom (game-env/make-game-env))
    (game-render/make-game-renderer)))
