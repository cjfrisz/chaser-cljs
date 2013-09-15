;;----------------------------------------------------------------------
;; File rules.cljs
;; Written by Chris Frisz
;; 
;; Created 10 Sep 2013
;; Last modified 15 Sep 2013
;; 
;; 
;;----------------------------------------------------------------------

(ns chaser-cljs.rules
  (:require [chaser-cljs.board :as board]
            [chaser-cljs.dom :as dom]
            [chaser-cljs.exit :as exit]
            [chaser-cljs.game-env :as game-env]
            [chaser-cljs.player :as player]))

(defn exit-reached?
  [game-env]
  (let [player (game-env/get-player game-env)
        exit (game-env/get-exit game-env)]
    (and (= (player/get-x player) (exit/get-x exit))
         (= (player/get-y player) (exit/get-y exit)))))

(defn move-player
  [game-env dir]
  (assert (some #{dir} [:left :down :right :up]))
  (let [player (game-env/get-player game-env)
        ;; NB: raw values
        target-x ((case dir
                    :right (partial + 50)
                    :left (partial + -50)
                    identity)
                    (player/get-x player))
        target-y ((case dir
                    :up (partial + -50)
                    :down (partial + 50)
                    identity) 
                    (player/get-y player))]
    (if (board/get-space (game-env/get-board game-env) 
          target-x
          target-y)
        (game-env/update-player game-env
          ;; NB: reserving the right to be a hypocrite
          (-> player
              (player/update-x target-x)
              (player/update-y target-y)
              (player/update-dir dir)))
        game-env)))
