(ns github-wrapper.core
  (:gen-class)
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.string :as s]
            [clojure.walk :refer [prewalk postwalk keywordize-keys]]))

(defonce token (atom ""))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (reset! token (first args)))

(defn authed-get [url]
  (http/get url {:headers {"Authorization"
                           (str "token " @token)}}))

(defn get-followers [user]
  (authed-get (str "https://api.github.com/users/" user
                    "/followers?per_page=5")))

(defn parse-followers [followers]
  (map #(do {:id (:login %)})
       (keywordize-keys (json/read-str (:body @followers)))))

(defn assoc-follows-async [m]
  (postwalk #(cond
               (and (map? %) (nil? (:followers %)))
               (assoc % :followers (get-followers (:id %)))
               :else %) m))

(defn resolve-async-follows [m]
  (postwalk #(cond
               (s/includes? (str (type %)) "org.httpkit.client$deadlock")
               (parse-followers %)
               :else %) m))

(defn followers [user]
  (loop [m {:id user} depth 0]
    (if (= 3 depth)
      m
      (let [async (assoc-follows-async m)]
        (recur (resolve-async-follows async) (inc depth))))))
