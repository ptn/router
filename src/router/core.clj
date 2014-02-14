(ns router.core
  (:gen-class)
  (:require [clojure.string :as str]))

(defn- build-route [[url-spec handler]]
  (let [regex (re-pattern (str/replace url-spec #":.+?\/" "(.+?)/"))]
    (fn [url]
      (when-let [match (re-matches regex url)]
        (if (coll? match)
          (fn [] (apply handler (drop 1 match)))
          (fn [] (handler)))))))

(defn not-found [] (println "404 Not Found"))

(defn build-routes [& route-forms]
  (let [handlers (mapv build-route (partition 2 route-forms))]
    (conj handlers (fn [url] not-found))))

(defn handler-for [url routes]
  (some #(% url) routes))

(defn route [url routes]
  (let [handler (handler-for url routes)]
    (handler)))

(defn -main [& args]
  (let [routes (build-routes
                 "/router/both/"            (fn [] (println "router both"))
                 "/only/controller/"        (fn [] (println "only controller"))
                 "/router/params/:x/:y/:z/" (fn [x y z] (println x y z)))]
    (route "/router/both/" routes)
    (route "/only/controller/" routes)
    (route "/router/params/1/2/3/" routes)
    (route "NOPE" routes)))
