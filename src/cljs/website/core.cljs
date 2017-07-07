(ns website.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [dispatch dispatch-sync]]
            [re-frisk.core :refer [enable-re-frisk!]]
            [accountant.core :as accountant]
            [bidi.bidi :as bidi :refer [match-route]]
            [website.events]
            [website.subs]
            [website.views :as views]
            [website.config :as config]
            [website.routes :refer [app-routes]]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (enable-re-frisk!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/page]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (dev-setup)
  (dispatch-sync [:initialize-db])
  (accountant/configure-navigation!
   {:nav-handler (fn
                   [path]
                   (let [match (match-route app-routes path)
                         current-page (:handler match)
                         route-params (:route-params match)]
                     (dispatch [:update-route {:current-page current-page
                                               :route-params route-params}])))
    :path-exists? (fn [path]
                    (boolean (match-route app-routes path)))})
  (accountant/dispatch-current!)
  (mount-root))
