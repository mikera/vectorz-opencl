(ns mikera.vectorz.jocl.test-vectorz
  (:use [clojure.core matrix]
        [clojure.test])
  (:require [mikera.vectorz.opencl-api])
  (:require [clojure.core.matrix.compliance-tester :as compl])
  ;; (:require [criterium.core :as c])
  )

(defn joclmatrix [m n]
  (mikera.vectorz.jocl.JoclMatrix/newMatrix (int m) (int n)))

(defn joclvector [m]
  (mikera.vectorz.jocl.JoclVector/createLength (int m)))


(set-current-implementation :vectorz-opencl)

(deftest test-joclmatrix-instance
  (compl/instance-test (joclmatrix 3 3)))

(deftest test-joclvector-instance
  (compl/instance-test (joclvector 4)))

(deftest test-joclsubvector-instance
  (let [src (joclvector 4)]
    (compl/instance-test (subvector src 1 2))))

;(deftest compliance-test
;  (compl/compliance-test (joclmatrix 3 3)))

(comment 
  (let [size 3
        m (rmatrix size size)
        n (transpose (rmatrix size size))]
     (c/quick-bench (mmul m n)))
  )