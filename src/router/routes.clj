;; TODO
;;
;; * compile the tree into closures

(ns router.routes
  (:gen-class)
  (:require [router.tokens :as tks]))

(declare merge-nodes)

(defn not-found [] (println "404 Not Found"))

(defn handler-for [url router]
  (if-let [match (router url)]
    match
    not-found))

(defn- insert-one [tree node]
  (if-let [match (some #(when (= (:root %) (:root node))
                          %)
                       tree)]
    (replace {match (merge-nodes match node)} tree)
    (conj tree node)))

(defn insert-all [tr1 tr2]
  (if (zero? (count tr2))
    tr1
    (recur (insert-one tr1 (first tr2)) (rest tr2))))

(defn- merge-nodes
  "Merge nodes that have the same :root"
  [old new]
  (cond
   ;; these two mean that one node is a special match or not-found node,
   ;; so just return the other.
   (empty? (:children old)) new
   (empty? (:children new)) old
   :else {:root (:root old)
          :children (insert-all (:children old)
                                (:children new))
          :handler nil}))

(defn- build-one [url-spec handler]
  (if-let [tail (tks/rest url-spec)]
    {:root (tks/first url-spec)
     :children (list (build-one tail handler))
     :handler nil}
    {:root (tks/first url-spec)
     :children (list {:root nil
                      :children ()
                      :handler handler})
     :handler nil}))

(defn- build-all [route-forms]
  (if (= (count route-forms) 1)
    (list (build-one (ffirst route-forms) (second (first route-forms))))
    (insert-one (build-all (rest route-forms))
            (build-one (ffirst route-forms) (second (first route-forms))))))

(defn compile-one [node]
  (if (empty? (:children node))
    (fn [url]
      (when (= (:root node) (tks/first url))
        (:handler node)))
    (let [compiled-ch (map compile-one (:children node))]
      (fn [url]
        (when (= (:root node) (tks/first url))
          (some #(% (tks/rest url)) compiled-ch))))))

(defn compile-tree [tree]
  (let [compiled-nodes (map compile-one tree)]
    (fn [url] (some #(% url) compiled-nodes))))

(defn build [& route-forms]
  (-> route-forms build-all compile-tree))
