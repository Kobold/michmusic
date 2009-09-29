(ns michmusic.html
  (:use compojure)
  (:require [compojure.encodings :as encodings]))

(defn- html-doc
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


(defn- artist-option
  [a]
  [a (str "/artist/" (encodings/urlencode a))])

(defn browse-html
  [artists]
  (html-doc
    [:div#artists
     [:h2 "Artists"]
     [:select#current-artist {:size 25}
      (select-options
       (map artist-option artists))]]
    [:div#main
     [:p "hi"]]))

(defn song-link
  [s]
  (let [t (:title s)]
    (link-to (str "/file/" (:artist s) "_" t ".mp3")
             (str t " - " (:album s)))))

(defn artist-html
  [artist summary img-src songs]
  (html
   [:h2 artist]
   [:img {:src img-src :alt artist}]
   [:p summary]
   (unordered-list
    (map song-link songs))))

(defn upload-get-html
  []
  (html-doc
    [:h2 "Upload File"]
    (form-to {:enctype "multipart/form-data"} [:post "/upload/"]
             (file-upload :file)
             (submit-button "Go"))))

(defn upload-post-html
  [filename sha-str]
  (html-doc
    [:p "Filename: " filename]
    [:p "SHA1: " sha-str]))