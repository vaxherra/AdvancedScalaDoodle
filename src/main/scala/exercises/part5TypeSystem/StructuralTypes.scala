package exercises.part5TypeSystem

object StructuralTypes extends App{
  /*
  - - - - - - - - - - 01. EXERCISE
   */

  trait CBL[+T] { // Cons Based List
    def head: T
    def tail : CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain  {
    override def toString: String =  "Brainzzz!"
  }

  def f[T](smthWhHead : {def head: T }   ): Unit = println(smthWhHead.head)
  // something with head, has a head of type T

  /*
  Question is f compatible with CBL and a Human instance?

  Answer: The function 'f' is compatible with both
   */

  case object CBNil extends CBL[Nothing]{
    override def head: Nothing =  ???
    override def tail: CBL[Nothing] = ???
  }

  case class CBCons[T](override val head :T, override val tail: CBL[T]) extends CBL[T]

  println("Exercise 1:")
  f(new CBCons(2,CBNil))
  f(new Human) // human has a head, and its head is of type [Brain], hence function f[Brain] = T

  /*
  - - - - - - - - - - 02. Exercise in compatibility
   */

  object HeadEqualizer{
    type Headable[T] = { def head:T   } // structural type alias
    def ===[T](a: Headable[T], b :Headable[T]): Boolean = a.head == b.head
  }

  /*
  Question: is HeadEqualizer compatible with CBL and a Human instance?

  Answer: YES it is.
   */

  println("Exercise 2:")
  val brainzList = CBCons(new Brain, CBNil)
  println(brainzList)

  println(HeadEqualizer.===(brainzList,brainzList))
  println(HeadEqualizer.===(new Human,brainzList)) // the 'head' field of provided arguments is of 'Brain' type, OK

  //this also poses a problem; as we can say:
  val stringList = CBCons("brains the string",CBNil)
  println( HeadEqualizer.===(new Human, stringList) )
  // human head is of type Brain, while 'stringList' head is of type String, however the equalizer is
  // NOT THROWING AN EXCEPTION, it is NOT TYPE SAFE

  // because the structural implementation of "def ===" RELIES ON REFLECTION!, THE TYPE PARAMETER 'T' IS ERASED!



}
