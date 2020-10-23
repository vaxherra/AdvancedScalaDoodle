package exercises.part1n2

import scala.annotation.tailrec

abstract class myStream[+A] {

  def isEmpty: Boolean
  def head: A
  def tail: myStream[A]

  def #::[B>:A](elem:B): myStream[B] // right associative prepend an elements of type A to the stream
  def ++[B >: A](otherStream: => myStream[B]): myStream[B] // concatenate two streams

  def foreach(f: A=>Unit): Unit
  def map[B](f: A => B): myStream[B]
  def flatMap[B](f: A=> myStream[B]): myStream[B]
  def filter(predicate: A => Boolean): myStream[A]

  def take(n:Int): myStream[A] // returns n first elements
  def takeAsList(n:Int): List[A] = this.take(n).toList()

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] = { // final - do not overwrite in inherited classes
    if(isEmpty) acc
    else tail.toList(acc = acc :+ head ) // prepend a head to the accumulator list
  }
}

object emptyStream extends myStream[Nothing] {

  def isEmpty: Boolean = true
  def head: Nothing = throw new NoSuchElementException
  def tail: myStream[Nothing] = throw new NoSuchElementException

  def #::[B>:Nothing](elem:B): myStream[B] = new stCons[B](hd = elem, tl = this)
  // right associative prepend an elements of type A to the stream
  def ++[B >: Nothing](otherStream: => myStream[B]): myStream[B] = otherStream // concatenate two streams

  def foreach(f: Nothing=>Unit): Unit = () // unit
  def map[B](f: Nothing => B): myStream[B] = this
  def flatMap[B](f: Nothing => myStream[B]): myStream[B] = this
  def filter(predicate: Nothing => Boolean): myStream[Nothing] = this

  def take(n:Int): myStream[Nothing] = this // returns n first elements
  // def takeAsList(n:Int): List[Nothing] = Nil

}

class stCons[+A](hd:A, tl: => myStream[A]) extends myStream[A] { //streamed Cons - stCons

  // tl is CALLED BY NAME! it is crucial!
  def isEmpty: Boolean = false
  override val head: A = hd
  override lazy val tail: myStream[A] = tl // CALL BY NEED
  // we have tail as call by name, and combine that with lazy val

  def #::[B>:A](elem:B): myStream[B] = new stCons[B](hd=elem,tl = this) // right associative prepend an elements of type A to the stream
  def ++[B >: A](otherStream: => myStream[B]): myStream[B] = new stCons[B](hd= head, tl = tail ++ otherStream)
  // concatenate two streams preserves lazy evaluation in the stream,
  // notice we use 'tail' and not 'tl', and that 'tail' is a lazy val;

  def foreach(f: A=>Unit): Unit ={
    // forces eveluation for every single value in the stream
    f(head)
    tail.foreach(f)
  }

  def map[B](f: A => B): myStream[B] = new stCons[B](hd = f(head), tl = tail.map(f)  ) // tail evaluated by need;
  def flatMap[B](f: A=> myStream[B]): myStream[B] = f(head) ++ tail.flatMap(f)  // tail is lazily evaluated;
  def filter(predicate: A => Boolean): myStream[A] = {
    if(predicate(head)) new stCons[A](head, tail.filter(predicate)) // tail is lazily evaluated
    else tail.filter(predicate) // will force at most eveluation of the tail's head, and will preserve lazy evaluation
  }

  def take(n:Int): myStream[A] = {
    if(n<=0) emptyStream
    else if(n==1) new stCons[A](head, emptyStream)
    else new stCons[A](hd=head, tl = tail.take(n-1)) // combination of by name parameter tl, and lazily evaluated tail
    // i.e. CALL BY NEAD

  }// returns n first elements
  // def takeAsList(n:Int): List[A]

}

// companion object
object myStream {
  def from[A](start:A)(generator: A=>A): myStream[A] = {
    new stCons[A](hd = start, tl = myStream.from(start = generator(start))( generator = generator) )
  }
  // generator generates next value based on the previous value known in the stream
}

object test extends App{


  // val naturals = new myStream.from(1)( x=> x+1) // stream of natural numbers

  // naturals.take(100) // lazily evaluated stream of the first 100 naturals (a finite stream)

  // naturals.foreach(println) // should crash, as the stream is infinite
  // naturals map (_*10) // another infinite stream


  val naturals = myStream.from(1)(x => x+1)
  (0 #:: naturals).map(_*10).map(_+" ").take(15).toList().foreach(print)

  println("\n Natural numbers flatmapped")
    // flatMap otherStream needs to be by name, otherwise a stack overflow occurs
  naturals.flatMap(x=> new stCons[String](x+" ", new stCons[String]((x+10)+" ", emptyStream)) ).take(20).toList().foreach(print)

  //
  //naturals.filter(_<10).take(10).map(_+" ").foreach(println)
  //println(naturals.filter((_<10)).toList())
  println("\nNaturals to a finite stream: \n \t")
  // println(naturals.filter(x=>x<50).toList()) // compiles tries to go through all naturals, and see if they are less
  // than 10 -> stack overflow error
  println(naturals.filter(x=>x<50).take(10).toList()) // compiles tries to go through all naturals, and see if they are less
    // than 10, so we need to take less or equal to the elements available in the stream

  /*
  Exercise 1: stream of fibonnaci numbers:
  Exercise 2: stream of prime numbers with Eratosthenes's sieve
    start with natural numbers [2 3 4 5 ... ],
    filter out all nums, divisible by 2: [2 3 5 7 9 11]
    filter out all numbers divisible by 3: [2 3 5 7 11 13]
    [filter out all numbers next in the sieve: [ 2 3 5 7 11 13]

   */

  //

  def fibonacci(x1: BigInt,x2:BigInt): myStream[BigInt] = {
    new stCons[BigInt](hd=x2, tl= fibonacci(x1=x2,  x2= (x1+x2) )  )
  }
  println("Fibonacci: ")
  println(fibonacci(1,1).take(100).toList() )

  // erathostenes sieve
  def primesErato(numbers: myStream[BigInt]): myStream[BigInt] = {
    if(numbers.isEmpty) numbers
    else new stCons[BigInt](numbers.head,  primesErato( numbers.tail.filter(_%numbers.head!=0) ))
  }
  println("Primes ertathostenes ")
  println(       primesErato( myStream.from(2:BigInt)(x=>x+1 : BigInt)).take(100).toList()       )
}