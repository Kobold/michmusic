(ns michmusic.database
  (:import [org.jaudiotagger.audio AudioFileIO]))

(def *music-directory* (java.io.File. "/Users/kobold/Music"))

(defstruct song :title :album :artist :path)

(def song-db (ref #{}))

(defn song-from-tag
  [tag file]
  (struct song
          (.getFirstTitle  tag)
          (.getFirstAlbum  tag)
          (.getFirstArtist tag)
          (.getPath file)))

(defn list-mp3
  []
  (let [is-mp3? (fn [file] (and (.isFile file)
                                (.. file getName (endsWith ".mp3"))))]
    (filter is-mp3? (file-seq *music-directory*))))

(defn load-song-db
  []
  (doseq [f (list-mp3)]
    (if-let [tag (.getTag (AudioFileIO/read f))]
      (dosync (alter song-db conj (song-from-tag tag f))))))

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
