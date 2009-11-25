(ns michmusic.serve
  (:use compojure
        michmusic.database
        michmusic.controller))

(run-server {:port 8080} 
            "/*"
            (servlet webservice))

;(.start
; (Thread.
;  (load-song-db)))
