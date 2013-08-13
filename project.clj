(defproject chaser-cljs "0.1.0-SNAPSHOT"
  :description "A top-down action survival game"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :plugins [[lein-cljsbuild "0.3.2"]]
  :cljsbuild
  {
   :source-path "src-cljs"
   :compiler
   {
    :output-to "resources/public/js/cljs.js"
    :optimizations :simple
    :pretty-print true
    }
   }
  :main hello-clojurescript.core
  :main chaser-cljs.core)
