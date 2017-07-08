(ns website.routes)

(def app-routes
  ["/"
   [["" :index]
    ["services" :services]
    ["demos" :demos]
    ["about" :about-us]
    [true :default]]])
