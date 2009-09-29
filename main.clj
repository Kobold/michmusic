(ns michmusic.main
  (:use clojure.contrib.json.read
        compojure
        michmusic.utils)
  (:require [clojure.http.client :as client]
            [compojure.encodings :as encodings]
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
     [:div#navigation
      (unordered-list [(link-to "/" "Browse")
                       (link-to "/upload/" "Upload")])]
     [:h1#title
      [:img {:src "/static/logo.png" :alt "Mich Music"}]]
     [:div#content
      body]]]))

(defn mp3-page
  [request]
  (html-doc
    [:div#artists
     [:h2 "Artists"]
     [:select#current-artist {:size 25}
      (select-options (map artist-option (db/artists)))]]
    [:div#main
     [:p "hi"]]))

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

(defn upload-page
  [request]
  (html-doc
    [:h2 "Upload File"]
    (form-to {:enctype "multipart/form-data"} [:post "/upload/"]
             (file-upload :test)
             (submit-button "Go"))))

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
  (GET "/upload/"
    upload-page)
  (POST "/upload/"
    (let [upload ((get-multipart-params request) :test)]
      (html-doc
        [:p "Filename: " (upload :filename)]
        [:p "SHA1: " (sha (.getInputStream (upload :disk-file-item)))])))
  (GET #"/artist/(.+)"
    (artist-page (encodings/urldecode ((:route-params request) 0))))
  (GET #"/file/(.+?)_(.+)\.mp3"
    file-download)
  (GET "/static/*"
    (or (serve-file static-files (params :*)) :next))
  (ANY "*"
    (page-not-found)))
