(ns router.tokens
  (:gen-class)
  (:require [clojure.string :as str]))

(defn- tokenizer [url f]
  (println url)
  (if (= url "/")
    ""
    ;; Get rid of initial /
    (let [url (subs url 1)
          first-slash-pos (.indexOf url "/")]
      (f url first-slash-pos))))

(defn first [url]
  (tokenizer url
             (fn [cleaned-url first-slash-pos]
               (subs cleaned-url 0 first-slash-pos))))

(defn rest [url]
  (tokenizer url
             (fn [cleaned-url first-slash-pos]
               (subs cleaned-url first-slash-pos))))

(defn all [url]
  (drop 1 (str/split url #"/")))

(defn last? [url]
  (= "" (rest url)))
