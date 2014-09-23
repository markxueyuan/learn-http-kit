(ns learn-http-kit.core
  (:use org.httpkit.server
        org.httpkit.timer
        ring.util.response))

(def a (atom nil))

(comment (defn handler [req]
  (with-channel req channel
    (on-close channel (fn [status]
                        (println "The status is: " status ", channel closed")))
    (loop [id 0]
      (when (< id 10)
        (schedule-task (* id 200)
                       (send! channel (str "message from #" id) false))
        (recur (inc id))))
    (schedule-task 10000 (close channel)))))

(defn handler [request]
  (with-channel request channel
    (reset! a channel)
    (on-close channel (fn [status]
                        (println "The closed status is: " status)))
    (on-receive channel (fn [data]
                          (send! channel data)
                          (println data)))))


(defonce server (atom nil))

(defn close-server
  []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main []
  (reset! server (run-server #'handler {:port 8080})))


on-receive