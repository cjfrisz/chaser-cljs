(defproject chaser-cljs "0.1.0-SNAPSHOT"
  :description "A top-down action survival game"
  :license {
    :name "Eclipse Public License"
    :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1859"]
                 [ring "1.1.8"]
                 [prismatic/dommy "0.1.1"]
                 [org.clojure/core.typed "0.2.5-20130907.135349-4"]]
  :plugins [[lein-cljsbuild "0.3.3-SNAPSHOT"]
            [lein-ring "0.8.3"]
            [lein-typed "0.3.0"]]
  :source-paths ["src/clj" "src/cljs"]
  :hooks [leiningen.cljsbuild]
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :core.typed {:check [chaser-cljs.coords]}
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
