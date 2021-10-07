(ns status-im.ui.screens.profile.visibility-status.styles
  (:require [quo.design-system.colors :as colors]))

(def color-online "#7CDA00")
(def color-dnd "#FA6565")
(def color-invisible "#FFFFFF")

(defn visibility-status-button-container []
  {:background-color       (:interactive-02 @colors/theme)
   :margin-left            16
   :border-radius          16
   :border-top-left-radius 4
   :align-self             "flex-start"
   :flex-direction         "row"
   :align-items            "center"
   :padding                6
   :padding-right          12})

(defn visibility-status-dot [dot-color]
  {:background-color dot-color
   :margin           6
   :width            10
   :height           10
   :border-radius    5
   :border-width     1
   :border-color     "rgba(0,0,0,0.1)"})

(defn visibility-status-text []
  {:color (:text-01 @colors/theme)
   :font-size 16
   :weight   700
   :text-align "center"})

(defn visibility-status-subtitle []
  {:color (:text-02 @colors/theme)
   :font-size 16
   :margin-left 22
   :weight 600})

(defn visibility-status-options [scale position]
  {:background-color (:ui-background @colors/theme)
   :border-radius 16
   :border-top-left-radius 4
   :justify-content "center"
   :align-self "flex-start"
   :left 16
   :top -76
   :padding 6
   :transform [{:scaleY scale}
               {:translateY position}]})
