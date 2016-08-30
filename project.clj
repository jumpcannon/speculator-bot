(defproject speculator "0.1.0-SNAPSHOT"
  :description "A robot that speculates about things"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.og/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [twitter-api "0.7.8"]]
  :main speculator.core
  :profiles {:uberjar {:aot :all}})
