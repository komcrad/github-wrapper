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
  (if (empty? @token)
    (throw (Exception. "token not defined"))
    (http/get url {:headers {"Authorization"
                             (str "token " @token)}})))

(defn get-followers
  "Returns a future containing followers of user"
  [user]
  (authed-get (str "https://api.github.com/users/" user
                    "/followers?per_page=5")))

(defn parse-followers
  "Resolves a future containing futures and returns the information
   as a map"
  [followers]
  (map #(do {:id (:login %)})
       (keywordize-keys (json/read-str (:body @followers)))))

(defn assoc-follows-async
  "Given a map with {:id github-user-name} associates a :followers
   key. The value of this key will be a future."
  [m]
  (postwalk #(cond
               (and (map? %) (nil? (:followers %)))
               (assoc % :followers (get-followers (:id %)))
               :else %) m))

(defn resolve-async-follows
  "Given a map, resolves all futures into lists of user ids"
  [m]
  (postwalk #(cond
               (s/includes? (str (type %)) "org.httpkit.client$deadlock")
               (parse-followers %)
               :else %) m))

(defn followers
  "given a github usernames, returns that users followers and their followers
   recursivley 3 levels deep. Each level's followers are gathered in parallel"
  [user]
  (loop [m {:id user} depth 0]
    (if (= 3 depth)
      m
      (let [async (assoc-follows-async m)]
        (recur (resolve-async-follows async) (inc depth))))))
