;;----------------------------------------------------------------------
;; File macros.clj
;; Written by Chris Frisz
;; 
;; Created  7 Sep 2013
;; Last modified  7 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.macros
  (:require [dommy.macros :as dommy]
            [clojure.string :as string]))

(defn make-factory-name
  [name]
  (symbol 
   (str "make" 
     (string/lower-case (string/replace name #"([A-Z])" "-$1")))))

(defmacro defrecord+
  [name field* & spec*]
  `(do
     (defrecord ~name ~(vec field*) ~@spec*)
     (defn ~(make-factory-name name)
       ~(vec field*)
       (~(symbol (str name ".")) ~@field*))
     ~@(for [field field*]
         `(def ~(symbol (str "get-" field)) ~(keyword field)))
     ~@(for [field field*]
         `(defn ~(symbol (str "update-" field))
            [record# new-val#]
            (assoc record# ~(keyword field) new-val#)))))

(defmacro with-protected-context
  [ctx & expr*]
  `(do (.save ~ctx) ~@expr* (.restore ~ctx)))

(defmacro get-2d-context 
  [canvas-elem]
  `(.getContext (dommy/sel1 ~canvas-elem) "2d"))

(defmacro set-attributes!
  [obj attr+val*]
  (assert (= (rem (count attr+val*) 2) 0))
  `(do
     ~@(for [[attr val] (partition 2 attr+val*)]
        `(set! (. ~obj ~(symbol (str "-" attr))) ~val))))

(defmacro make-path!
  [ctx & expr*]
  `(doto ~ctx .beginPath ~@expr* .closePath))
