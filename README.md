# router

A compiler for a web framework-like router.

It reads pairs of route formats plus functions that handle them and
compiles a function that takes a url and invokes the corresponding
handler, or raise an exception in case of a 404 Not Found.

All of the code is essentially one giant macro, `with-router`. Use it
like this:

```
(with-router route ["/first/route/"  (fn [] (println "handler 1"))
                    "/second/route/" (fn [] (println "handler 2"))
                    "/params/:x/"    (fn [x] (println x))]
    (route "/first/route/")
    (route "/second/route/")
    (route "/params/1/")
    ;; this will throw an exception
    (route "NOPE"))
```

## Todo

I have absolutely no idea why I need to quote that map on line 76 in
`src/router/core.clj`. Clojure tries to invoke it as a function even
though it is not in function position - is that a bug? Investigate.

## License

Copyright © 2014 Pablo Torres

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
