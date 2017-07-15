(ns website.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :route
 (fn [db]
   (:route db)))

(re-frame/reg-sub
 :scroll-y
 (fn [db]
   (:scroll-y db)))
