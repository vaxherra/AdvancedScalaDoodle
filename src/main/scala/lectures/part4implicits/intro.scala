package lectures.part4implicits

object intro extends App{
  // IMPLICITS INTRODUCTION

  val pair = "Robert" -> "Kwapich"
  println(s"01. $pair")
  // there is no arrow method "->" on the int class, but we know we are creating a pair instance


  case class Person(name:String){
    def greet = s"Hi, I am $name!"
  }
  // we'd like to have a method that converts a string to a Person object with name as that string

  implicit def fromStrToPerson(st:String):Person = Person(st)
  print("02. ")
  println("Robert Kwapich".greet) // calls greet method on string!
  // but behind the scenes the compiles looks for all implicit classes that could apply

  // compiler assumes there is only one thing that marches, for more, there will be ERRORS

  /*
  class A {
  def greet = "A"
  }

  implicit def fromStringToA(str:String): A = new A

  // this will throw an error
   */


    // -------------------------- IMPLICIT PARAMETERS
  def increment(x:Int)(implicit amount: Int) = x+amount
  implicit val default_amount : Int = 1

  println(s"03. Increment implicitly 5=>: ${increment(x=5)}")

  // not the same as default arguments, the implicit value is found by the compliler from its search scope!!!



}
