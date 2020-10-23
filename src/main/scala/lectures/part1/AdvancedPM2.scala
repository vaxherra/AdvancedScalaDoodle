package lectures.part1

object AdvancedPM2 extends App{
  //--------------------------------------------------//
  println("01. Infix patterns: custom ones")
  case class Or[A,B](a:A, b:B)
  // we can us it infix
  val either = Or(2,"two")

  val humanDesc = either match {
    //case Or(number,string) => s"$number is written as $string"
      // the above is equivalent to the below (infix patterns)
      // this pattern works for two arguments
    case number Or string => s"$number is written as $string"
  }
  println(humanDesc)

  //--------------------------------------------------//
  println("\n 02. Decomposing sequences")
  val numbers = List(1,2,3,4,5,10,15)
  val varargs = numbers match {
    case List(1,_*) => "starting with 1"
  }

  // when we define our own custom stuff, we'd like to apply the same custom "_*" for
  // pattern matching. The way to do this, is to define a custom unapply method
  abstract class MyList2[+A] {
    def head: A = ???
    def tail: MyList2[A] = ???
  }
  case object Empty extends MyList2[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList2[A]) extends MyList2[A]

  object MyList2 {

    // must retun an OPTION OF A SEQUENCE
    def unapplySeq[A](list : MyList2[A]): Option[Seq[A]] = {
      if(list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
    }
  }


  val exMyList2 : MyList2[Int] = Cons(1,Cons(2,Cons(3,Empty)))

  val exMylist2decomposed = exMyList2 match {
    // name of the singleton object
    case MyList2(1,2,_*) => "Starting a list with 1,2"
    case _ => "Not starting a list with 1,2"
  }
  println(exMylist2decomposed)
  //this allows for variable patterns, when we don't know how many element will be returned

  //--------------------------------------------------//
  println("\n 03. Custom types for RETURN types for unapply")
  // only needs to have:
  // 1. isEmpty: Boolean
  // 2. get : something

  class Person(val name:String, val age:Int) // not a case class, but we want to apply Pattern Matching
  val bob = new Person("Robert",21)

  // the KEY PART is to define these methods: isEmpty: Boolean and get : T
  abstract class Wrapper[T] {
    def isEmpty : Boolean
    def get : T
  }
  
  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false
      override def get: String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"We matched $n"
    case _ => "Nothing matched"
  })


  //--------------------------------------------------//
  //--------------------------------------------------//
  //--------------------------------------------------//
  //--------------------------------------------------//
  //--------------------------------------------------//
  //--------------------------------------------------//
  //--------------------------------------------------//
}
