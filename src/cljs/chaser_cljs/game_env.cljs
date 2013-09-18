;;----------------------------------------------------------------------
;; File game_env.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 17 Sep 2013
;; 
;; Contains the state of the game environment
;;----------------------------------------------------------------------

(ns chaser-cljs.game-env
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.grid-generator :as grid-gen]
            [chaser-cljs.player :as player]
            [chaser-cljs.protocols :as proto]
            [chaser-cljs.room :as room]))

(defrecord+ GameEnv [board player])

(def pick-player-start-coords 
  (comp (juxt proto/get-x proto/get-y) rand-nth board/get-room*))

;; NB: probably want something smarter than this
(defn add-board-exit
  [board player]
  (let [room* (board/get-room* board)
        player-room (apply board/get-room board 
                      ((juxt proto/get-x proto/get-y) player))]
    (loop [exit-room (rand-nth room*)]
      (if (= exit-room player-room)
          (recur (rand-nth room*))
          (apply board/set-exit-room board
            ((juxt proto/get-x proto/get-y) exit-room))))))

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
    (partial mapv tile->room)
    grid-gen/generate-tile*))

(let [default-board-size 15]
  (defn make-game-env []
    (let [board (make-randomized-board default-board-size)
          player (apply player/make-player 
                   (pick-player-start-coords board))
          board-with-exit (add-board-exit board player)]
      (->GameEnv board-with-exit player))))
