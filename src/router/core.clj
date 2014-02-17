(ns router.core
  (:gen-class)
  (:require [clojure.string :as str])
  (:require [router.routes :as routes]))

(defn route [url routes]
  (let [handler (routes/handler-for url routes)]
    (handler)))

(defn -main [& args]
  (let [routes (routes/build
                "/router/one/" (fn [] (println "router one"))
                "/router/two/" (fn [] (println "router two"))
                "/other/base/" (fn [] (println "other base")))]
    (println routes)))
                ;; "/only/controller/"        (fn [] (println "only controller"))
                ;; "/router/params/:x/:y/:z/" (fn [x y z] (println x y z)))]
    ;; (route "/router/both/" routes)
    ;; (route "/only/controller/" routes)
    ;; (route "/router/params/1/2/3/" routes)
    ;; (route "NOPE" routes)))
