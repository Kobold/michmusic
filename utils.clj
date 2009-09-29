(ns michmusic.utils
  (:use [clojure.contrib.duck-streams :only (to-byte-array)]
        [clojure.contrib.str-utils :only (str-join)])
  (:import [java.security MessageDigest]))

(defn sha
  "Generates a SHA-1 hash of the given input plaintext.

  => (sha \"hi\")
  \"c22b5f917834269428d6f51b2c5af4cbde6a42\"
  "
  [input]
  (let [md (MessageDigest/getInstance "SHA-1")]
    (. md update (to-byte-array input))
    (let [digest (.digest md)]
      (str-join "" (map #(Integer/toHexString (bit-and % 0xff)) digest)))))
