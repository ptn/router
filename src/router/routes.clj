(ns router.routes
  (:gen-class)
  (:require [router.tokens :as tks]))

(declare merge-nodes)
(declare compile-one)

(defn not-found [] (throw (Exception. "404 Not Found")))

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
    ;; we need lists here because conj'ing onto a list essentially
    ;; stores the route specs in reverse order, and later when we
    ;; compile we need to do so backwards, in order to compile the
    ;; node that doesn't depend on any one first and close over that
    ;; compiled result when compiling its parent - once the parent is
    ;; compiled, there's no way to adding a reference to its
    ;; children. that's kinda what compiling means. reversing
    ;; :children would work, but we would need to reverse every
    ;; :children before compiling them. I think using a list is a
    ;; solution just as good.
    (list (build-one (ffirst route-forms) (second (first route-forms))))
    (insert-one (build-all (rest route-forms))
                (build-one (ffirst route-forms) (second (first route-forms))))))

(defn compile-leaf [node]
  (if (= (first (:root node)) \:)
    `(fn [url# captures#]
       [(:handler ~node) (conj captures# (tks/first url#))])
    `(fn [url# captures#]
       (when (= (:root ~node) (tks/first url#))
         [(:handler ~node) captures#]))))

(defn compile-internal [node]
  (let [compiled-ch (mapv compile-one (:children node))]
    (if (= (first (:root node)) \:)
      `(fn [url# captures#]
         (some #(% (tks/rest url#) (conj captures# (tks/first url#)))
               ~compiled-ch))
      `(fn [url# captures#]
         ;; why is that quote needed here wtf
         (when (= (:root '~node) (tks/first url#))
           (some #(% (tks/rest url#) captures#) ~compiled-ch))))))

(defn compile-one [node]
  (if (empty? (:children node))
    (compile-leaf node)
    (compile-internal node)))

(defn compile-tree
  [tree]
  (let [compiled-nodes (mapv compile-one tree)]
    `(fn [url#] (some #(% url# []) ~compiled-nodes))))

(defmacro with-router
  "Compiles the routes spec into a closure that returns the handler
and captured variables, and then invokes the former with the latter as
parameters."
  [name spec & body]
  (let [tree (-> (partition 2 spec) build-all compile-tree)
        matcher `(fn [url#]
                   (if-let [match# (~tree url#)]
                     match#
                     [not-found []]))]
    `(let [~name (fn [url#]
                   (let [[handler# captures#] (~matcher url#)]
                     (apply handler# captures#)))]
       ~@body)))
