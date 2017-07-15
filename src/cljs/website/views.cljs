(ns website.views
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [reagent.core :as reagent :refer [atom] :rename {atom ratom}]
            [bidi.bidi :as bidi :refer [path-for]]
            [website.routes :refer [app-routes]]
            [cljs.core.async :refer [<! put! chan timeout]]
            [goog.events :as events]
            [goog.events.EventType :as EventType]
            [goog.dom :as dom]))

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
      [:div.jumbotron "Lorem Ipsum
\"Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...\"
\"There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain...\"

Lorem ipsum dolor sit amet, consectetur adipiscing elit. In blandit purus eget justo maximus efficitur. In mattis elementum mi, id faucibus lectus eleifend id. Ut eu feugiat metus, eu pellentesque orci. Nulla quis pharetra ligula. In nec vulputate erat. Quisque vitae enim ligula. In sit amet ipsum sed quam vehicula porta. Donec vitae lorem sit amet magna tincidunt finibus. Sed dui augue, elementum a purus et, efficitur varius nisl. Quisque cursus, lacus id iaculis egestas, sem nulla egestas risus, at auctor felis felis placerat sapien.

Aliquam id sem scelerisque ipsum dignissim scelerisque. Donec eu erat id turpis varius finibus. Suspendisse dignissim ante lorem, non dignissim sem dignissim sit amet. Quisque luctus neque quis mauris dignissim tincidunt. Proin sodales enim non consequat feugiat. Pellentesque ultricies nulla in quam auctor placerat. Quisque nulla leo, lacinia in vehicula non, condimentum ac lorem.

Phasellus a mattis leo. Etiam ut fermentum nibh. Vestibulum ac diam rutrum, commodo lorem quis, posuere quam. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Quisque et felis vulputate, tincidunt erat sit amet, blandit ante. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla pellentesque suscipit nisl at facilisis. Morbi eget neque ac quam aliquet scelerisque semper volutpat nulla. Nulla a urna laoreet, molestie ligula nec, elementum justo. Pellentesque efficitur commodo est, id tincidunt nisl feugiat et. Nullam eu sem id lacus blandit blandit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla feugiat odio nunc, at ultrices urna euismod id. Nam fringilla felis non lacinia egestas.

Nam efficitur purus sit amet ipsum vehicula malesuada. Aenean urna diam, tempus sed orci a, ultrices sollicitudin dui. Sed sagittis eleifend sodales. Nullam elementum congue molestie. Nunc cursus molestie feugiat. Duis mattis id magna ut mollis. Integer sollicitudin dapibus lectus eu porttitor. Aenean laoreet tellus sit amet euismod vehicula. Nulla condimentum est nec lorem suscipit ornare.

Aliquam venenatis ultricies tellus mattis vehicula. Maecenas pellentesque viverra urna vehicula rutrum. Integer scelerisque eros non purus scelerisque porta. Aenean tristique suscipit orci sed dignissim. Nam eleifend nibh ac velit eleifend, nec ullamcorper massa sagittis. Duis sodales vitae diam in ornare. Fusce cursus nibh sit amet ipsum fermentum blandit. Mauris viverra turpis eros, eu varius risus imperdiet a. Donec sit amet placerat enim. Sed tincidunt nunc in pellentesque finibus. Morbi sit amet arcu ut libero rhoncus rhoncus ac vel nibh."]
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

(defn- get-scroll []
  (-> (dom/getDocumentScroll) (.-y)))

(defn- events->chan [el event-type c]
  (events/listen el event-type #(put! c %))
  c)

(defn scroll-chan []
  (events->chan js/window EventType/SCROLL (chan 1 (map get-scroll))))

(defn listen! []
  (let [chan (scroll-chan)]
    (go-loop []
      (dispatch [:update-scroll-y (<! chan)])
      (recur))))

(listen!)

(defn hidden-letter [letter]
  (let [scroll-y @(subscribe [:scroll-y])]
    [:span {:style {:font-size (str (* 35 (/ (max 0 (- 100 scroll-y)) 100)) "px")
                    ;;:font-family "Segoe UI"
                    ;;:font-weight (when (> scroll-y 0) "lighter")
                    }} letter]))

(defn reverse-hidden-letter [letter]
  (let [scroll-y @(subscribe [:scroll-y])]
    [:span {:style {:font-size (str (* 18 (min 1 (/ scroll-y 100))) "px")
                    ;;:font-family "Segoe UI"
                    ;;:font-weight (when (> scroll-y 0) "lighter")
                    }} letter]))

(defn nav-bar []
  (let [scroll-y @(subscribe [:scroll-y])]
    [:nav.navbar.navbar-default.navbar-fixed-top
     [:div.container-fluid
      [:div.navbar-header #_{:style {:overflow "hidden"}}
       [:a.navbar-brand
        {:href (path-for app-routes :index)
         :style {;;:font-family "Segoe UI"
                 ;;:font-weight (when (and #_(not= scroll-y 0) (< scroll-y 100)) "bold")
                 :font-size (str (+ 18 (max 0 (* 17 (/ (- 100 scroll-y) 100)))) "px")
                 ;;:letter-spacing "-1px"
                 }}
        "m"
        [hidden-letter "a"]
        "rk"
        [hidden-letter " & "]
        "d"
        [hidden-letter "a"]
        "n"
        [hidden-letter "ie"]
        "l"
        [reverse-hidden-letter ".io"]]
       [:button.navbar-toggle {:type "button" :data-toggle "collapse" :data-target "#myNavbar"}
        [:span.icon-bar]
        [:span.icon-bar]
        [:span.icon-bar]]]
      [:div#myNavbar.collapse.navbar-collapse
       [:ul.nav.navbar-nav
        [:li [:a {:href (path-for app-routes :services) } "services"]]
        [:li [:a {:href (path-for app-routes :demos) } "demos"]]
        [:li [:a {:href (path-for app-routes :about-us) } "about us"]]]]]]))

(defn page [] (fn []
(let [page (:current-page @(subscribe [:route]))]
[:div
[nav-bar]
^{:key page} [page-contents page]])))
