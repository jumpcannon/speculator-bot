(defproject speculator "0.1.0-SNAPSHOT"
  :description "A robot that speculates about things"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.og/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [twitter-api "0.7.6"]]
  :main speculator.core
  :profiles {:uberjar {:aot :all}})
