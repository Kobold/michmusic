(ns michmusic.html
  (:use compojure))

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

(defn- destructure-select-option
  [{l :label v :value}]
  [l v])

(defn browse-html
  [artist-options]
  (html-doc
    [:div#artists
     [:h2 "Artists"]
     [:select#current-artist {:size 25}
      (select-options (map destructure-select-option
                           artist-options))]]
    [:div#main
     [:p "hi"]]))


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