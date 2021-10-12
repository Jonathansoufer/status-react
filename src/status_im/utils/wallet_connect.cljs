(ns status-im.utils.share
  (:require ["@walletconnect/client" :default WalletConnectClient]
            ["@react-native-community/async-storage" :default AsyncStorage]))

(defn init [on-success on-error]
  (-> ^js WalletConnectClient
      (.init (clj->js {:controller true
                       :relay-provider "wss://relay.walletconnect.com"
                       :metadata {:name "Status Wallet"
                                  :description "Status is a secure messaging app, crypto wallet, and Web3 browser built with state of the art technology."
                                  :url "#"
                                  :icons ["https://statusnetwork.com/img/press-kit-status-logo.svg"]}
                       :storage-options {:async-storage (^js AsyncStorage)}}))
      (.then on-success)
      (.catch on-error)))
