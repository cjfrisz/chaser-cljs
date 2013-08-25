;;----------------------------------------------------------------------
;; File game_env.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 25 Aug 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.game-env)

(defn make-game-env
  [game-map player key-stream]
  {:game-map game-map
   :player player
   :key-stream key-stream})
(def game-env-get-game-map :game-map)
(def game-env-get-player :player)
(def game-env-get-key-stream :key-stream)
(defn game-env-update-game-map
  [game-env new-game-map]
  (assoc game-env :game-map new-game-map))
(defn game-env-update-player
  [game-env new-player]
  (assoc game-env :player new-player))
(defn game-env-update-key-stream
  [game-env new-key-stream]
  (assoc game-env :key-stream new-key-stream))
