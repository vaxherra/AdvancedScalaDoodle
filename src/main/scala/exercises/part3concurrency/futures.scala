package exercises.part3concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._ //DurationInt


object futures extends App{

  /*
  1. A future that returns immediately with a value:
   */

  val aFuture1 : Future[Int] = Future(25)
  val aFuture2 : Future[Int] = Future(45)
  def fullfillImmediately[T](value : T): Future[T] = Future(value)
  // since value is already computed, the future will return with a value'

  //--------------------------------------------------
  /*
  2. Run a function that returns a Future "B", AFTER a future "A" has finished running;
  def inSequence(fa, fb)
   */

  def inSequence[A,B](fa:Future[A], fb:Future[B]): Future[B] = {
    // runs future "b" (fb) after it has made sure that future "a" has finished running.

    // OPTION 1
   /* for {
      xa <- fa
      xb <- fb
    } yield xb
    */

    // OPTION 2: more compact
    fa.flatMap(_ => fb)
  }

/*
  Thread.sleep(200)
  println(inSequence(aFuture1,aFuture2))
*/

  //--------------------------------------------------
  /*
  3. Return a future containing the EARLIEST value returned by two futures;

  def first(fa,fb): new future with the first value of either fa or fb
   */

  def first[A](fa:Future[A], fb:Future[A]): Future[A] = {
    val promise = Promise[A]() // controller of a future of type A

    // SOLUTION 1: we perform a TRY, as promise can be fulfilled only once.
    /*
   fa.onComplete({
     case Success(v) => try{ promise.success(v) } catch {case _ =>}
     case Failure(t) => try{promise.failure(t)} catch {case _=>}
   })

    fb.onComplete({
      case Success(v) => try{ promise.success(v) } catch {case _ =>}
      case Failure(t) => try{promise.failure(t)} catch {case _=>}
    })
*/
    // SOLUTION 2: alternatively just check if promise is not already completed, before evaluating it;
    /*    fa.onComplete({
      case Success(v) => if(!promise.isCompleted) promise.success(v)
      case Failure(v) => if(!promise.isCompleted) promise.failure(v)
    })
    fb.onComplete({
      case Success(v) => if(!promise.isCompleted) promise.success(v)
      case Failure(v) => if(!promise.isCompleted) promise.failure(v)
    })
  */

    // SOLUTION 3 the BEST solution is to use already implemented function tryComplete on futures;
    // promise is TRIED to be completed, if it was not already completed by other future
    fa.onComplete(promise.tryComplete(_))
    fb.onComplete(promise.tryComplete) // and converted to a method value for better-looking syntax

    promise.future
  }

  //println(first(aFuture1,aFuture2))

  //--------------------------------------------------
  /*
  4. Similar to exercise (3), but return the LAST value returned by two futures;
   */

  def last[A](fa:Future[A],fb:Future[A]): Future[A] = {
    // THE LOGIC: both promises if they finish, will try to fulfill promise A,
    // if already fulfilled, then fulfill promise B, the promise B is the second one fulfilled
    // and we return its "future" value
    val promiseA = Promise[A]()
    val promiseB = Promise[A]()

/* the logic, but with duplicated code:
    fa.onComplete(result => {
      if(!promiseA.tryComplete(result)){
        promiseB.complete(result)
      }
    })

    fb.onComplete(result => {
      if(!promiseA.tryComplete(result)){
        promiseB.complete(result)
      }
    })
*/
    def checkAndComplete(result: Try[A]) = {
      if(!promiseA.tryComplete(result)){
        promiseB.complete(result)
    }}

      fa.onComplete(checkAndComplete)
      fb.onComplete(checkAndComplete)

      promiseB.future
  }


  //---------------------------------------------------------------------------
  //-------------------- TESTS ------------------------------------------------
  //---------------------------------------------------------------------------
  val fast = Future{
    Thread.sleep(100)
    42
  }

  val slow = Future{
    Thread.sleep(500)
    37
  }

  Thread.sleep(2500)
  first(fast,slow).foreach(f => println(s"FIRST to complete(fast=$fast, slow=$slow) :== $f"))
  last(fast,slow).foreach(f => println(s"LAST to cmplete (fast=$fast, slow=$slow) :== $f"))


  //--------------------------------------------------
  /*
  5. Run an action repeatedly until a condition is met, and return the first value that satisfies the condition
  def retryUntil[T]( action : () => Future[T], condition : T => Boolean): Future[T]

  runs the action, when it is completed then checks the condition, if it is not met, then runs the action again, until the condition is met.
  when it is met, returns the future as the result.
   */

  def retryUntil[T]( action : ()=> Future[T], condition : T => Boolean): Future[T] = {

    //triggering the action returns the future
    action(). // action returns a Future ...
    filter(condition). // ... if it succeeds, then return it ...
    recoverWith({
      case _ => retryUntil(action,condition) // ... or in case of any exception ...
    }) // ...  recover with another action
  }

  val random = new Random()
  // generates some random numbers
  val action = () => Future{
    Thread.sleep(100)
    val nextVal = random.nextInt(100)
    println(s"Generated: $nextVal")
    nextVal
  }

  retryUntil(action=action, condition = (x:Int) => x>95 ).foreach(f => println(s"Settled at $f"))
  Thread.sleep(10000)
}
