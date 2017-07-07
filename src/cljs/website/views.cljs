(ns website.views
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [reagent.core :as reagent :refer [atom] :rename {atom ratom}]
            [bidi.bidi :as bidi :refer [path-for]]
            [website.routes :refer [app-routes]]))

(defmulti page-contents identity)

(defmethod page-contents :index []
  (fn []
    [:span
     [:h1 "Routing example: Index"]
     [:ul
      [:li [:a {:href (path-for app-routes :section-a) } "Section A"]]
      [:li [:a {:href (path-for app-routes :section-b) } "Section B"]]
      [:li [:a {:href (path-for app-routes :missing-route) } "Missing-route"]]
      [:li [:a {:href "/borken/link" } "Borken link"]]]]))

(defmethod page-contents :section-a []
  (fn []
    [:span
     [:h1 "Routing example: Section A"]
     [:ul (map (fn [item-id]
                 [:li {:key (str "item-" item-id)}
                  [:a {:href (path-for app-routes :a-item :item-id item-id)} "Item: " item-id]])
               (range 1 6))]]))

(defmethod page-contents :a-item []
  (fn []
    (let [routing-data @(subscribe [:route])
          item (get-in routing-data [:route-params :item-id])]
      [:span
       [:h1 (str "Routing example: Section A, item " item)]
       [:p [:a {:href (path-for app-routes :section-a)} "Back to Section A"]]])))

(defmethod page-contents :section-b []
  (fn [] [:span
          [:h1 "Routing example: Section B"]]))

(defmethod page-contents :four-o-four []
  "Non-existing routes go here"
  (fn []
    [:span
     [:h1 "404: It is not here"]
     [:pre.verse
      "What you are looking for,
I do not have.
How could I have,
what does not exist?"]]))

(defmethod page-contents :default []
  "Configured routes, missing an implementation, go here"
  (fn []
    [:span
     [:h1 "404: My bad"]
     [:pre.verse
      "This page should be here,
but I never created it."]]))

(defn page []
  (fn []
    (let [page (:current-page @(subscribe [:route]))]
      [:div
       [:p [:a {:href (path-for app-routes :index) } "Go home"]]
       [:hr]
       ^{:key page} [page-contents page]
       [:hr]
       [:p "(Using "
        [:a {:href "https://reagent-project.github.io/"} "Reagent"] ", "
        [:a {:href "https://github.com/juxt/bidi"} "Bidi"] " & "
        [:a {:href "https://github.com/venantius/accountant"} "Accountant"]
        ")"]])))
