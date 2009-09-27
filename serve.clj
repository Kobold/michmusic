(ns michmusic.serve
  (:use compojure michmusic.main))

(.start
 (Thread.
  (load-song-db)))
(run-server {:port 8080} 
            "/*"
            (servlet webservice))
