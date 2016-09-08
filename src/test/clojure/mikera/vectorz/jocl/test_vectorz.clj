(ns mikera.vectorz.jocl.test-vectorz
  (:use [clojure.core matrix]
        [clojure.test])
  (:require [clojure.core.matrix.compliance-tester :as compl])
  ;; (:require [criterium.core :as c])
  )

(defn rmatrix [m n]
  (mikera.vectorz.jocl.JoclMatrix/newMatrix (int m) (int n)))

(set-current-implementation :vectorz)

(deftest test-joclmatrix-instance
  (compl/instance-test (rmatrix 3 3)))


(deftest compliance-test
  (compl/compliance-test (rmatrix 3 3)))

(comment 
  (let [size 3
        m (rmatrix size size)
        n (transpose (rmatrix size size))]
     (c/quick-bench (mmul m n)))
  )