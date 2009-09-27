(ns michmusic.main
  (:use compojure)
  (:require [compojure.encodings :as encodings]
            [michmusic.database :as db])
  (:import [java.io File]))

(defn artist-link
  [a]
  (link-to (str "/artist/" (encodings/urlencode a)) a))

(defn song-link
  [s]
  (let [t (:title s)]
    (link-to (str "/file/" (:artist s) "_" t ".mp3")
             (str t " - " (:album s)))))

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
      [:img {:src "/static/logo.png" :alt "Mich Music"}]]
     [:div.content
      [:div.artists
       [:h2 "Artists"]
       (unordered-list (map artist-link (db/artists)))]
      [:div.main
       body]]]]))

(defn mp3-page
  [request]
  (html-doc
    [:p "hi"]))

(defn artist-page
  [artist]
  (html-doc
    [:h2 artist]
    (unordered-list
     (map song-link (db/songs-for-artist artist)))))

(defn file-download
  [request]
  (let [[artist title] (map encodings/urldecode (:route-params request))]
    (if-let [path (db/song-path artist title)]
      (File. path)
      :next)))

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
  (GET #"/file/(.+?)_(.+)\.mp3"
    file-download)
  (GET "/static/*"
    (or (serve-file static-files (params :*)) :next))
  (ANY "*"
    (page-not-found)))
