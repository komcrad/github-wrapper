(ns github-wrapper.core
  (:gen-class)
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
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

(defn get-followers-seq [coll]
  (loop [id (map #(do {:id %}) coll)
         follows (map parse-followers (map get-followers coll))
         result []]
    (if (empty? id)
      result
      (recur (rest id) (rest follows)
             (conj result (assoc (first id) :followers (first follows)))))))

(defn followers-of [user]
  {:id user :followers (parse-followers (get-followers user))})

(defn followers-of-followers [m]
  (let [followers (map :id (:followers m))
        async (get-followers-seq followers)]
    (assoc m :followers async)))

(defn assoc-followers [m]
  (postwalk #(if (and (not (nil? (:followers %)))
                      (nil? (:followers (first (:followers %)))))
               (followers-of-followers %) %) m))

(defn followers [user]
  (loop [followers (followers-of user) depth 0]
    (if (< depth 2)
      (recur (assoc-followers followers) (inc depth))
      followers)))
