package lectures.part5TypeSystem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HigherKindedTypes extends App{
  // HIGHER KINDED TYPES: are deeper generic types with some unknown parameter at the deepest level;

  trait AHigherKindedType[F[_]] // a parameter type F that also takes 'higher-kinded types'

  // example #1: we have multiple similar traits
  trait MyList[T]{
    def flatMap[B](f: T=>B): MyList[B]
    def map[B](f : T => B): B
  }
  trait MyOption[T]{
    def flatMap[B](f: T=>B): MyOption[B]
    def map[B](f : T => B):B
  }
  trait MyFuture[T]{
    def flatMap[B](f: T=>B): MyFuture[B]
    def map[B](f : T => B): B
  }

  // combine/multiply, ex. list(a,b) and list(1,2) -> list(1a,1b,2a,2b)
  // for these traits we define a combine method, for different traits
  // how to avoid this boilerplate, assuming that List, Option and Future are/we want to treat them as unrelated types?

  def combine[A,B](listA: MyList[A], listB: MyList[B]): MyList[(A,B)] = {
    for{
      a <- listA
      b <- listB
    } yield (a,b)
  }

  def combine[A,B](listA: MyOption[A], listB: MyOption[B]): MyOption[(A,B)] = {
    for{
      a <- listA
      b <- listB
    } yield (a,b)
  }

  def combine[A,B](listA: MyFuture[A], listB: MyFuture[B]): MyFuture[(A,B)] = {
    for{
      a <- listA
      b <- listB
    } yield (a,b)
  }


  // ------------------------------------------------------------- solution: Use a Higher Kinded Type

  trait myMonad[F[_],A]{ // a higher-kinded type class
    def flatMap[B](f : A=> F[B]): F[B]
    def map[B](f : A=>B): F[B]
  }

  implicit class myMonadList[A](mylist : List[A]) extends myMonad[List, A]{
    // a wrapper over a list;
    override def flatMap[B](f: A => List[B]): List[B] = mylist.flatMap(f)
    override def map[B](f: A => B): List[B] = mylist.map(f)
  }

  implicit class myMonadOption[A](myOption: Option[A]) extends myMonad[Option, A]{
    // a wrapper over a list;
    override def flatMap[B](f: A => Option[B]): Option[B] = myOption.flatMap(f)
    override def map[B](f: A => B): Option[B] = myOption.map(f)
  }

  implicit class myMonadFuture[A](myFuture : Future[A]) extends myMonad[Future, A]{
    // a wrapper over a list;
    override def flatMap[B](f: A => Future[B]): Future[B] = myFuture.flatMap(f)
    override def map[B](f: A => B): Future[B] = myFuture.map(f)
  }

  val myMonadListInstance = new myMonadList(mylist = List(1,2,3,4,5,6,7,8,9,10))
  myMonadListInstance.flatMap(x => List(x,x+1)) // myMonad[List,Int] turns into List[Int]
  myMonadListInstance.map(_+10) // myMonad[List,Int] turns into List[Int]

  // a single method that applies to all the myMonad KINDS!
  // a single method that has a map and a flatMap (needed for 'for' comprehensions)
  def multiply[F[_],A,B]( ma : myMonad[F,A],  mb: myMonad[F,B] ): F[(A,B)] = {
    // type parameters A and B, and type of the monad which is being passed, i.e. the HKT (Higher Kinded Type)
    for {
      a <- ma
      b <- mb
    } yield (a,b)
  }

  println(
  multiply(new myMonadList( (1 to 5).toList ), new myMonadList(List("a","b","c")))
  )


 // utilizing the implicits
  println(
    multiply( (1 to 5).toList , List("a","b","c"))
  )

  println(
    multiply( Some(1) ,Some("a"))
  )

  // as a result multiply applies both to options and list with THE SAME IMPLEMENTATION!
}
