package lectures.part4implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPatterns extends App{
//  MAGNET PATTERN: a use case of type classess that aims at solving some of the problems created by method overloading;

    class P2PRequest
    class P2PResponse
    class Serializer[T]

    trait Actor{

      // ... a lot of overloads...
      /*
      this poses a lot of problems:
      1. TYPE ERASURE
      2. Lifting doesnt work for all overloads for Higher Order Functions!
            ex. val receiveFV = receive _ // the "_" is not possible to interpret by the compiler

      3. CODE DUPLICATIONS: code is very similar
      4. Type inference and default arguments,
          ex. actor.receive(?!) // what default args to featch for each method, and what overloaded method is used;

      the good news is that this API can be re-written;
       */
      def receive(statusCode:Int):Int
      def receive(request:P2PRequest): Int
      def receive(response:P2PResponse):Int
      def receive[T:Serializer](response:T):Int //(implicit serializer: Serializer[T]), if not context bound with [T:Serializer]
      def receive[T:Serializer](message:T,statusCode:Int):Int
      def receive(future:Future[P2PRequest]): Int
      //def receive(future:Future[P2PResponse]): Int // Type erasure: at compile time, the method signature checks
      // only that it takes some future, regardless of the type, as there would be two such methods -> compile error
    }

  trait MessageMagnet[Result]{
    def apply():Result
  }

  // single receive method
  def receive[R](magnet:MessageMagnet[R]): R = magnet() // magnet's apply method!

  // IMPLICIT CONVERSIONS TO AN IMPLICIT CLASS;
  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int]{
    // the Int is our original result type we want
    override def apply(): Int =  {
      // the exact logic for handling a P2P request
      println("Handling P2P request")
      42
    }
  }

  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {
    override def apply(): Int = {
      println("Handling P2P response")
      24
    }
  }

  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int]{
    override def apply(): Int = {
      println("Handling Future of P2PResponse")
      25
    }
  }
  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int]{
    override def apply(): Int = {
      println("Handling Future of P2PRequest")
      21
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  /*
  PROS & CONS OF THE MAGNET PATTERNS

  I. BENEFITS OF THE MAGNET PATTERN
  1. No more TYPE ERASURE problems

   */
  // THE COMPILER LOOKS AT IMPLICIT CONVERSIONS BEFORE! THE TYPES ARE ERASED DUE TO JAVA...heck with java
  println( receive( Future(new P2PResponse)  ))
  println( receive( Future(new P2PRequest)  ))


  // 2. LIFTING - also works (with a small catch)

  //given trait/library we want to magnetize...
  trait MathLib{
    def add1(x:Int): Int = x+1
    def add1(s:String): Int =  s.toInt + 1
    // .. other add1 overloads
  }
  // to MAGNETIZE THAT... write a small trait...
  trait AddMagnet{ // without the parameter type
    def apply(): Int
  }

  //... a magnet apply method....
  def add1(magnet:AddMagnet): Int = magnet()

  // and implicit classes...
  implicit class AddInt(x:Int) extends AddMagnet {
    override def apply(): Int = x+1
  }

  implicit class AddStringInt(x:String) extends AddMagnet{
    override def apply(): Int = x.toInt + 1
  }


  println(add1("12"))

  val addFunctionFalue = add1 _

  // this is reall nice for TRULY FUNCTIONAL API
  println(addFunctionFalue(25))
  println(addFunctionFalue("25"))

  // the catch:
  // it we added type parameter to AddMagnet trait, then the addFunctionValue would not know to which TYPE
  // this apply method of add1 would be used for

  // hence
  // val receiveFunctionValue = receive _ // would not work!


  /*
    II. CONS OF THE MAGNET PATTERN
      1. Verbose: our API blown into magnet, receive, implicit conversions - harder to read
      2. you can't name or place DEFAULT ARGUMENTS
      3. hard to debug: call by name does not work correctly (see magnet exercise file)
     */

}
