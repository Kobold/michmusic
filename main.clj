(ns michmusic.main
  (:use clojure.contrib.json.read
        compojure
        michmusic.html
        michmusic.utils)
  (:require [clojure.http.client :as client]
            [compojure.encodings :as encodings]
            [michmusic.database :as db])
  (:import [java.io File]))

(defn song-link
  [s]
  (let [t (:title s)]
    (link-to (str "/file/" (:artist s) "_" t ".mp3")
             (str t " - " (:album s)))))


(defn artist-info
  [artist]
  (let [url (url-params "http://ws.audioscrobbler.com/2.0/"
                        {:method "artist.getinfo"
                         :artist artist
                         :format "json"
                         :api_key "6d02d500b71c11ea4a22f28832c82c6b"})
        response (client/request url)
        json ((read-json (apply str (response :body-seq))) "artist")]
    [((json "bio") "summary")
     ((( json "image") 3) "#text")]))


(defn browse-page
  [request]
  (browse-html
   (map (fn [a] {:label a
                 :value (str "/artist/" (encodings/urlencode a))})
        (db/artists))))

(defn artist-page
  [artist]
  (let [[summary img-src] (artist-info artist)
        songs (db/songs-for-artist artist)]
    (html
     [:h2 artist]
     [:img {:src img-src :alt artist}]
     [:p summary]
     (unordered-list
      (map song-link songs)))))

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
    browse-page)
  (GET "/upload/"
    (upload-get-html))
  (POST "/upload/"
    (let [upload ((get-multipart-params request) :file)]
      (upload-post-html (upload :filename)
                        (sha (.getInputStream (upload :disk-file-item))))))
  (GET #"/artist/(.+)"
    (artist-page (encodings/urldecode ((:route-params request) 0))))
  (GET #"/file/(.+?)_(.+)\.mp3"
    file-download)
  (GET "/static/*"
    (or (serve-file static-files (params :*)) :next))
  (ANY "*"
    (page-not-found)))
