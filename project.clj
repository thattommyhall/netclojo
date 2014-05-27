(defproject netclojo "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [org.clojure/clojurescript "0.0-2227"]
                 [domina "1.0.2-SNAPSHOT"]
                 ]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "netclojo"
              :source-paths ["src"]
              :compiler {
                :output-to "netclojo.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
