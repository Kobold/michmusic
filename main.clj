(ns michmusic.main
  (:use compojure)
  (:import [java.io File]))

(def *music-directory* "/Users/kobold/Music")

(defn list-mp3 []
  (let [is-mp3? (fn [file] (.. file getName (endsWith ".mp3")))
        dir (File. *music-directory*)]
    (assert (.isDirectory dir))
    (filter is-mp3? (file-seq dir))))

(defn html-doc 
  [title & body] 
  (html 
   (doctype :html4) 
   [:html 
    [:head 
     [:title title]] 
    [:body 
     [:div 
      [:h2 
       [:a {:href "/"} "Home"]]]
     body]])) 

(def mp3-page
     (html-doc "MP3s"
       [:ul
        (map #(vector :li %)
             (list-mp3))]))

(defroutes webservice
  (GET "/" mp3-page)) 
