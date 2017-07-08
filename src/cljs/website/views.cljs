(ns website.views
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [reagent.core :as reagent :refer [atom] :rename {atom ratom}]
            [bidi.bidi :as bidi :refer [path-for]]
            [website.routes :refer [app-routes]]))

(defmulti page-contents identity)

(defn index-panels [header content]
  [:div.col-xs-4
   [:div.panel.panel-default
    [:div.panel-heading
     [:h3.panel-title header]]
    [:div.panel-body content]]])

(defmethod page-contents :index []
  (fn []
    [:div.container-fluid
      [:div.jumbotron "This is the main section"]
      [:div.row
        [index-panels "Services" "This is the service section"]
        [index-panels "Demos" "This is the demos section"]
        [index-panels "About Us" "This is the about us section"]]]))

(defmethod page-contents :services []
  (fn []
    [:div.container
     [:h1 "Services"]]))

(defmethod page-contents :demos []
  (fn []
    [:div.container
     [:h1 "Demos"]]))

(defmethod page-contents :about-us []
  (fn []
    [:div.container
     [:h1 "About Us"]]))

(defmethod page-contents :default []
  (fn []
    [:div.container
     [:h1 "404: My bad"]
     [:pre.verse
      "This page should be here,
but I never created it."]]))

(defn nav-bar []
  [:nav.navbar.navbar-default
   [:div.container-fluid
    [:div.navbar-header
     [:button.navbar-toggle {:type "button" :data-toggle "collapse" :data-target "#myNavbar"}
      [:span.icon-bar]
      [:span.icon-bar]
      [:span.icon-bar]]
      [:a.navbar-brand {:href (path-for app-routes :index) } "mrkdnl.io"]]
    [:div#myNavbar.collapse.navbar-collapse
      [:ul.nav.navbar-nav
      [:li [:a {:href (path-for app-routes :services) } "services"]]
      [:li [:a {:href (path-for app-routes :demos) } "demos"]]
      [:li [:a {:href (path-for app-routes :about-us) } "about us"]]]]]])

(defn page []
  (fn []
    (let [page (:current-page @(subscribe [:route]))]
      [:div
       [nav-bar]
       ^{:key page} [page-contents page]])))
