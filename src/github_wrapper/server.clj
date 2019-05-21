(ns github-wrapper.server
  (:gen-class)
  (:require 
    [github-wrapper.core :as c]
    [org.httpkit.server :as http]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [cheshire.core :refer [generate-string]]))

(defn to-json [coll]
  (generate-string coll {:pretty true}))

(defn followers [username]
  {:status 200
   :body (to-json (c/followers username))})

(defroutes app-routes
  (GET "/followers/:username" [username] (followers username))
  (route/not-found "not found"))

(defn -main [& args]
  (reset! c/token (first args))
  (let [handler (wrap-defaults app-routes
                               (assoc-in site-defaults
                                         [:security :anti-forgery] false))]
    (http/run-server handler {:port 8080})
        (println "listening on port 8080")))
