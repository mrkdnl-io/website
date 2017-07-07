(ns website.events
  (:require [re-frame.core :as re-frame]
            [website.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
 :update-route
 (fn  [db [_ route]]
   (assoc db :route route)))
