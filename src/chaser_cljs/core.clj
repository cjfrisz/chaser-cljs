;;--------------------------------------------------
;; File core.clj
;; Written by Chris Frisz
;; Created 12 Aug 2013
;; Last updated 12 Aug 2013
;;
;; Core file for chaser game. Doesn't do much yet
;;--------------------------------------------------

(ns chaser-cljs.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!"))