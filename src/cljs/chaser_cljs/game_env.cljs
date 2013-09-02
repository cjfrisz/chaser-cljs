;;----------------------------------------------------------------------
;; File game_env.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified  2 Sep 2013
;; 
;; Contains the state of the game environment
;;----------------------------------------------------------------------

(ns chaser-cljs.game-env
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.key-stream :as key-stream]
            [chaser-cljs.player :as player]))

;; NB: maybe maintaining the key-stream as part of the game environment
;;     isn't a good idea
(defn make-game-env
  [board board-size player exit key-stream]
  {:board board
   :board-size board-size
   :player player
   :exit exit

   :key-stream key-stream})

(def get-board :board)
(def get-board-size :board-size)
(def get-player :player)
(def get-exit :exit)

(def get-key-stream :key-stream)

(defn update-board
  [game-env new-board]
  (assoc game-env :board new-board))
(defn update-board-size
  [game-env new-board-size]
  (assoc game-env :board-size new-board-size))
(defn update-player
  [game-env new-player]
  (assoc game-env :player new-player))
(defn update-eit
  [game-env new-exit]
  (assoc game-env :exit new-exit))

(defn update-key-stream
  [game-env new-key-stream]
  (assoc game-env :key-stream new-key-stream))

(defn move-player
  [player dir board]
  (assert (some #{dir} [:left :down :right :up]))
  (let [target-x ((case dir :right inc :left dec identity)
                   (player/get-x player))
        target-y ((case dir :up inc :down dec identity) 
                   (player/get-y player))]
    (if (board/get-space board target-x target-y)
        (as-> player player
          (player/update-x player target-x)
          (player/update-y player target-y))
        player)))

(def player-start-coords (comp rand-nth board/get-coord*))

(defn exit-start-coords
  [board board-size player]
  (letfn [(sqr [n] (.pow js/Math n 2))]
    (let [coord* (board/get-coord* board)
          exit-coords* (filter (fn [coords]
                                 (> (.sqrt js/Math 
                                      (+ (sqr (- (player/get-x player)
                                                 (coords/get-x coords)))
                                         (sqr (- (player/get-y player)
                                                 (coords/get-y coords)))))
                                    (quot board-size 3)))
                         coord*)]
      (if (nil? (seq exit-coords*))
          (rand-nth (filter #(or (not (= (coords/get-x %) 
                                         (player/get-x player)))
                                 (not (= (coords/get-y %)
                                         (player/get-y player))))
                    coord*))
          (rand-nth exit-coords*)))))

(let [default-board-size 15]
  (defn make-fresh-game-env []
    (let [board (board/make-randomized-board default-board-size)
          player (player/make-player (player-start-coords board))]
      (make-game-env 
        board
        default-board-size
        player
        (exit/make-exit 
          (exit-start-coords board default-board-size player))
        (key-stream/make-key-stream)))))
