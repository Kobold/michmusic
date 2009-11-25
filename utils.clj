(ns michmusic.utils
  (:require [clojure.contrib.str-utils2 :as str-utils2])
  (:use [clojure.contrib.duck-streams :only (copy to-byte-array)]
        [clojure.contrib.java-utils :only (file)]
        [clojure.contrib.str-utils :only (str-join)])
  (:import [java.security MessageDigest]
           [java.util.zip ZipEntry ZipOutputStream]))

(defn- song-filename
  "Converts a song struct to a filename appropriate for the mp3."
  [song]
  (let [escaped-title (str-utils2/replace (song :title) "/" "_")]
    (str (song :track) " " escaped-title ".mp3")))

(defn zip-files
  "Given a list of songs, dumps them zipped into ostream."
  [artist album songs ostream]
  (let [dir-name (str artist " - " album "/")]
    (with-open [out (ZipOutputStream. ostream)]
      (doseq [song songs]
        (.putNextEntry out (ZipEntry. (str dir-name (song-filename song))))
        (copy (file (song :path)) out)
        (.closeEntry out)))))

(defn sha-1
  "Generates a SHA-1 hash of the given input plaintext.

  => (sha-1 \"hi\")
  \"c22b5f917834269428d6f51b2c5af4cbde6a42\"
  "
  [input]
  (let [md (MessageDigest/getInstance "SHA-1")]
    (. md update (to-byte-array input))
    (let [digest (.digest md)]
      (str-join "" (map #(Integer/toHexString (bit-and % 0xff)) digest)))))
