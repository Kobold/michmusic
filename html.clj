(ns michmusic.html
  (:use compojure)
  (:require [compojure.encodings :as encodings]))

(defn- include-css-media [style media]
  [:link {:type "text/css" :href style :rel "stylesheet" :media media}])

(defn- html-doc
  [& body]
  (html
   (doctype :html4)
   [:html
    [:head
     [:title "Mich House Music"]
     (include-css-media "/static/external/screen.css" "screen, projection")
     (include-css-media "/static/external/print.css" "print")
     "<!--[if lt IE 8]>"
     (include-css-media "/static/external/ie.css" "screen, projection")
     "<![endif]-->"
     (include-css "/static/style.css")
     (include-js "/static/external/jquery-1.3.2.min.js"
                 "/static/external/swfobject.js"
                 "/static/external/1bit.js"
                 "/static/navigation.js")]
    [:body
     [:div#navigation
      (unordered-list [(link-to "/" "Browse")
                       (link-to "/upload/" "Upload")])]
     [:div.container
      [:div#content
       body]]]]))


(defn- artist-option
  [a]
  [a (str "/artist/" (encodings/urlencode a))])

(defn browse-html
  [artists]
  (html-doc
    [:div#artists {:class "span-7"}
     [:h2 "Artists"]
     [:select#current-artist {:class "center" :size 25}
      (select-options
       (map artist-option artists))]]
    [:div#main {:class "span-15 prepend-1 append-1 last"}
     [:p "hi"]]))

(defn- song-link
  [s]
  (let [t (:title s)]
    (link-to (str "/file/" (:artist s) "_" t ".mp3")
             (str t " - " (:album s)))))

(defn artist-html
  [artist summary img-src songs]
  (html
   [:h2 artist]
   [:div {:class "span-8"}
    [:p summary]]
   [:div {:class "span-7 last"}
    [:img.center {:src img-src :alt artist}]]
   [:div {:class "span-15 prepend-top last"}
    (unordered-list
     (map song-link songs))]))

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