;;----------------------------------------------------------------------
;; File key_stream.cljs
;; Written by Chris Frisz
;; 
;; Created 24 Aug 2013
;; Last modified 25 Aug 2013
;; 
;; Whoops...this turned into aliases for vector ops. Probably not a
;; useful datatype anymore.
;;----------------------------------------------------------------------

(ns chaser-cljs.key-stream)

(def make-key-stream vector)
(def key-stream-enqueue conj)
(def key-stream-dequeue first)
(def key-stream-dequeue-batch identity)
