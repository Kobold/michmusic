(ns michmusic.controller
  (:use clojure.contrib.json.read
        compojure
        michmusic.html
        michmusic.utils)
  (:require [clojure.contrib.duck-streams :as duck-streams]
            [clojure.http.client :as client]
            [compojure.encodings :as encodings]
            [michmusic.database :as db])
  (:import [java.io File]))

(def *upload-directory* (java.io.File. "/Users/kobold/michmusic-storage"))

(defn artist-info
  [artist]
  (try
   (let [url (url-params "http://ws.audioscrobbler.com/2.0/"
                         {:method "artist.getinfo"
                          :artist artist
                          :format "json"
                          :api_key "6d02d500b71c11ea4a22f28832c82c6b"})
         response (client/request url)
         json ((read-json (apply str (response :body-seq))) "artist")]
     [((json "bio") "summary") ((( json "image") 3) "#text")])
   (catch java.net.UnknownHostException _
     ["" ""])
   (catch java.net.ConnectException _
     ["" ""])))

(defn artist-page
  [artist]
  (let [[summary img-src] (artist-info artist)]
    (artist-html artist
                 summary
                 img-src
                 (db/songs-by-album artist))))

(defn upload-post
  [request]
  (let [upload ((get-multipart-params request) :file)
        dfi (upload :disk-file-item)
        sha (sha-1 (.getInputStream dfi))
        storage (File. *upload-directory* (str sha ".mp3"))]
    (.createNewFile storage)
    (duck-streams/copy (.getInputStream dfi) storage)
    (db/import-file storage)
    (upload-post-html (.getPath storage) sha)))

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
    (browse-html (db/artists)))
  (GET "/upload/"
    (upload-get-html))
  (POST "/upload/"
    upload-post)
  (GET #"/artist/(.+)"
    (artist-page (encodings/urldecode ((:route-params request) 0))))
  (GET #"/file/(.+?)_(.+)\.mp3"
    file-download)
  (GET "/static/*"
    (or (serve-file "static" (params :*)) :next))
  (ANY "*"
    (page-not-found)))
