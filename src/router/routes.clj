;; TODO
;;
;; * compile the tree into closures

(ns router.routes
  (:gen-class)
  (:require [router.tokens :as tks]))

(defn not-found [] (println "404 Not Found"))

(defn handler-for [url routes]
  (some #(% url) routes))

(defn- merge-nodes [old new]
  (if (or (empty? (:children old))
          (empty? (:children new)))
    {:root (:root old)
     :children (into (:children old)
                     (:children new))
     :handler nil}))

(defn- insert [tree node]
  (if-let [match (some #(when (= (:root %) (:root node))
                          %)
                       tree)]
    (replace {match (merge-nodes match node)} tree)
    (conj tree node)))

(defn- build-one [url-spec handler]
  (if-let [tail (tks/rest url-spec)]
    {:root (tks/first url-spec)
     :children (list (build-one tail handler))
     :handler nil}
    {:root (tks/first url-spec)
     :children ()
     :handler handler}))

(defn- build-all [route-forms]
  (if (= (count route-forms) 1)
    (list (build-one (ffirst route-forms) (second (first route-forms))))
    (insert (build-all (rest route-forms))
            (build-one (ffirst route-forms) (second (first route-forms))))))

(defn build [& route-forms]
  (-> (partition 2 route-forms) build-all)) ;;compile-tree))
