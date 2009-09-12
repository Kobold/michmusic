(ns michmusic.main
  (:use compojure)
  (:import [java.io File]
           [org.jaudiotagger.audio AudioFileIO]))

(def *music-directory* (File. "/Users/kobold/Music"))
(def *song-db* (ref []))

(defstruct song :title :album :artist :path)

(defn- song-from-tag [tag file]
  (struct song
          (.getFirstTitle  tag)
          (.getFirstAlbum  tag)
          (.getFirstArtist tag)
          (.getPath file)))

(defn- extract-metadata [file]
  (if-let [tag (.getTag (AudioFileIO/read file))]
    (let [s (song-from-tag tag file)]
      (dosync (alter *song-db* conj s)))))

(defn- list-mp3 []
  (let [is-mp3? (fn [file] (and (.isFile file)
                                (.. file getName (endsWith ".mp3"))))]
    (filter is-mp3? (file-seq *music-directory*))))

(defn load-song-db []
  (.start
   (Thread.
    (fn []
      (dorun (map extract-metadata (list-mp3)))))))

(defn html-doc
  [body]
  (html
   (doctype :html4)
   [:html
    [:head
     [:title "Mich House Music"]]
    [:body body]]))

(defn mp3-page [request]
  (let [artists (set (map :artist @*song-db*))]
    (html-doc
        [:div.artists
         [:h2 "Artists"]
         [:ul (map (fn [x] [:li x])
                   (sort artists))]])))

(defroutes webservice
  (GET "/" mp3-page))
