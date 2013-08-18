(defproject chaser-cljs "0.1.0-SNAPSHOT"
  :description "A top-down action survival game"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.1.8"]]
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-ring "0.8.3"]]
  :cljsbuild {
    :builds [{:source-paths ["src/cljs"],
    :builds nil,
    :compiler {
      :pretty-print true,
      :output-to "resources/public/js/cljs.js",
      :optimizations :simple}}]}
  :main chaser-cljs.server
  :ring {:handler chaser-cljs.server/app})
