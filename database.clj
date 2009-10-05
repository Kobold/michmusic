(ns michmusic.database
  (:import [org.cmc.music.myid3 MyID3]))

(def *music-directory* (java.io.File. "/Users/kobold/Music"))

(defstruct song :title :album :artist :year :path)

(def song-db (ref #{}))

(defn- song-from-metadata
  [md file]
  (struct song
          (.getSongTitle md)
          (.getAlbum md)
          (.getArtist md)
          (.getYear md)
          (.getPath file)))


(defn import-file
  [f]
  (let [md (.. (MyID3.) (read f) getSimplified)]
    (dosync (alter song-db conj (song-from-metadata md f)))))

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

(defn songs-for-artist
  [a]
  (clojure.set/select #(= (:artist %) a)
                      @song-db))

(defn song-path
  [artist title]
  (let [[s] (filter #(and (= (:artist %) artist)
                         (= (:title %) title))
                   @song-db)]
    (if s (:path s))))
