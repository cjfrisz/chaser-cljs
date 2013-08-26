;;----------------------------------------------------------------------
;; File js_utils.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 25 Aug 2013
;; 
;; Miscellaneous JavaScript-related utilities.
;;----------------------------------------------------------------------

(ns chaser-cljs.js-utils
  (:require-macros [dommy.macros :refer (sel1)]))

(defn get-2d-context [] (.getContext (sel1 :#gameCanvas) "2d"))
