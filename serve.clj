(ns michmusic.serve
  (:require [cupboard.core :as cb])
  (:use compojure
        michmusic.database
        michmusic.controller))

(cb/open-cupboard! "/tmp/songs")

(run-server {:port 8080} 
            "/*"
            (servlet webservice))

;(.start
; (Thread.
;  (load-song-db)))
