;;----------------------------------------------------------------------
;; File game_env.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 29 Aug 2013
;; 
;; Contains the state of the game environment
;;----------------------------------------------------------------------

(ns chaser-cljs.game-env)

;; NB: maybe maintaining the key-stream as part of the game environment
;;     isn't a good idea
(defn make-game-env
  [game-map player exit
   game-map-renderer player-renderer exit-renderer
   key-stream]
  {:game-map game-map
   :player player
   :exit exit

   :game-map-renderer game-map-renderer
   :player-renderer player-renderer
   :exit-renderer exit-renderer

   :key-stream key-stream})

(def game-env-get-game-map :game-map)
(def game-env-get-player :player)
(def game-env-get-exit :exit)

(def game-env-get-game-map-renderer :game-map-renderer)
(def game-env-get-player-renderer :player-renderer)
(def game-env-get-exit-renderer :exit-renderer)

(def game-env-get-key-stream :key-stream)

(defn game-env-update-game-map
  [game-env new-game-map]
  (assoc game-env :game-map new-game-map))
(defn game-env-update-player
  [game-env new-player]
  (assoc game-env :player new-player))
(defn game-env-update-eit
  [game-env new-exit]
  (assoc game-env :exit new-exit))

(defn game-env-update-game-map-renderer
  [game-env new-game-map-renderer]
  (assoc game-env :game-map-renderer new-game-map-renderer))
(defn game-env-update-player-renderer
  [game-env new-player-renderer]
  (assoc game-env :player-renderer new-player-renderer))
(defn game-env-update-exit-renderer
  [game-env new-exit-renderer]
  (assoc game-env :exit-renderer new-exit-renderer))

(defn game-env-update-key-stream
  [game-env new-key-stream]
  (assoc game-env :key-stream new-key-stream))
