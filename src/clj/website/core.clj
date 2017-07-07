(ns website.core
  (:require [bidi.ring :refer [make-handler ->ResourcesMaybe]]
            [ring.util.response :as res]
            [clojure.java.io :as io]
            [ring.middleware.reload :refer [wrap-reload]]))

(def handler
  (make-handler ["/" [["api" {"/test" (fn [_] (res/response "test"))}]
                      ["" (->ResourcesMaybe {:prefix "public/"})]
                      [true (fn [_] (res/content-type (res/resource-response "public/index.html") "text/html"))]]]))

(def dev-handler (wrap-reload #'handler))
