(ns status-im.ui.screens.profile.visibility-status.views
  (:require-macros [status-im.utils.views :as views])
  (:require ["react-native" :refer (BackHandler)]
            [quo.core :as quo]
            [quo.design-system.colors :as colors]
            [quo.react-native :as rn]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [status-im.i18n.i18n :as i18n]
            [status-im.ui.components.animation :as anim]
            [status-im.ui.components.react :as react]
            [status-im.utils.platform :as platform]
            [status-im.ui.screens.profile.visibility-status.styles :as styles]))

;; === Code Related to visibility-status-button ===

(def button-ref (atom nil))

(defn dispatch-popover [top]
  (re-frame/dispatch [:show-visibility-status-popover {:top top}]))
  
(defn calculate-button-height-and-dispatch-popover []
  (.measure @button-ref
            (fn  [fx fy width height px py]
              (dispatch-popover py))))

(defn visibility-status-button [on-press props]
  [rn/touchable-opacity
   (merge {:on-press  on-press
    :accessibility-label :visibility-status-button
    :style (styles/visibility-status-button-container)
    :ref #(reset! button-ref ^js %)} props)
   [rn/view {:style (styles/visibility-status-dot styles/color-online)}]
   [rn/text {:style (styles/visibility-status-text)} (i18n/label :t/status-online)]])

;; === Code Related to visibility-status-popover ===
(def scale (anim/create-value 0))
(def position (anim/create-value 0))

(defn hide-options []
  (anim/start
   (anim/parallel
    [(anim/timing scale {:toValue         0
                         :duration        140
                         :useNativeDriver true})
     (anim/timing position {:toValue         50
                            :duration        210
                            :useNativeDriver true})])))

(defn show-options []
  (anim/start
   (anim/parallel
    [(anim/timing scale {:toValue         1
                         :duration        210
                         :useNativeDriver true})
     (anim/timing position {:toValue         80
                            :duration        70
                            :useNativeDriver true})])))

(defn status-option[color title subtitle]
  [rn/touchable-opacity {:style {:padding 6 :padding-left 6}}
  [rn/view  {:style {:flex-direction "row"}
                     :accessibility-label :visibility-status-option}
   [rn/view {:style (styles/visibility-status-dot color)}]
   [rn/text {:style (styles/visibility-status-text)} title]]
   (when-not (nil? subtitle)
     [rn/text {:style (styles/visibility-status-subtitle)} subtitle])])

(defn visibility-status-options [on-press top]
  [react/view {:position :absolute
               :top (int top)}
   [visibility-status-button on-press {:ref nil :active-opacity 1}]
   [react/animated-view {:style (styles/visibility-status-options scale position)
                        :accessibility-label :visibility-status-options}
    [status-option styles/color-online (i18n/label :t/status-online)]
    [quo/separator {:style {:margin-top 8}}]
    [status-option styles/color-dnd (i18n/label :t/status-dnd) (i18n/label :t/subtitle-dnd)]
    [quo/separator]
    [status-option styles/color-invisible (i18n/label :t/status-invisible) (i18n/label :t/subtitle-invisible)]]])

(defn popover-view [_]
  (let [clear-timeout     (atom nil)
        current-popover   (reagent/atom nil)
        update?           (reagent/atom nil)
        request-close     (fn []                       
                            (reset! clear-timeout
                                       (js/setTimeout
                                       #(do (reset! current-popover nil)
                                            (re-frame/dispatch [:hide-visibility-status-popover])) 200))
                            (hide-options)
                            true)
        on-show           (fn []
                            (show-options)
                            (when platform/android?
                              (.removeEventListener BackHandler
                                                    "hardwareBackPress"
                                                    request-close)
                              (.addEventListener BackHandler
                                                 "hardwareBackPress"
                                                 request-close)))
        on-hide           (fn []
                            (when platform/android?
                              (.removeEventListener BackHandler
                                                    "hardwareBackPress"
                                                    request-close)))]
    (reagent/create-class
     {:UNSAFE_componentWillUpdate
      (fn [_ [_ popover _]]
        (when @clear-timeout (js/clearTimeout @clear-timeout))
        (cond
          @update?
          (do (reset! update? false)
              (on-show))

          (and @current-popover popover)
          (do (reset! update? true)
              (js/setTimeout #(reset! current-popover popover) 600)
              (hide-options))

          popover
          (do (reset! current-popover popover)
              (on-show))

          :else
          (do (reset! current-popover nil)
              (on-hide))))
      :component-will-unmount on-hide
      :reagent-render
      (fn []
        (when @current-popover
          (let [{:keys [top]} @current-popover]
            [react/view {:position :absolute :top 0 :bottom 0 :left 0 :right 0}
             (when platform/ios?
               [react/view
                {:style {:flex 1 :background-color colors/black-persist}}])
             [react/touchable-highlight
              {:style {:flex 1}
               :on-press request-close}
               [visibility-status-options request-close top]]])))})))

(views/defview visibility-status-popover []
  (views/letsubs [popover [:visibility-status-popover/popover]]
                 [popover-view popover]))
