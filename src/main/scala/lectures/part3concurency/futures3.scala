package lectures.part3concurency

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._ //DurationInt

object futures3 extends App{

  // --------------------------------------------------------------------------------------------------
  // --------------------- BLOCKING ON THE FUTURE -----------------------------------------------------
  // --------------------------------------------------------------------------------------------------
  // for transaction like operations, where we want to make sure that operation if fully complete;

  // online banking app -- toy example of course
  case class User(name:String)
  case class Transaction(sender:String,receiver:String, amount:Double, status: String = "Default status of the transaction")

  object BankingAPP{
    val name ="Kwapich Banking"

    def fetchUser(name:String): Future[User]  = Future{
      // simulate long computation
      Thread.sleep(500)
      User(name)
    }

    def createTransation(user:User, merchantName:String, amount:Double) : Future[Transaction] = Future{
      // simulate some processes (enough funds, is a valid transaction etc...) with waiting;
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "Status: OK" )
    }

    def purchase(username:String,item:String, merchantName:String,cost:Double) : String = {
      // 1. fetch the user from the Database (may take long time, so that's why it's future),
        // (we don't simulate if they have enough money etc...; we just 'sleep',
      // 2. create a transaction from the username to the merchant name
      // 3. wait for the transaction to finish.
      val transationStatusFuture = for {
          user <- fetchUser(username)
          transaction <- createTransation(user,merchantName,cost )
        } yield transaction.status
      // ------------------------------------------ THIS BLOCKS UNTIL ALL THE FUTURES ARE COMPLETED!
      Await.result(transationStatusFuture, 2.seconds) // INT uses implicit conversions here
      /// there is "Await.ready"  (todo check its implementation and usage)
      // import scala.concurrent.duration._ //DurationInt
      // a technique called "PIMP MY LIBRARY"
    }

    // end of object: BankingApp
  }

  // println(BankingAPP.purchase("Robert K.", "Cyberpunk 2077","CD PROJEKT RED", 59.99))

  // --------------------------------------------------
  // PROMISES, the PROMISE PATTERN:
  // one thread 1, knows how to handle the future
  // other thread 2, inserts values/failures into the future
  /// end of futures3 app
  val promise = Promise[Int]() // some sort of controller over future
  val someFuture = promise.future

  // a toy example of consumers and producers using promises;
  // thread 1 - "consumers", know how to handle futures completion
  someFuture.onComplete({
    case Success(r) => println(s"Consumer: I have received $r")
  })

  // thread 2: the producer;
  val producer = new Thread( ()=>{
    println(s"Producer: Crunching numbers....")
    Thread.sleep(500)
    // fullfilling the promise;
    promise.success(42) // completes the promise!
    // alternatively promise.failure(...)
    println("Producer: done producing")
  })
  
  producer.start()
  Thread.sleep(1000)
}
