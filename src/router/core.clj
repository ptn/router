(ns router.core
  (:gen-class)
  (:require [router.routes :as routes]))

(defn route [url routes]
  (let [[handler captures] (routes/handler-for url routes)]
    (apply handler captures)))

(defn -main [& args]
  (let [routes (routes/build
                ["/router/one/"   (fn [] (println "router one"))]
                ["/router/two/a/" (fn [] (println "router two a"))]
                ["/router/two/b/" (fn [] (println "router two b"))]
                ["/other/base/"   (fn [] (println "other base"))]
                ["/router/three/" (fn [] (println "router three"))]
                ["/router/params/:x/:y/3/" (fn [x y] (println x y))])]
    ;; "/only/controller/"        (fn [] (println "only controller"))))
    (route "/router/two/b/" routes)
    (route "/router/two/a/" routes)
    (route "/router/three/" routes)
    (route "/other/base/" routes)
    (route "/NOPE/" routes)
    (route "/router/params/1/2/3/" routes)
    (route "/router/params/1/2/33333/" routes)
    (route "/router/params/1/2/3/4/5/6/" routes)))
