;;----------------------------------------------------------------------
;; File game_env.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 10 Sep 2013
;; 
;; Contains the state of the game environment
;;----------------------------------------------------------------------

(ns chaser-cljs.game-env
  (:require-macros [chaser-cljs.macros :refer (defrecord+)])
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.player :as player]))

(defrecord+ GameEnv [board player exit])

#_(defn move-player
  [player dir board]
  (assert (some #{dir} [:left :down :right :up]))
  (let [target-x ((case dir :right inc :left dec identity)
                   (player/get-x player))
        target-y ((case dir :up dec :down inc identity) 
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
          (rand-nth (filter (fn [coords]
                              (or (not (= (coords/get-x coords) 
                                          (player/get-x player)))
                                  (not (= (coords/get-y coords)
                                          (player/get-y player)))))
                    coord*))
          (rand-nth exit-coords*)))))

(let [default-board-size 15]
  (defn make-fresh-game-env []
    (let [board (board/make-randomized-board default-board-size)
          player (player/make-player (player-start-coords board))]
      (make-game-env 
        board
        player
        (exit/make-exit 
          (exit-start-coords board default-board-size player))))))
