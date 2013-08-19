;;--------------------------------------------------
;; File server.clj
;; Written by Chris Frisz
;; Created 12 Aug 2013
;; Last updated 12 Aug 2013
;;
;; Core file for chaser game. Doesn't do much yet
;;--------------------------------------------------

(ns chaser-cljs.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :as resources]
            [ring.util.response :as response])
  (:gen-class))

(defn render-app []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body
   (str "<!DOCTYPE html>"
        "<html>"
        "<body>"
        "<div id=\"game\">"
        "Hello"
        "</div>"
        "<script src=\"js/cljs.js\"></script>"
        "</body>"
        "</html>")})

;; all roads lead to the app
(defn handler [request] (render-app))

(def app
  (-> handler
    (resources/wrap-resource "public")))

(defn -main [& args]
  (jetty/run-jetty app {:port 3000}))
