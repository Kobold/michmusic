(ns michmusic.test
  (:require [michmusic.controller :as c]
            [michmusic.database :as db]
            [michmusic.html :as h])
  (:use [clojure.contrib.def :only (defvar-)]
        clojure.test))

(deftest t-import
  (do (db/import-file (java.io.File. "test/test.mp3"))
      (is (= @db/song-db
             #{{:title "Intro"
                :track 1
                :album "Devin Dazzle And The Neon Fever"
                :artist "Felix Da Housecat"
                :sha "92d69686d9766cfb77c25c1c786cebce39a561b1"
                :year nil
                :path "test/test.mp3"}}))))

(deftest t-song-path
  (do (db/import-file (java.io.File. "test/test.mp3"))
      (is (= (db/song-path "92d69686d9766cfb77c25c1c786cebce39a561b1")
             "test/test.mp3"))))

(deftest t-album-display
  (let [songs-by-album
        [["Antidotes" 2008]
         [{:title "B" :track 9 :album "Antidotes" :artist "Foals" :year 2008 :sha "bob"}
          {:title "H" :track 7 :album "Antidotes" :artist "Foals" :year 2008 :sha "bob"}]]
        html [[:div.album-header [:h3 [:span.year 2008] "Antidotes"]]
              [:ul
               [[:li 9 " "
                 [:span.play-button "play"] " "
                 [:a {:href "/file/bob/Foals_B.mp3"} ["B"]]]
                [:li 7 " "
                 [:span.play-button "play"] " "
                 [:a {:href "/file/bob/Foals_H.mp3"} ["H"]]]]]]]
    (is (= (h/album-display songs-by-album) html))))

(defvar- json-response)
(deftest t-artist-info
  (let [[summary pic-url] (c/artist-info (fn [url] json-response)
                                         "Tycho")]
    (is (= (apply str (take 10 summary)) "Tycho is t"))
    (is (= pic-url "http://userserve-ak.last.fm/serve/252/3463494.jpg"))))

