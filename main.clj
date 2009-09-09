(ns michmusic.main
  (:use compojure)
  (:import [java.io File]
           [org.jaudiotagger.audio AudioFileIO]))

(def *music-directory* (File. "/Users/kobold/Music"))

(defn extract-metadata [file]
  (let [tag (.getTag (AudioFileIO/read file))]
    {:title  (.getFirstTitle  tag)
     :album  (.getFirstAlbum  tag)
     :artist (.getFirstArtist tag)
     :path   (.getPath file)}))

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

(def mp3-page
     (html-doc "MP3s"
       (map #(song-table (extract-metadata %))
            (take 10 (list-mp3)))))

(defroutes webservice
  (GET "/" mp3-page)) 
