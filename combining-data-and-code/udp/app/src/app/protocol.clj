(ns app.protocol
  (:use (app record)))

(defprotocol FIXO
  (fixo-push [fixo value])
  (fixo-pop [fixo])
  (fixo-peek [fixo]))

(extend-type TreeNode
  FIXO
  (fixo-push [node value]
    (xconj node value))
  (fixo-peek [node]
    (if (:l node)
      (recur (:l node))
      (:val node)))
  (fixo-pop [node]
    (if (:l node)
      (TreeNode. (:val node) (fixo-pop (:l node)) (:r node))
      (:r node))))

(extend-type clojure.lang.IPersistentVector
  FIXO
  (fixo-push [vector value]
    (conj vector value))
  (fixo-peek [vector]
    (peek vector))
  (fixo-pop [vector]
    (pop vector)))

(extend-type nil
  FIXO
  (fixo-push [t v]
    (TreeNode. v nil nil)))

(defn fixo-into [c1 c2]
  (reduce fixo-push c1 c2))

(def tree-node-fixo
  {:fixo-push (fn [node value]
                (xconj node value))
   :fixo-peek (fn [node]
                (if (:l node)
                  (recur (:l node))
                  (:val node)))
   :fixo-pop (fn [node]
               (if (:l node)
                 (TreeNode. (:val node) (fixo-pop ( :l node)) (:r node))
                 (:r node)))})

(extend TreeNode FIXO tree-node-fixo)

(defn fixed-fixo
  "size limited FIXO stack using reify"
  ([limit] (fixed-fixo limit []))
  ([limit vector]
     (reify FIXO
       (fixo-push [this value]
         (if (< (count vector) limit)
           (fixed-fixo limit (conj vector value))
           this))
       (fixo-peek [_]
         (peek vector))
       (fixo-pop [_]
         (pop vector)))))



(deftype InfiniteConstant [i]
  clojure.lang.ISeq
  (seq [this]
    (lazy-seq (cons i (seq this)))))
