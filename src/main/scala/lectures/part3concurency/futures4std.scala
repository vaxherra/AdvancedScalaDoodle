package lectures.part3concurency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.ParVector

object futures4std extends App{
// SCALA PARALLEL UTILITIES

  // I. PARALLEL COLLECTIONS:

  val parList = List(1,2,3).par
  val aParVector = ParVector[Int](1,2,3)

  // MEASURING the time of an operation
  def measure[T](operation: => T): Long = {
    val start = System.currentTimeMillis()
    operation
    System.currentTimeMillis()-start
  }


  // some big lists tests:
  val list = (1 to 10000000).toList
  val serialTime = measure { list.map(_+1)}

  val parTime = measure{ list.par.map(_+1)}

  println(s"Linear time $serialTime \n Parallel time $parTime")

   /*
   Parallel collections operate on the well-known Map-reduce model;
   - splitting elements into chunks that will be processed independently by a single thread -> by spliter
   - elements are combined with Combinder (reduce step)
   -
    */

  //------------------------------ PROBLEMS WITH PARALLEL COLLECTIONS
  // 1. #########################
  // be very careful with fold and reduce, as we don't know the order in which they are run!!!!
  // the operations are NOT ASSOCIATIVE
  println("No par/par on reduce _-_")
  println(List(1,2,3).reduce(_-_))
  println(List(1,2,3).par.reduce(_-_))

  // 2. #########################
  // if we need synchronizations on the results
  var sum = 0

  List(1,2,3).par.foreach(sum += _ )
  println(s"Summing of (1,2,3)=$sum should be 6, but is not guaranteed!")
  // it is not guaranteed, foreach is spawning multiple threads and there might be race conditions,
  // side effects might be calculated in a different order

  //--------------------------------------------------
  // configuring parallel collections;
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))
  /*
  alternatively we can use:
  - (deprecated) ThreadPoolTaskSupport
  - ExecutionContextTaskSupport(EC) // EC - execution context, compatible with futures API
  - creating new task support = new TaskSupport {
  override execute ... -> schedules to run in parallel
  override execudeAndWaitResult ...  -> ... also blocks for a thread to join
  override parallelismLevel ... -> # of cpu cores
  override val environment ... -> the environment manager that manages the threads: ForkJoinPoll, execution context,
                                   could be anything you want;
  }
   */


  //--------------------------------------------------
  //----- ATOMIC OPERATIONS AND REFERENCES -----------
  //--------------------------------------------------

  // atomic cannot be divided -> runs fully or not at all; cannot be intercepted by another thread;
  // java atomic types are thread safe
  val atomic = new AtomicReference[Int](2)
  // there are atomic operations
  val currentVal = atomic.get() // THREAD SAFE! no other thread can read(get) // or write in setters
  atomic.set(4) // THREAT-SAFE WRITE


  atomic.getAndSet(5) // a combo in a THREAD-SAFE way, first get and then set

  atomic.compareAndSet(38,56) // 1st expectedValue, 2nd newValue
  // if expected, then set to new value, otherwise nothing -> all in a thread safe way!
  // REFERENCE equality only

  atomic.updateAndGet(_+5) // applies a function! in a thread-safe way
  atomic.getAndUpdate(_+1) // does the opposite, first gets, and then applies a function in a thread safe way

  atomic.accumulateAndGet(12, _+_) // takes the argument, the atomic value, and performs a function
  atomic.getAndAccumulate(12, _+_) // returns stuff, and then accumulates with atomic value and passed argument
}
