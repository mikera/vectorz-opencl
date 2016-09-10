(ns mikera.vectorz.opencl-api
  (:use clojure.core.matrix)
  (:use clojure.core.matrix.utils)
  (:require [mikera.vectorz.matrix-api :as vclj])
  (:require [clojure.core.matrix.implementations :as imp])
  (:require [clojure.core.matrix.protocols :as mp])
  (:import [mikera.vectorz.jocl JoclMatrix JoclUtils JoclVector JoclScalar ADenseJoclVector IJoclArray]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(def JOCL-INSTANCE (JoclVector/createLength (int 1)))

(eval
  `(extend-protocol mp/PImplementation
     ~@(mapcat 
         (fn [sym]
           (cons sym
             '(
                (implementation-key [m] :vectorz-opencl)
                (supports-dimensionality? [m dims] (<= 0 (long dims) 2))
                (new-vector [m length] (JoclUtils/createVector (int length)))
                (new-matrix [m rows columns] (JoclUtils/createMatrix (int rows) (int columns)))
                (new-matrix-nd [m shape] 
                               (case (count shape)
                                 0 (JoclScalar/create 0.0)
                                 1 (JoclUtils/createVector (int (first shape)))
                                 2 (JoclUtils/createMatrix (int (first shape)) (int (second shape)))
                                 (mikera.arrayz.Array/newArray (int-array shape))))
                (construct-matrix [m data]
                                  (if (instance? IJoclArray data)
                                    (.clone ^IJoclArray data)
                                    (let [da (vclj/vectorz-coerce data)
                                          shp (mp/get-shape da)
                                          ja (mp/new-matrix-nd JOCL-INSTANCE shp)]
                                      (mp/assign! ja da)
                                      ja))))))
         ['mikera.vectorz.jocl.JoclMatrix 'mikera.vectorz.jocl.JoclScalar 'mikera.vectorz.jocl.ADenseJoclVector]) ))

(imp/register-implementation JOCL-INSTANCE)



