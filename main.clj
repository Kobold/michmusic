(ns michmusic.main
  (:use compojure)
  (:import [java.io File]
           [org.jaudiotagger.audio AudioFileIO]))

(def *music-directory* (File. "/Users/kobold/Music"))

(defn extract-metadata [file]
  (let [tag (.getTag (AudioFileIO/read file))]
    (if tag ; getTag may return null
      {:title  (.getFirstTitle  tag)
       :album  (.getFirstAlbum  tag)
       :artist (.getFirstArtist tag)
       :path   (.getPath file)}
      nil)))

(defn list-mp3 []
  (let [is-mp3? (fn [file] (and (.isFile file)
                                (.. file getName (endsWith ".mp3"))))]
    (filter is-mp3? (file-seq *music-directory*))))

(defn html-doc 
  [title & body] 
  (html 
   (doctype :html4) 
   [:html 
    [:head 
     [:title title]] 
    [:body 
     [:div 
      [:h2 
       [:a {:href "/"} "Home"]]]
     body]])) 

(defn song-table [song]
  [:div.song
   (for [[k v] song]
     [:p (str (name k) ": " v)])])

(def *song-db*
     (let [parsed-songs (map extract-metadata (list-mp3))]
       (filter (complement nil?) parsed-songs)))

(defn mp3-page [request]
     (html-doc "MP3s"
       (map song-table *song-db*)))

(defroutes webservice
  (GET "/" mp3-page)) 
