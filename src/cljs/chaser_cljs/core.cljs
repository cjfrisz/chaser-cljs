;;----------------------------------------------------------------------
;; File core.cljs
;; Written by Chris Frisz
;; 
;; Created 21 Aug 2013
;; Last modified 25 Aug 2013
;; 
;; Entrypoint for the game.
;;----------------------------------------------------------------------

(ns chaser-cljs.core
  (:require-macros [dommy.macros :refer (node sel1)])
  (:require [dommy.core :refer (append! listen!)]
            [chaser-cljs.coords :refer (make-coords
                                        coords-get-x coords-get-y
                                        coords-update-x 
                                        coords-update-y)]
            [chaser-cljs.game-env :refer (make-game-env
                                          game-env-get-game-map
                                          game-env-get-player
                                          game-env-get-key-stream
                                          game-env-update-game-map
                                          game-env-update-player
                                          game-env-update-key-stream)]
            [chaser-cljs.key-stream :refer (make-key-stream 
                                            key-stream-enqueue
                                            key-stream-dequeue-batch)]
            [chaser-cljs.player :refer (make-player 
                                        player-get-coords 
                                        player-update-coords)]
            [chaser-cljs.map-generator :refer (build-map)]))

(def map-size 15)

(def space-width 50)
(def space-height space-width)
(def space-border-width 5)
(def space-border-color "#000000")
(def space-interior-color "#B0B0B0")

(def canvas-border-width space-width)

(def global-game-env (atom nil))

(defn init-canvas
  "Initialize the game canvas."
  [game-map]
  (letfn [(game-map-max-coord [coord-getter]
            (reduce (fn [cur-max coord]
                      (max cur-max (coord-getter coord)))
              0
              game-map))
          (dimension-pxs [max-coord]
            (+ (* (inc max-coord) space-width) 
               (* canvas-border-width 2)))]
    (let [max-x (game-map-max-coord coords-get-x)
          max-y (game-map-max-coord coords-get-y)]
      (append! (sel1 :#game)
        (node 
         [:canvas#gameCanvas
          {:width (dimension-pxs max-x)
           :height (dimension-pxs max-y)
           :style "border:1px solid #000000;"}])))))

(defn render-space
  [space-px-x space-px-y]
  (let [ctx (.getContext (sel1 :#gameCanvas) "2d")]
    (.beginPath ctx)
    (.rect ctx space-px-x space-px-y space-width space-width)
    (set! (. ctx -fillStyle) space-interior-color)
    (.fill ctx)
    (set! (. ctx -lineWidth) space-border-width)
    (set! (. ctx -strokeStyle) space-border-color)
    (.stroke ctx)))

(defn render-map
  [game-map]
  (doseq [space game-map]
    (render-space
     (+ canvas-border-width (* (coords-get-x space) space-width))
     (+ canvas-border-width (* (coords-get-y space) space-width)))))

(def player-color "#FF0000")
(def player-border-color "#000000")
(def player-border-width 2)

(defn render-player
  [player]
  (let [ctx (.getContext (sel1 :#gameCanvas) "2d")
        coords (player-get-coords player)
        half-space (/ space-width 2)]
    (. ctx beginPath)
    (. ctx arc
      (+ (* (coords-get-x coords) space-width)
         half-space 
         canvas-border-width)
      (+ (* (coords-get-y coords) space-width)
         half-space 
         canvas-border-width)
      half-space
      0
      (* 2 (. js/Math -PI))
      false)
    (set! (. ctx -fillStyle) player-color)
    (. ctx fill)
    (set! (. ctx -lineWidth) player-border-width)
    (set! (. ctx -strokeStyle) player-border-color)
    (. ctx stroke)))

(defn player-update-coord
  [player coord mod-fn]
  (let [[getter updater] (case coord
                           :x [coords-get-x coords-update-x]
                           :y [coords-get-y coords-update-y])
        coords (player-get-coords player)]
    (player-update-coords player 
      (updater coords (mod-fn (getter coords))))))

(listen! js/document :keydown 
  (fn [key-event]
    (let [dir (case (. key-event -keyCode)
                37 :left
                38 :down
                39 :right
                40 :up
                nil)]
      (when dir
        (swap! global-game-env
          (fn [game-env]
            (game-env-update-key-stream game-env
              (key-stream-enqueue (game-env-get-key-stream game-env)
                dir))))
        (.preventDefault key-event)))))

(defn game-loop []
  (let [game-env @global-game-env]
    (loop [key* (key-stream-dequeue-batch 
                  (game-env-get-key-stream game-env))
           player (game-env-get-player game-env)]
      (if (nil? (seq key*))
          (do
            (render-map (game-env-get-game-map game-env))
            (render-player player)
            (swap! global-game-env
              (comp #(game-env-update-player % player)
                #(game-env-update-key-stream % [])))) 
          (recur (next key*)
            (apply player-update-coord player
              (case (first key*)
                :left  [:x dec]
                :up    [:y inc]
                :right [:x inc]
                :down  [:y dec])))))))
             
(set! (.-onload js/window) 
  (fn []
    (let [game-map (build-map map-size)
          player (make-player (make-coords 0 0))
          key-stream (make-key-stream)
          game-env (make-game-env game-map player key-stream)]
      (swap! global-game-env (constantly game-env))
      (init-canvas game-map)
      (js/setInterval game-loop (/ 1000 30)))))
