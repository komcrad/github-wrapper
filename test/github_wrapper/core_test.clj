(ns github-wrapper.core-test
  (:require [clojure.test :refer :all]
            [github-wrapper.core :refer :all]
            [clojure.data.json :as json]))

(deftest followers-test
  (testing "Followers"
    (is (thrown? Exception (followers "komcrad")))
    (reset! token (System/getenv "GITHUB_API_KEY"))
    (is (= "{\"id\":\"komcrad\",\"followers\":[{\"id\":\"cdzwm\",\"followers\":[{\"id\":\"eggcaker\",\"followers\":[{\"id\":\"cdzwm\"},{\"id\":\"alexbosworth\"},{\"id\":\"ijuhoor\"},{\"id\":\"marcwan\"},{\"id\":\"JSansalone\"}]},{\"id\":\"yckit\",\"followers\":[]},{\"id\":\"WuLex\",\"followers\":[{\"id\":\"36lean\"},{\"id\":\"angusshire\"},{\"id\":\"comecross\"},{\"id\":\"MichalPaszkiewicz\"},{\"id\":\"Mati365\"}]},{\"id\":\"cl11922587\",\"followers\":[{\"id\":\"bugknightyyp\"},{\"id\":\"mssalvo\"},{\"id\":\"hamzaaladwan\"},{\"id\":\"brunocasanova\"},{\"id\":\"angusshire\"}]},{\"id\":\"Sssssusan\",\"followers\":[{\"id\":\"developer787\"},{\"id\":\"mstraughan86\"}]}]}]}"
           (json/write-str (followers "komcrad"))))))
