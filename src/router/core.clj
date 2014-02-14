(ns router.core
  (:gen-class)
  (:use [clojure.string
         :only [replace]
         :rename {replace str-replace}]))

(defn- build-route [[url-spec handler]]
  (let [regex (re-pattern (str-replace url-spec #":.+?\/" "(.+?)/"))]
    `(fn [url#]
       (when-let [match# (re-matches ~regex url#)]
         (if (coll? match#)
           (fn [] (apply ~handler (drop 1 match#)))
           (fn [] (~handler)))))))

(defn not-found [] (println "404 Not Found"))

(defmacro defroutes [& route-forms]
  (let [handlers (map build-route (partition 2 route-forms))]
    `(def routes [~@handlers (fn [url#] (fn [] (not-found)))])))

(defroutes
  "/router/both/"            (fn [] (println "router both"))
  "/only/controller/"        (fn [] (println "only controller"))
  "/router/params/:x/:y/:z/" (fn [x y z] (println x y z)))

(defmacro with-handler [[var url] & body]
  `(let [url# ~url
         handler# (some #(% url#) routes)
         ~var handler#]
     (do ~@body)))

(defn route [url]
  (with-handler [handler url]
    (handler)))

(defn -main [& args]
  (route "/router/both/")
  (route "/only/controller/")
  (route "/router/params/1/2/3/")
  (route "NOPE"))
