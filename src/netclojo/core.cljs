(ns netclojo.core
  (:require [cljs.core.async :as async
             :refer [<! >! chan alts! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defn new-canvas
  ([width height]
     (let [canvas
           (.createElement js/document "canvas")]
       (set! (.-width canvas) width)
       (set! (.-height canvas) height)
       canvas))
  ([] (new-canvas 0 0)))


(def canvas (.getElementById js/document "world"))
(def context (.getContext canvas "2d"))
(def offscreen-canvas (new-canvas))
(def offscreen-canvas-context (.getContext offscreen-canvas "2d"))


(def width (atom nil))
(def height (atom nil))


(def cell-size 5)
(def world (atom {}))

(defn resized []
  (set! (.-width canvas) (.-innerWidth js/window))
  (set! (.-height canvas) (.-innerHeight js/window))
  (set! (.-width offscreen-canvas) (.-innerWidth js/window))
  (set! (.-height offscreen-canvas) (.-innerHeight js/window))

  (reset! width (/ (.-width canvas) cell-size))
  (reset! height (/ (.-height canvas) cell-size)))

(defn fill_sq [x y colour]
  (set! (.-fillStyle offscreen-canvas-context) colour)
  (.fillRect offscreen-canvas-context
             (* x cell-size)
             (* y cell-size)
             cell-size
             cell-size))

(defn deg->rad [d]
  (* Math/PI (/ d 360)))

(defn make-turtle
  ([] (make-turtle (rand @width) (rand @height) (rand 360)))
  ([x y theta]
     (atom {:x x
            :y y
            :theta theta})))

(defn fd [turtle distance]
  (let [{:keys [x y theta]} @turtle
        theta-rad (deg->rad theta)
        new-x (+ x (* distance (Math/sin theta-rad)))
        new-y (+ y (* distance (Math/cos theta-rad)))
        bounce? (not (and (< 0 new-x @width)
                          (< 0 new-y @height)))
        ]
    (swap! turtle merge {:x new-x
                         :y new-y
                         :theta (if bounce? (+ 180 theta)
                                    theta)})))

(defn rt [turtle angle]
  (swap! turtle update-in [:theta] (partial + angle)))

(defn lt [turtle angle]
  (rt turtle (- angle)))

(def black "#000000")
(def yellow "#FFDB7F")
(def red "#E88E7F")
(def purple "#DA98FF")
(def blue "#88BAE8")
(def green "#7FFFA5")

(set! (.-onresize js/window) resized)

(resized)


(def turtles (take 50 (repeatedly make-turtle)))

(defn draw [turtle]
  (let [t @turtle]
    (fill_sq (:x t) (:y t) red )))

(defn blank []
  (set! (.-fillStyle offscreen-canvas-context) black)
  (.fillRect offscreen-canvas-context
             0
             0
             (* cell-size @width)
             (* cell-size @height)))

(defn copy []
  (.drawImage context offscreen-canvas 0 0))

(go (loop []
      (<! (timeout 20))
      (blank)
      (doseq [t turtles]
        (draw t))
      (copy)
      (println "hello")
      (recur)))

(go
 (loop []
   (<! (timeout 10))
   (doseq [t turtles]
     (fd t 1)
     (rt t (rand 60))
     (lt t (rand 60)))
   (recur)))
