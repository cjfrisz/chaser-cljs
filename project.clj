(defproject chaser-cljs "0.1.0-SNAPSHOT"
  :description "A top-down action survival game"
  :license {
    :name "Eclipse Public License"
    :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1877"]
                 [ring "1.1.8"]
                 [prismatic/dommy "0.1.1"]]
  :plugins [[lein-cljsbuild "0.3.3-SNAPSHOT"]
            [lein-ring "0.8.3"]]
  :source-paths ["src/clj" "src/cljs"]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {
    :builds 
      [{:source-paths ["src/cljs"],
        :builds nil,
        :compiler {
          :pretty-print true,
          :output-to "resources/public/js/chaser.js",
          :optimizations :whitespace
          :foreign-libs [{:file "http://davidbau.com/encode/seedrandom-min.js"
                          :provides ["seedrandom"]}]}}]}
  :main chaser-cljs.server
  :ring {:handler chaser-cljs.server/app})
