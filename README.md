# Advanced Scala Playground

Code written while playing with the concepts covered in Advanced Scala Course offered on https://rockthejvm.com/p/advanced-scala. 

## Note on structure

The main topics covered are residing in `lectures` and `exercises` folders in `AdvancedScalaDoodle/src/main/scala/`. Both lectures and notes contain my comments and testing out various concepts in Scala language. The exercises contain full problem specification and my solution offered.

## Topics covered 

### Part 1. Advanced Scala Intro
  1. "Dark" Syntax Sugars
      - single parameter methods
      - lambda reductions
      - `::` and `#::` methods
      - multi-word method naming
      - some "generic's" features
      - update and setters
  2. Advanced Pattern Matching
    - matching classes that are not `case class`: the unapply method overloading
    - infix patterns
    - decomposing sequences
    - custom return types for `unapply`
    
### Part 2. Advanced Functional Programming
  1. Partial Functions
  2. Functional Sets
  3. Streams
  4. Currying with Partially Applied Functions
  5. Lazy evaluations
  6. Monads (with exercises)
  
### Part 3. Functional Concurrent Programming
  1. Parallelism on the JVM
  2. Concurrency problems on the JVM
  3. JVM thread communication, the producer-consumer problem
  4. Futures and Promises
  5. Scala & JVM Standard Parallel Libraries
  
### Part 4. IMPLICITS
  1. Implicits overview
  2. Organizing Implicits
  3. Type Classes 
  4. "Pimp my library" pattern
  5. JSON serialization with implicits (exercise)
  6. Scala <> Java conversions with implicits
  
### Part 5. Mastering the Type System
  1. Advanced Inheritance
  2. Veriance: Invariance, Covariance, Contravariance (+ exercises)
  3. Type members
  4. Inner Types
  5. Structural Types
  6. Self-types
  7. F-bounded polymorphisms
  8. Higher-Kinded Types
  9. Reflections
