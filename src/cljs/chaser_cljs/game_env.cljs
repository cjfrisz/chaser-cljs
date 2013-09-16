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
            [chaser-cljs.grid-generator :as grid-gen]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.room :as room]))

(defrecord+ GameEnv [board player exit])

(def player-start-coords 
  (comp (juxt proto/get-x proto/get-y) rand-nth board/get-room*))

;; NB: probably want something smarter than this
(defn exit-start-coords 
  [board player]
  (let [player-coords ((juxt proto/get-x proto/get-y) player)]
    (loop [start-coords (player-start-coords board)]
      (if (= start-coords player-coords)
          (recur (player-start-coords board))
          start-coords))))

;; NB: algorithm only works for uniformly-sized rooms. will need
;; NB: something more intelligent if rooms get more interesting
(let [default-room-width 50
      default-room-height default-room-width]
  (defn tile->room
    [tile]
    (room/make-room 
      (* (proto/get-x tile) default-room-width)
      (* (proto/get-y tile) default-room-height)
      default-room-width
      default-room-height)))

(def make-randomized-board 
  (comp board/make-board
    (partial map tile->room)
    grid-gen/generate-tile*))

(let [default-board-size 15]
  (defn make-game-env []
    (let [board (make-randomized-board default-board-size)
          player (apply player/make-player (player-start-coords board))]
      (->GameEnv
        board
        player
        (apply exit/make-exit (exit-start-coords board player))))))
