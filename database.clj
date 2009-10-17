(ns michmusic.database
  (:import [org.cmc.music.myid3 MyID3])
  (:require [clojure.contrib.seq-utils :as seq-utils]
            [clojure.contrib.str-utils2 :as str-utils2]
            [michmusic.utils :as utils]))

(def *music-directory* (java.io.File. "/Users/kobold/Music"))

(defstruct song :title :track :album :artist :year :sha :path)

(def song-db (ref #{}))

(defn- song-from-metadata
  [md file]
  (struct song
          (.getSongTitle md)
          (.getTrackNumberNumeric md)
          (.getAlbum md)
          (.getArtist md)
          (.getYear md)
          (utils/sha-1 file)
          (.getPath file)))

(defn import-file
  [f]
  (let [md (.. (MyID3.) (read f) getSimplified)]
    (if (.hasBasicInfo md)
      (dosync (alter song-db conj (song-from-metadata md f))))))

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
  (sort
   (map :artist
        (clojure.set/project @song-db [:artist]))))

(defn songs-by-album
  [artist]
  (let [songs (clojure.set/select #(= (:artist %) artist) @song-db)
        albums (seq-utils/group-by (fn [x] [(:album x) (:year x)])
                                   (sort-by :track songs))]
    (sort-by #(nth (key %) 1) albums)))

(defn song-path
  [artist title]
  (let [[s] (filter #(and (= (:artist %) artist)
                         (= (:title %) title))
                   @song-db)]
    (if s (:path s))))
