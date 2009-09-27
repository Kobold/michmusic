(ns michmusic.main
  (:use compojure)
  (:require [compojure.encodings :as encodings]
            [michmusic.database :as db])
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
      (unordered-list (map #(link-to (str "/artist/" (encodings/urlencode %)) %)
                           (db/artists)))]
     [:div.main
      body]]]))

(defn mp3-page [request]
  (html-doc
    [:p "hi"]))

(defn artist-page [artist]
  (html-doc
    [:h2 artist]
    (unordered-list
     (map #(link-to (str "/file/" (:artist %) "_" (:title %) ".mp3")
                    (str (:title %) " - " (:artist %)))
          (db/songs-for-artist artist)))))

(def static-files
     #^{:doc "Location of static files (css, images, etc)."}
     (str (.getParent (File. *file*))
          File/separator
          "static"))

(defroutes webservice
  (GET "/"
    mp3-page)
  (GET "/artist/:artist"
    (artist-page (encodings/urldecode (params :artist))))
  (GET #"/file/(\w+)_(\w+)\.mp3"
    (let [[artist title] (:route-params request)]
      (if-let [path (db/song-path artist title)]
        (File. path)
        :next)))
  (GET "/static/*"
    (or (serve-file static-files (params :*)) :next))
  (ANY "*"
    (page-not-found)))
