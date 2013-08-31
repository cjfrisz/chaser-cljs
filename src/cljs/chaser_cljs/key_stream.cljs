;;----------------------------------------------------------------------
;; File key_stream.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified 31 Aug 2013
;; 
;; Whoops...this turned into aliases for vector ops. Probably not a
;; useful datatype anymore.
;;----------------------------------------------------------------------

(ns chaser-cljs.key-stream)

(def make-key-stream vector)
(def enqueue conj)
(def dequeue first)
(def dequeue-batch identity)
