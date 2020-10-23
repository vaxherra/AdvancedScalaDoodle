package lectures.part3concurency

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object futures extends App{
// FUTURES AND PROMISES;

  // a functional way of computing something in parallel or on another thread;

  def calculateSmth: Int = {
    Thread.sleep(1000)
    42
  }

  val aFuture = Future{
    calculateSmth //calculates on another THREAD;
  }// global is passed (see import *Implicits.global)

  // on complete returns a "TRY" (as the calculation might have failed)
  aFuture.onComplete({ //on complete is called by some thread
    case Success(x) => println(s"The result of the calculation is $x")
    case Failure(exception) => println(s"I failed with exception $exception")
  })

  // the JVM main thread can complete before the future completes
  // so we introduce some sleep here
  Thread.sleep(2000)




}
