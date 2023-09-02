(ns html-scraping
  (:require [clojure.java.javadoc :refer [javadoc]])
  (:import [org.jsoup Jsoup]))

(def doc (Jsoup/connect ""))