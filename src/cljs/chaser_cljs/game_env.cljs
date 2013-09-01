;;----------------------------------------------------------------------
;; File game_env.cljs
;; Written by Chris Frisz
;; 
;; Created 25 Aug 2013
;; Last modified 31 Aug 2013
;; 
;; Contains the state of the game environment
;;----------------------------------------------------------------------

(ns chaser-cljs.game-env
  (:require [chaser-cljs.board-generator :as board-generator]
            [chaser-cljs.coords :as coords]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.key-stream :as key-stream]
            [chaser-cljs.player :as player]
            [chaser-cljs.render.board :as board-render]
            [chaser-cljs.render.exit :as exit-render]
            [chaser-cljs.render.player :as player-render]))

;; NB: maybe maintaining the key-stream as part of the game environment
;;     isn't a good idea
;; NB: separating the renderers is probably also a good idea
(defn make-game-env
  [board player exit
   board-renderer player-renderer exit-renderer
   key-stream]
  {:board board
   :player player
   :exit exit

   :board-renderer board-renderer
   :player-renderer player-renderer
   :exit-renderer exit-renderer

   :key-stream key-stream})

(def get-board :board)
(def get-player :player)
(def get-exit :exit)

(def get-board-renderer :board-renderer)
(def get-player-renderer :player-renderer)
(def get-exit-renderer :exit-renderer)

(def get-key-stream :key-stream)

(defn update-board
  [game-env new-board]
  (assoc game-env :board new-board))
(defn update-player
  [game-env new-player]
  (assoc game-env :player new-player))
(defn update-eit
  [game-env new-exit]
  (assoc game-env :exit new-exit))

(defn update-board-renderer
  [game-env new-board-renderer]
  (assoc game-env :board-renderer new-board-renderer))
(defn update-player-renderer
  [game-env new-player-renderer]
  (assoc game-env :player-renderer new-player-renderer))
(defn update-exit-renderer
  [game-env new-exit-renderer]
  (assoc game-env :exit-renderer new-exit-renderer))

(defn update-key-stream
  [game-env new-key-stream]
  (assoc game-env :key-stream new-key-stream))

;; NB: global value
(def board-size 15)

(def player-start-coords rand-nth)

(defn exit-start-coords
  [board player]
  (letfn [(sqr [n] (.pow js/Math n 2))]
    (let [exit-coords* (filter (fn [coords]
                                 (> (.sqrt js/Math 
                                      (+ (sqr (- (player/get-x player)
                                                 (coords/get-x coords)))
                                         (sqr (- (player/get-y player)
                                                 (coords/get-y coords)))))
                                      (quot board-size 3)))
                         board)]
      (if (nil? (seq exit-coords*))
          (rand-nth (filter #(or (not (= (coords/get-x %) 
                                         (player/get-x player)))
                                 (not (= (coords/get-y %)
                                         (player/get-y player))))
                    board))
        (rand-nth exit-coords*)))))

(defn make-fresh-game-env []
  (let [board (board-generator/build-board board-size)
        player (player/make-player (player-start-coords board))]
    (make-game-env 
      board
      player
      (exit/make-exit (exit-start-coords board player))
      (board-render/make-renderer)
      (player-render/make-renderer)
      (exit-render/make-renderer)
      (key-stream/make-key-stream))))
