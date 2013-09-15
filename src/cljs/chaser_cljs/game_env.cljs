;;----------------------------------------------------------------------
;; File game_env.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 15 Sep 2013
;; 
;; Contains the state of the game environment
;;----------------------------------------------------------------------

(ns chaser-cljs.game-env
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]))

(defrecord+ GameEnv [board player exit])

(def player-start-coords 
  (comp (juxt proto/get-x proto/get-y) rand-nth board/get-room*))

;; NB: probably want something smarter than this
(defn exit-start-coords 
  [board player]
  (let [player-coords ((juxt player/get-x player/get-y) player)]
    (loop [start-coords (player-start-coords board)]
      (if (= start-coords player-coords)
          (recur (player-start-coords board))
          start-coords))))

(let [default-board-size 15]
  (defn make-game-env []
    (let [board (board/make-randomized-board default-board-size)
          player (player/make-player (player-start-coords board))]
      (->GameEnv
        board
        player
        (exit/make-exit (exit-start-coords board player))))))
