(ns michmusic.main
  (:use compojure)
  (:require [michmusic.database :as db])
  (:import [java.io File]
           [org.jaudiotagger.audio AudioFileIO]))

(defn html-doc
  [& body]
  (html
   (doctype :html4)
   [:html
    [:head
     [:title "Mich House Music"]
     [:link {:rel "stylesheet" :type "text/css" :href "/static/style.css"}]]
    [:body body]]))

(defn mp3-page [request]
  (html-doc
    [:div.title
     [:h1 "Mich House Music"]]
    [:div.artists
     [:h2 "Artists"]
     [:ul (map (fn [x] [:li x])
               (db/artists))]]))

(def static-files
     #^{:doc "Location of static files (css, images, etc)."}
     (str (.getParent (File. *file*))
          File/separator
          "static"))

(defroutes webservice
  (GET "/"
    mp3-page)
  (GET "/static/*"
    (or (serve-file static-files (params :*)) :next))
  (ANY "*"
    (page-not-found)))
