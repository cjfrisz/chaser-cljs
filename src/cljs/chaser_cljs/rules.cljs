;;----------------------------------------------------------------------
;; File rules.cljs
;; Written by Chris Frisz
;; 
;; Created 10 Sep 2013
;; Last modified 13 Oct 2013
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


(defn start-player-movement
  [player dir]
  (assert (some #{dir} [:left :down :right :up]))
  (-> player
    (player/update-moving? true)
    (player/update-dir dir)))

(defn exit-reached?
  [game-env]
    (apply = (map (juxt proto/get-x proto/get-y) 
      [(game-env/get-player game-env) 
       (board/get-exit-room (game-env/get-board game-env))])))

(def accelerate inc)

(defn move-player
  [player]
  (if (player/get-moving? player)
      (let [dir (player/get-dir player)
            speed (player/get-cur-speed player)
            target-x ((case dir
                        :right (partial + speed)
                        :left (partial + (- speed))
                        identity)
                       (proto/get-x player))
            target-y ((case dir
                        :up (partial + (- speed))
                        :down (partial + speed)
                        identity) 
                       (proto/get-y player))]
        (-> player
          (proto/update-x target-x)
          (proto/update-y target-y)))
      player))
        
(defn update-game!
  [system-atom]
  (let [game-env (system/get-game-env @system-atom)
        player (game-env/get-player game-env)
        speed (accelerate (player/get-cur-speed player))
        new-player (move-player (player/update-cur-speed player speed))
        new-game-env (game-env/update-player game-env new-player)]
    (if (exit-reached? new-game-env)
        (let [new-system (system/make-system)]
          (dom/reset-canvas! 
           (system/get-game-env new-system)
           (system/get-renderer new-system))
          (swap! system-atom (constantly new-system)))
        (swap! system-atom system/update-game-env new-game-env))))