(def json-response
      {:body-seq "{
    \"artist\": {
        \"bio\": {
            \"content\": \"<strong>Tycho</strong> is the music project of San Francisco based artist and producer Scott Hansen. Hansen began his foray into electronic music with 2002's <em><a title=\\\"Tycho - The Science of Patterns EP\\\" href=\\\"http://ws.audioscrobbler.com/music/Tycho/The+Science+of+Patterns+EP\\\" class=\\\"bbcode_album\\\">The Science of Patterns EP</a></em> which was followed in 2004 by his first full length, <em><a title=\\\"Tycho - Sunrise Projector\\\" href=\\\"http://ws.audioscrobbler.com/music/Tycho/Sunrise+Projector\\\" class=\\\"bbcode_album\\\">Sunrise Projector</a></em>. 2006 saw the release of <em><a title=\\\"Tycho - Past is Prologue\\\" href=\\\"http://ws.audioscrobbler.com/music/Tycho/Past+is+Prologue\\\" class=\\\"bbcode_album\\\">Past is Prologue</a></em> on the now defunkt IDM label <a href=\\\"http://ws.audioscrobbler.com/label/Merck+Records/\\\" class=\\\"bbcode_label\\\">Merck Records</a>. In 2007 <a href=\\\"http://ws.audioscrobbler.com/label/Ghostly+International/\\\" class=\\\"bbcode_label\\\">Ghostly International</a> released Tycho's first single under that imprint, &quot;The Daydream / The Disconnect&quot;. A new full length album on Ghostly is in the works and slated for a 2009 release.\n \n \n AUDIO: <a href=\\\"http://www.tychomusic.com\\\" rel=\\\"nofollow\\\">www.tychomusic.com</a>\n VISUAL: <a href=\\\"http://www.iso50.com/\\\" rel=\\\"nofollow\\\">ISO50.com</a>\n GHOSTLY: <a href=\\\"http://www.ghostly.com/1.0/artists/tycho/index.shtml\\\" rel=\\\"nofollow\\\">Artist Page</a>\n MYSPACE: <a href=\\\"http://www.myspace.com/tycho\\\" rel=\\\"nofollow\\\">Myspace.com/tycho</a>\", 
            \"published\": \"Fri, 3 Apr 2009 11:36:48 +0000\", 
            \"summary\": \"Tycho is the music project of San Francisco based artist and producer Scott Hansen. Hansen began his foray into electronic music with 2002's <a title=\\\"Tycho - The Science of Patterns EP\\\" href=\\\"http://ws.audioscrobbler.com/music/Tycho/The+Science+of+Patterns+EP\\\" class=\\\"bbcode_album\\\">The Science of Patterns EP</a> which was followed in 2004 by his first full length, <a title=\\\"Tycho - Sunrise Projector\\\" href=\\\"http://ws.audioscrobbler.com/music/Tycho/Sunrise+Projector\\\" class=\\\"bbcode_album\\\">Sunrise Projector</a>. 2006 saw the release of <a title=\\\"Tycho - Past is Prologue\\\" href=\\\"http://ws.audioscrobbler.com/music/Tycho/Past+is+Prologue\\\" class=\\\"bbcode_album\\\">Past is Prologue</a> on the now defunkt IDM label <a href=\\\"http://ws.audioscrobbler.com/label/Merck+Records/\\\" class=\\\"bbcode_label\\\">Merck Records</a>. In 2007 <a href=\\\"http://ws.audioscrobbler.com/label/Ghostly+International/\\\" class=\\\"bbcode_label\\\">Ghostly International</a> released Tycho's first single under that imprint, &quot;The Daydream / The Disconnect&quot;. A new full length album on Ghostly is in the works and slated for a 2009 release. \"
        }, 
        \"image\": [
            {
                \"#text\": \"http://userserve-ak.last.fm/serve/34/3463494.jpg\", 
                \"size\": \"small\"
            }, 
            {
                \"#text\": \"http://userserve-ak.last.fm/serve/64/3463494.jpg\", 
                \"size\": \"medium\"
            }, 
            {
                \"#text\": \"http://userserve-ak.last.fm/serve/126/3463494.jpg\", 
                \"size\": \"large\"
            }, 
            {
                \"#text\": \"http://userserve-ak.last.fm/serve/252/3463494.jpg\", 
                \"size\": \"extralarge\"
            }, 
            {
                \"#text\": \"\", 
                \"size\": \"mega\"
            }
        ], 
        \"mbid\": \"b36f70be-71b1-44d5-9abc-8cd5cc78eb6e\", 
        \"name\": \"Tycho\", 
        \"similar\": {
            \"artist\": [
                {
                    \"image\": [
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/34/2132648.jpg\", 
                            \"size\": \"small\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/64/2132648.jpg>\", 
                            \"size\": \"medium\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/126/2132648.jpg\", 
                            \"size\": \"large\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/252/2132648.jpg\", 
                            \"size\": \"extralarge\"
                        }, 
                        {
                            \"#text\": \"\", 
                            \"size\": \"mega\"
                        }
                    ], 
                    \"name\": \"Boards of Canada\", 
                    \"url\": \"http://www.last.fm/music/Boards+of+Canada\"
                }, 
                {
                    \"image\": [
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/34/22098815.jpg\", 
                            \"size\": \"small\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/64/22098815.jpg>\", 
                            \"size\": \"medium\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/126/22098815.jpg\", 
                            \"size\": \"large\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/252/22098815.jpg\", 
                            \"size\": \"extralarge\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/_/22098815/Ulrich+Schnauss+UlrichSchnauss.jpg\", 
                            \"size\": \"mega\"
                        }
                    ], 
                    \"name\": \"Ulrich Schnauss\", 
                    \"url\": \"http://www.last.fm/music/Ulrich+Schnauss\"
                }, 
                {
                    \"image\": [
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/34/25015967.jpg\", 
                            \"size\": \"small\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/64/25015967.jpg>\", 
                            \"size\": \"medium\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/126/25015967.jpg\", 
                            \"size\": \"large\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/252/25015967.jpg\", 
                            \"size\": \"extralarge\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/500/25015967/Milosh+me2.jpg\", 
                            \"size\": \"mega\"
                        }
                    ], 
                    \"name\": \"Milosh\", 
                    \"url\": \"http://www.last.fm/music/Milosh\"
                }, 
                {
                    \"image\": [
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/34/21184503.jpg\", 
                            \"size\": \"small\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/64/21184503.jpg>\", 
                            \"size\": \"medium\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/126/21184503.jpg\", 
                            \"size\": \"large\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/252/21184503.jpg\", 
                            \"size\": \"extralarge\"
                        }, 
                        {
                            \"#text\": \"\", 
                            \"size\": \"mega\"
                        }
                    ], 
                    \"name\": \"Telefon Tel Aviv\", 
                    \"url\": \"http://www.last.fm/music/Telefon+Tel+Aviv\"
                }, 
                {
                    \"image\": [
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/34/231962.jpg\", 
                            \"size\": \"small\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/64/231962.jpg>\", 
                            \"size\": \"medium\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/126/231962.jpg\", 
                            \"size\": \"large\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/252/231962.jpg\", 
                            \"size\": \"extralarge\"
                        }, 
                        {
                            \"#text\": \"http://userserve-ak.last.fm/serve/_/231962/Proem.jpg\", 
                            \"size\": \"mega\"
                        }
                    ], 
                    \"name\": \"Proem\", 
                    \"url\": \"http://www.last.fm/music/Proem\"
                }
            ]
        }, 
        \"stats\": {
            \"listeners\": \"108794\", 
            \"playcount\": \"1936813\"
        }, 
        \"streamable\": \"1\", 
        \"tags\": {
            \"tag\": [
                {
                    \"name\": \"ambient\", 
                    \"url\": \"http://www.last.fm/tag/ambient\"
                }, 
                {
                    \"name\": \"idm\", 
                    \"url\": \"http://www.last.fm/tag/idm\"
                }, 
                {
                    \"name\": \"electronic\", 
                    \"url\": \"http://www.last.fm/tag/electronic\"
                }, 
                {
                    \"name\": \"chillout\", 
                    \"url\": \"http://www.last.fm/tag/chillout\"
                }, 
                {
                    \"name\": \"downtempo\", 
                    \"url\": \"http://www.last.fm/tag/downtempo\"
                }
            ]
        }, 
        \"url\": \"http://www.last.fm/music/Tycho\"
    }
}"})

(run-tests)
