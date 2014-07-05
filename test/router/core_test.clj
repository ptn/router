(ns router.core-test
  (:require [clojure.test :refer :all]
            [router.core :refer :all])
  (:use [router.routes :only [with-router]]))

(defmacro assert-throws404
  [router-call]
  `(try
     ~router-call
     (catch Exception ex# (is (= "404 Not Found" (.getMessage ex#))))))

(deftest router
  (with-router route ["/router/one/"   (fn [] "router one")
                      "/router/two/a/" (fn [] "router two a")
                      "/router/two/b/" (fn [] "router two b")
                      "/other/base/"   (fn [] "other base")
                      "/router/three/" (fn [] "router three")
                      "/router/params/:x/:y/3/" (fn [x y] [x y])]
    (is (= "router two b") (route "/router/two/b/"))
    (is (= "router tow a") (route "/router/two/a/"))
    (is (= "router three") (route "/router/three/"))
    (is (= "other base") (route "/other/base/"))
    (is (= ["1" "2"] (route "/router/params/1/2/3/")))
    (assert-throws404 (route "/NOPE/"))
    (assert-throws404 (route "/router/params/1/2/33333/"))
    (assert-throws404 (route "/router/params/1/2/3/4/5/6/"))))
