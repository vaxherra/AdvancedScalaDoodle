package exercises.part3concurrency

object introExercises extends App{
  /*
  1. Construct 50 inception threads:

  inception thread constructs other threads:
  thread 1 -> thread 2 -> thread 3
   and do println(s"hello from thread ${thread_num}"
   and do that in reverse order (start/join methods)

   */
  def inceptionThread(maxThreads:Int, i:Int = 1): Thread =    new Thread( () => {
    // returns a single thread that starts other thread

     if(i < maxThreads ) {
       val newThread = inceptionThread(maxThreads,i+1)
       newThread.start()
       newThread.join() //wait for it to finish
     }
     println(s"Hello from thread ${i}")


   })

  inceptionThread(20).start() // from 20 -> 1




  /*
  2.

  Q1. what is the biggest value possible for x?
    - if they run sequentially, so 1 to X, the X is the max
  Q2. What is the smallest value possible for x?
    - if all the threads run in parallel, so Y to X, and then Y+initial value
   */
  var x= 0
  val threads = (1 to 1000).map(_ => new Thread(() => x+=1))
  threads.foreach(_.start())
  threads.foreach(_.join())
  println(s"Ex 2: x=${x}")

  /*
  3. SLEEP FaLLACY exercise

  very wrong programming practice at putting them  to sleep at different times

   */

  var message = ""

  val awesomeThread = new Thread( () => {
    Thread.sleep(1000)
    // the problem here is that awesomeThread does not take exactly 1000ms, but at least 1000ms,
    // the OS might do this in let's say 5000ms, as CPU has a lot to handle,
    // then the output is not predictable!
    message = "Scala is awesome"
  })

  message = "Scala sucks!"
  awesomeThread.start()
  Thread.sleep(1001)
  println(message) // what is the value for message?


  // almost always "Scala is awesome" but it IS NOT GUARANTEED!

  /* INTUITION
  msg = "Scala sucks"
  awesomeThread.start()
    sleep - relives executions
   (awesome thread)
    sleep - also relives execution
    (OS gives CPU to some important thread, which can take more than 2 seconds)
    at this point the threads both could finish running
    and we can get "Scala sucks"  thorugh println
    and then we can finish awesomeThread
   */

  // how to fix it?
  //synchronizing won't work here. Synchronization works for concurrent stuff, when threads attempt to modify
  // the same variable at the same time
  // but here we have a sequential problem, we must joint them

  // so, BEFORE PRINTLN we'd "awesomeThread.join()" waiting for it to finish
}
