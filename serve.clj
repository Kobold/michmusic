(ns michmusic.serve
  (:use compojure michmusic.main))

(run-server {:port 8080} 
            "/*"
            (servlet webservice))
