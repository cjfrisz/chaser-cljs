;;----------------------------------------------------------------------
;; File rules.cljs
;; Written by Chris Frisz
;; 
;; Created 10 Sep 2013
;; Last modified 17 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.rules
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.dom :as dom]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.room :as room]
            [chaser-cljs.system :as system]))
    
(defn move-player
  [game-env dir]
  (assert (some #{dir} [:left :down :right :up]))
  (let [player (game-env/get-player game-env)
        ;; NB: raw values
        target-x ((case dir
                    :right (partial + 50)
                    :left (partial + -50)
                    identity)
                    (proto/get-x player))
        target-y ((case dir
                    :up (partial + -50)
                    :down (partial + 50)
                    identity) 
                    (proto/get-y player))]
    (if (board/get-room (game-env/get-board game-env) 
          target-x
          target-y)
        (game-env/update-player game-env
          ;; NB: reserving the right to be a hypocrite
          (-> player
              (proto/update-x target-x)
              (proto/update-y target-y)
              (player/update-dir dir)))
        game-env)))

(defn exit-reached?
  [game-env]
    (apply = (map (juxt proto/get-x proto/get-y) 
      [(game-env/get-player game-env) 
       (board/get-exit-room (game-env/get-board game-env))])))

(defn update-game!
  [system-atom]
  (if (exit-reached? (system/get-game-env @system-atom))
      (let [new-system (system/make-system)]
        (dom/reset-canvas! 
         (system/get-game-env new-system)
         (system/get-renderer new-system))
        (swap! system-atom (constantly new-system)))
      system-atom))
