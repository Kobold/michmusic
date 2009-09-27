(ns michmusic.serve
  (:use compojure
        michmusic.database
        michmusic.main)
  (:import [java.util.logging LogManager]
           [java.io StringBufferInputStream]))

; disable jaudiotagger's crazy logging
(let [stream (StringBufferInputStream. "org.jaudiotagger.level = OFF")]
  (.. (LogManager/getLogManager)
      (readConfiguration stream)))

(run-server {:port 8080} 
            "/*"
            (servlet webservice))

(.start
 (Thread.
  (load-song-db)))
