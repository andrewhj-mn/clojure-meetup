(ns app.record)

(defrecord TreeNode [val l r])
(defn xconj [t v]
  (cond
   (nil? t) (TreeNode. v nil nil)
   (< v (:val t)) (TreeNode. (:val t) (xconj (:l t) v) (:r t))
   :else (TreeNode. (:val t) (:l t) (xconj (:r t) v))))
(defn xseq [t]
  (when t
    (concat (xseq (:l t)) [(:val t)] (xseq (:r t)))))

(def sample-tree (reduce xconj nil [3 5 2 4 6]))
