(ns router.core
  (:gen-class)
  (:require [router.routes :as routes]))

(defn route [url routes]
  (let [handler (routes/handler-for url routes)]
    (handler)))

(defn -main [& args]
  (let [routes (routes/build
                "/router/one/"   (fn [] (println "router one"))
                "/router/two/a/" (fn [] (println "router two a"))
                "/router/two/b/" (fn [] (println "router two b"))
                "/other/base/"   (fn [] (println "other base"))
                "/router/three/" (fn [] (println "router three")))]
    (println routes)))
                ;; "/only/controller/"        (fn [] (println "only controller"))
                ;; "/router/params/:x/:y/:z/" (fn [x y z] (println x y z)))]
    ;; (route "/router/both/" routes)
    ;; (route "/only/controller/" routes)
    ;; (route "/router/params/1/2/3/" routes)
    ;; (route "NOPE" routes)))
