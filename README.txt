Experimental OpenCL support for Vectorz

Uses Jocl as an interface to OpenCL

Initial tests suggest very high overhead on Jocl calls (on the order of 5000ns for typical operations)
This suggests that the implementation should not be recommended for work with small arrays, where
pure-JVM vectorz is likely to perform much better

Notes:
- Defines new JoclMatrix, JoclVector classes that work as valid Vectorz arrays
- vectorz-jocl itself is pure JVM code (but requires Jocl)
- Testing includes compliance checks with core.matrix for Clojure usage