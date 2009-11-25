(ns michmusic.database
  (:import [org.cmc.music.myid3 MyID3])
  (:require [clojure.contrib.seq-utils :as seq-utils]
            [clojure.contrib.str-utils2 :as str-utils2]
            [cupboard.core :as cb]
            [michmusic.utils :as utils]))

(def *music-directory* (java.io.File. "/Users/kobold/Music"))

(cb/open-cupboard! "/tmp/songs")

(cb/defpersist song
  ((:title :index :any)
   (:track :index :any)
   (:album :index :any)
   (:artist :index :any)
   (:year :index :any)
   (:sha :index :unique)
   (:path :index :unique)))

(defn- song-from-metadata
  [md file]
  [(.getSongTitle md)
   (.getTrackNumberNumeric md)
   (.getAlbum md)
   (.getArtist md)
   (.getYear md)
   (utils/sha-1 file)
   (.getPath file)])

(defn- unified-metadata
  [md-set]
  (let [id3v1 (.id3v1Raw md-set)
        id3v2 (.id3v2Raw md-set)]
    (assert (or id3v1 id3v2))
    (cond (and id3v1 id3v2) (doto (.values id3v2)
                              (.mergeValuesIfMissing (.values id3v1)))
          id3v2 (.values id3v2)
          id3v1 (.values id3v1))))

(defn import-file
  [f]
  (let [md-set (.read (MyID3.) f)
        md (unified-metadata md-set)]
    (if (.hasBasicInfo md)
      (cb/make-instance song (song-from-metadata md f)))))

(defn- list-mp3
  []
  (let [is-mp3? (fn [file] (and (.isFile file)
                                (.. file getName (endsWith ".mp3"))))]
    (filter is-mp3? (file-seq *music-directory*))))

(defn load-song-db
  []
  (doseq [f (list-mp3)]
    (import-file f)))

(defn artists
  []
  (apply sorted-set
         (map :artist (cb/query :struct song))))

(defn songs-by-album
  [artist]
  (let [songs (cb/query (= :artist artist) :struct song)
        albums (seq-utils/group-by (fn [x] [(:album x) (:year x)])
                                   (sort-by :track songs))]
    (sort-by #(nth (key %) 1) albums)))

(defn song-path
  [sha]
  (let [[s] (cb/query (= :sha sha))]
    (if s (:path s))))
