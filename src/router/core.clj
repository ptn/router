(ns router.core
  (:gen-class)
  (:require [router.routes :as routes]))

(defn -main [& args]
  (let [route (routes/router-fn ["/router/one/"   (fn [] (println "router one"))
                                 "/router/two/a/" (fn [] (println "router two a"))
                                 "/router/two/b/" (fn [] (println "router two b"))
                                 "/other/base/"   (fn [] (println "other base"))
                                 "/router/three/" (fn [] (println "router three"))
                                 "/router/params/:x/:y/3/" (fn [x y] (println x y))])]
    (route "/router/two/b/")
    (route "/router/two/a/")
    (route "/router/three/")
    (route "/other/base/")
    (route "/NOPE/")
    (route "/router/params/1/2/3/")
    (route "/router/params/1/2/33333/")
    (route "/router/params/1/2/3/4/5/6/")))
