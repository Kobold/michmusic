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
     (include-css "/static/style.css")]
    [:body
     [:div.title
      [:h1 "Mich House Music"]]
     [:div.artists
      [:h2 "Artists"]
      (unordered-list (map #(link-to (str "/" %) %)
                           (db/artists)))]
     [:div.main
      body]]]))

(defn mp3-page [request]
  (html-doc
    [:p "hi"]))

(defn artist-page [artist]
  (html-doc
    (unordered-list
     (map #(str (:title %) " - " (:artist %))
          (db/songs-for-artist artist)))))

(def static-files
     #^{:doc "Location of static files (css, images, etc)."}
     (str (.getParent (File. *file*))
          File/separator
          "static"))

(defroutes webservice
  (GET "/"
    mp3-page)
  (GET "/:artist"
    (artist-page (params :artist)))
  (GET "/static/*"
    (or (serve-file static-files (params :*)) :next))
  (ANY "*"
    (page-not-found)))
