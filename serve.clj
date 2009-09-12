(ns michmusic.serve
  (:use compojure michmusic.main))

(load-song-db)
(run-server {:port 8080} 
            "/*"
            (servlet webservice))
