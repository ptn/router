(ns router.routes
  (:gen-class)
  (:require [clojure.string :as str]
            [router.tokens :as tks]))

(defn not-found [] (println "404 Not Found"))

(defn handler-for [url routes]
  (some #(% url) routes))

(defn- merge-nodes [old new]
  {:root (:root old)
   :data {:children (into (:children (:data old))
                          (:children (:data new)))
          :handler nil}})

(defn- insert [tree node]
  (if-let [match (some #(when (= (:root %) (:root node))
                          %)
                       tree)]
    (replace {match (merge-nodes match node)} tree)
    (conj tree node)))

(defn- build-one [url-spec handler]
  (if-let [tail (tks/rest url-spec)]
    (let [children [(build-one tail handler)]]
      {:root (tks/first url-spec)
       :data {:children children :handler nil}})
    {:root (tks/first url-spec)
     :data {:children nil :handler handler}}))

(defn- build-all [route-forms]
  (if (= (count route-forms) 1)
    [(apply build-one (first route-forms))]
    (insert (build-all (rest route-forms))
            (apply build-one (first route-forms)))))

(defn build [& route-forms]
  (-> (partition 2 route-forms) build-all)) ;;compile-tree))
