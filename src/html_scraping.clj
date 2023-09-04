(ns html-scraping
  (:require [clojure.java.javadoc :refer [javadoc]])
  (:import [java.awt Desktop Desktop$Action]
           [java.net URI]
           [org.jsoup Jsoup]
           [org.jsoup.nodes Document Element]
           [org.jsoup.select Elements]))

(set! *warn-on-reflection* true)

(def url "https://todomvc.com")

(def todomvc-webpage (Jsoup/connect url))

(def homepage ^Document (.get todomvc-webpage))

(def frameworks ^Elements (.select homepage "li.routing"))

(def reagent-demo
  (let [[link] (for [^Element el frameworks
                     :let [^Elements link (.select el "a[href]")]
                     :when (= "Reagent" (.text link))
                     :let [browse (str url "/" (.attr link "href"))]]
                 browse)]
    link))

(defn browse-reagent-example []
  (when (and (Desktop/isDesktopSupported)
             (.. Desktop getDesktop (isSupported Desktop$Action/BROWSE)))
    (.. Desktop getDesktop (browse (URI. reagent-demo)))))

(comment
  (javadoc Desktop)
  (browse-reagent-example))