;; TODO
;;
;; * deal with missing / at the beginning or end - for now assume they are
;;   always there

(ns router.tokens
  (:gen-class)
  (:refer-clojure :exclude [first rest])
  (:require [clojure.string :as str]))

(defn first [url]
   (when (string? url)
     (if (empty? url)  ""
       (let [a (str/split url #"/")]
          (if  (seq a)  (let [url (nth a 0) ](if (empty? url) (nth a 1)  url)) "")))))


(defn rest [url]
   (when (string? url)
     (if (empty? url)  ""
       (let [a (str/split url #"/")]
         (if (seq a)
           (let [url (nth a 0) ]
             (if (empty? url)
               (str "/"(str/join #"/" (subvec a 2)))
               (str "/"(str/join #"/" (subvec a 1))))) "")))))

(defn all [url]
  (drop 1 (str/split url #"/")))

(defn last? [url]
  (when-not (empty? url)
    (= "" (rest url))))
