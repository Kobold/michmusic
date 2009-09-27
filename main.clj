(ns michmusic.main
  (:use compojure)
  (:require [compojure.encodings :as encodings]
            [michmusic.database :as db])
  (:import [java.io File]))

(defn artist-option
  [a]
  [a (str "/artist/" (encodings/urlencode a))])

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
     (include-css "/static/style.css")
     (include-js "/static/external/jquery-1.3.2.min.js"
                 "/static/external/swfobject.js"
                 "/static/external/1bit.js"
                 "/static/navigation.js")]
    [:body
     [:div#title
      [:img {:src "/static/logo.png" :alt "Mich Music"}]]
     [:div#content
      [:div#artists
       [:h2 "Artists"]
       [:select#current-artist {:size 25}
        (select-options (map artist-option (db/artists)))]]
      [:div#main
       body]]]]))

(defn mp3-page
  [request]
  (html-doc
    [:p "hi"]))

(defn artist-page
  [artist]
  (html
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
  (GET #"/artist/(.+)"
    (artist-page (encodings/urldecode ((:route-params request) 0))))
  (GET #"/file/(.+?)_(.+)\.mp3"
    file-download)
  (GET "/static/*"
    (or (serve-file static-files (params :*)) :next))
  (ANY "*"
    (page-not-found)))
