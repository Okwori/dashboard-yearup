(ns yearup.core
  (:require
    [clojure.string :as string]
    [clojure.spec.alpha :as s]
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [yearup.ajax :as ajax]
    [yearup.events]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [tick.locale-en-us]
    [tick.alpha.api :as t])
  (:import goog.History))

(s/def ::r-ratio (s/and integer? pos? #(<= % 100)))

(defn parse-date [tagged]
  (t/format (t/formatter "HH:mm MM/dd/yyyy ") (t/parse (.-rep tagged))))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page])) :is-active)}
   title])

(defn navbar [] 
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-info>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "yearup"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click #(swap! expanded? not)
                  :class (when @expanded? :is-active)}
                 [:span][:span][:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Home" :home]
                 [nav-link "#/about" "About" :about]]]]))

(defn setting-page [] [:p "About"] )

(defn home-page []
  (when-let [report @(rf/subscribe [:page/report])]
    [:div.container.body
    [:div.main_container
     [:div.col-md-3.left_col
      [:div.left_col.scroll-view
       [:div.navbar.nav_title {:style {:border "0"}}
        [:a.site_title {:href "#"} [:span "YearUp Admin"]]]
       [:div.clearfix]
       [:br]
       [:div#sidebar-menu.main_menu_side.hidden-print.main_menu
        [:div.menu_section
         [:ul.nav.side-menu
          [:li [:a {:href "#"} [:i.fa.fa-dashboard] "Dashboard"]]]]]]]
     [:div.top_nav
      [:div.nav_menu
       [:div.nav.toggle
        [:a#menu_toggle [:i.fa.fa-bars]]]
       [:nav.nav.navbar-nav]]]
     [:div.right_col {:role "main"}
      [:div.row {:style {:display "inline-block"}}
       [:div.animated.flipInY.col-lg-3.col-md-3.col-sm-6
        [:div.tile-stats
         [:div.icon [:i.fa.fa-user.blue]]
         [:div.count (:totalNo report)]
         [:h3 "Total"]
         [:p "Number of all the users that responded to the quiz"]]]
       [:div.animated.flipInY.col-lg-3.col-md-3.col-sm-6
        [:div.tile-stats
         [:div.icon [:i.fa.fa-check.green]]
         [:div.count.green (:positiveNo report)]
         [:h3 "Positive"]
         [:p "Number of responses that are positive based on ratio"]]]
       [:div.animated.flipInY.col-lg-3.col-md-3.col-sm-6
        [:div.tile-stats
         [:div.icon [:i.fa.fa-thumbs-down.red]]
         [:div.count (:negativeNo report)]
         [:h3 "Negative"]
         [:p "Number of responses that aren't favorable based on ratio"]]]
       [:div.animated.flipInY.col-lg-3.col-md-3.col-sm-6
        [:div.tile-stats
         [:div.icon [:i.fa.fa-shield]]
         [:div.count (str (:ratio report) "%")]
         [:h3 "Ratio"]
         [:p "A percentage of user's response to be deemed positive"]]]]
      [:div.row
       [:div.col-md-12.col-sm-12
        [:div.col-md-8.col-sm-8
         [:div.x_panel
          [:div.x_title
           [:h2 "Quiz Responses"]
           [:ul.nav.navbar-right.panel_toolbox
            [:li [:a.collapse-link [:i.fa.fa-chevron-up]]]
            [:li [:a.close-link [:i.fa.fa-close]]]]
           [:div.clearfix]]
          [:div.x_content
           [:div.row
            [:div.col-sm-12
             [:div.card-box.table-responsive
              [:p.text-muted.font-13.m-b-30 "This table show all the responses to the Quiz ordered the latest responses"]
              [:table#datatable-buttons.table.table-striped.table-bordered {:style {:width "100%"}}
               [:thead
                [:tr
                 [:th "Email"]
                 [:th "City"]
                 [:th "Time"]
                 [:th "Accepted Responses?"]]]
               [:tbody
                (for [response (:response report)]
                  ^{:key (parse-date (:date response)) }[:tr
                   [:td [:a {:href "#" :data-toggle "modal" :data-target ".bs-example-modal-lg"
                             :on-click #(rf/dispatch [:question-response (:responses response)])} (:email response)]]
                   [:td (:city response)]
                   [:td (parse-date (:date response))]                                   ;
                   (cond (= (:accepted response) "Yes") [:td [:span.badge.badge-success "Yes"]]
                         (= (:accepted response) "No") [:td [:span.badge.badge-danger "No"]])])]]
              [:div.modal.fade.bs-example-modal-lg {:tabIndex "-1" :role "dialog" :aria-hidden "true"}
               [:div.modal-dialog.modal-lg
                [:div.modal-content
                 [:div.modal-header
                  [:h4#myModalLabel.modal-title "Response"]
                  [:button.close {:type "button" :data-dismiss "modal"} [:span {:aria-hidden "true"} "×"]]]
                 [:div.modal-body
                  [:table#datatable-buttons2.table.table-striped.table-bordered {:style {:width "100%"}}
                   [:thead
                    [:tr
                     [:th "Question"]
                     [:th "Option Selected"]]]
                   [:tbody
                    (when-let [current @(rf/subscribe [:get-current-response])]
                      (for [question-response current]
                        ^{:key question-response}[:tr
                                                  [:td (:question question-response)]
                                                  [:td (:option question-response)]]))]]]
                 [:div.modal-footer
                  [:button.btn.btn-secondary {:type "button" :data-dismiss "modal"
                                              :on-click #(rf/dispatch [:page/dispose] )} "Close"]]]]]]]]]]]
        [:div.col-md-4.col-sm-4
         ;[:div.x_panel.tile.fixed_height_320.overflow_hidden
         ; [:div.x_title
         ;  [:h2 "Responses By City"]
         ;  [:ul.nav.navbar-right.panel_toolbox
         ;   [:li [:a.collapse-link [:i.fa.fa-chevron-up]]]
         ;   [:li [:a.close-link [:i.fa.fa-close]]]]
         ;  [:div.clearfix]]
         ; [:div.x_content
         ;  [:table {:style {:width "100%"}}
         ;   [:thead [:tr
         ;            [:th {:style {:width "37%"}}
         ;             [:p " "]]
         ;            [:th
         ;             [:div.col-lg-7.col-md-7.col-sm-7
         ;              [:p "City"]]
         ;             [:div.col-lg-5.col-md-5.col-sm-5
         ;              [:p "% age"]]]]]
         ;   [:tbody [:tr
         ;            [:td
         ;             [:canvas.canvasDoughnut {:height "140" :width "140" :style {:margin "15px 10px 10px 0"}}]]
         ;            [:td
         ;             [:table.tile_info
         ;              [:tbody [:tr
         ;                       [:td
         ;                        [:p [:i.fa.fa-square.blue] "Los Angeles"]]
         ;                       [:td "35%"]]
         ;               [:tr
         ;                [:td
         ;                 [:p [:i.fa.fa-square.green] "New York"]]
         ;                [:td "45%"]]
         ;               [:tr
         ;                [:td
         ;                 [:p [:i.fa.fa-square.purple] "Charlotte"]]
         ;                [:td "20%"]]]]]]]]]]
         (let [vals-ratio (r/atom {:r-ratio (:ratio report)})] [:div.x_panel.tile.fixed_height_320.overflow_hidden
           [:div.x_title
            [:h2 "Settings"]
            [:ul.nav.navbar-right.panel_toolbox
             [:li [:a.collapse-link [:i.fa.fa-chevron-up]]]
             [:li [:a.close-link [:i.fa.fa-close]]]]
            [:div.clearfix]]
           [:div.x_content
            [:p.text-muted.font-13.m-b-30 "Adjust system settings"]
            (when-let [error-message @(rf/subscribe [:common/error])]
              [:p {:style {:color "red"}} error-message])
            [:form#demo-form2.form-horizontal.form-label-left {:action      "#" :data-parsley-validate "true"
                                                               :on-key-down #(if (= (.-key %) "Enter")
                                                                               (if (not (s/valid? ::r-ratio (js/parseInt (:r-ratio @vals-ratio))))
                                                                                 (do (rf/dispatch [:common/set-error "Enter a valid number"]))
                                                                                 (do (rf/dispatch [:clear-exceptions])
                                                                                     (rf/dispatch [:submit-ratio @vals-ratio]))))}
             [:div.item.form-group
              [:label.col-form-label.col-md-3.col-sm-3.label-align {:for "ratio-percent"} "Ratio %"]
              [:div.col-md-6.col-sm-6
               [:input#ratio-percent.form-control {:type        "text"
                                                   :placeholder (:ratio report)
                                                   :on-change   #(swap! vals-ratio assoc :r-ratio (-> % .-target .-value))}]]]
             [:div.item.form-group
              [:div.col-md-6.col-sm-6.offset-md-3
               [:button.btn.btn-success {:type "button"
                                         :on-click #(if (not (s/valid? ::r-ratio (js/parseInt (:r-ratio @vals-ratio))))
                                                      (do (rf/dispatch [:common/set-error "Enter a valid number"]))
                                                      (do (rf/dispatch [:clear-exceptions])
                                                          (rf/dispatch [:submit-ratio @vals-ratio])))} "Adjust"]]]]]])

         (let [vals-city (r/atom {:r-city ""})]
           [:div.x_panel.tile.fixed_height_320.overflow_hidden
            [:div.x_title
             [:h2 "Cities"]
             [:ul.nav.navbar-right.panel_toolbox
              [:li [:a.collapse-link [:i.fa.fa-chevron-up]]]
              [:li [:a.close-link [:i.fa.fa-close]]]]
             [:div.clearfix]]
            [:div.x_content
             [:p.text-muted.font-13.m-b-30 "Add more cities to the system"]
             [:a {:href "#" :data-toggle "modal" :data-target ".bs-example-modal-sm"} "view cities"]
             (when-let [error-message @(rf/subscribe [:common/error])]
               [:p {:style {:color "red"}} error-message])
             [:form#demo-form3.form-horizontal.form-label-left {:action      "#" :data-parsley-validate "true"
                                                                :on-key-down #(if (= (.-key %) "Enter")
                                                                                (if (not (s/valid? ::r-city (:r-city (string/trim @vals-city))))
                                                                                  (do (rf/dispatch [:common/set-error "Enter a city name"]))
                                                                                  (do (rf/dispatch [:clear-exceptions])
                                                                                      (rf/dispatch [:submit-city @vals-city]))))}
              [:div.item.form-group
               [:label.col-form-label.col-md-3.col-sm-3.label-align {:for "city-name"} "Name"]
               [:div.col-md-6.col-sm-6
                [:input#city-name.form-control {:type        "text"
                                                :placeholder "Enter city name"
                                                :on-change   #(swap! vals-city assoc :r-city (-> % .-target .-value))}]]]
              [:div.item.form-group
               [:div.col-md-6.col-sm-6.offset-md-3
                [:button.btn.btn-success {:type "button"
                                          :on-click #(if (= (.-key %) "Enter")
                                                       (if (not (s/valid? ::r-city (:r-city (string/trim @vals-city))))
                                                         (do (rf/dispatch [:common/set-error "Enter a city name"]))
                                                         (do (rf/dispatch [:clear-exceptions]) (println @vals-city)
                                                             (rf/dispatch [:submit-city @vals-city]))))} "Submit"]]]]
             ]
            ])]
        ]]
      [:br]]
     [:footer
      [:div.pull-right "© 2020 YearUp"]
      [:div.clearfix]]]]) )

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name        :home
           :view        #'home-page
           :controllers [{:start (fn [_] (rf/dispatch [:page/init-admin]))}]}]
     ["/setting" {:name :setting
                :view #'setting-page}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {:use-fragment false}))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components))
