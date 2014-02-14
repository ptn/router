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

(def routes
  (build-routes
    "/router/both/"            (fn [] (println "router both"))
    "/only/controller/"        (fn [] (println "only controller"))
    "/router/params/:x/:y/:z/" (fn [x y z] (println x y z))))

(defn handler-for [url]
  (some #(% url) routes))

(defn route [url]
  (let [handler (handler-for url)]
    (handler)))

(defn -main [& args]
  (route "/router/both/")
  (route "/only/controller/")
