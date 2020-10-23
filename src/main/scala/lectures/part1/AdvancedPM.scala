package lectures.part1

object AdvancedPM extends App{
  //--------------------------------------------------
  println("01. Matching a list head :: tail, head :: Nil")
  val aList = List(1,2,3,4,5,6,7,8,9,10)
  val aListMatched = aList match {
      // infix pattern
    case head :: Nil => s"the only element is $head"
    case h :: tail => s"the list has head $h and tail $tail" //this will be executed 
  }
  println(aListMatched)

  //--------------------------------------------------
  println("02. Matching a class that is not a case class")
  class Person(val name:String, val age:Int) // not a case class, but we want to apply Pattern Matching
  
  // first define an object companion, and define a special method
  object Person  {
    // UNAPPLY METHOD IS THE MAGIC HERE
    // we can put some IF conditions here ! but in practice
    def unapply(arg: Person): Option[(String, Int)] = Some((arg.name,arg.age))

    // overoading
    def unapply(age: Int): Option[String] = {
      if(age<21) Some("minor")
      else Some("adult")
    }
  }

  val bob = new Person("Bobby", 19)
  val greetBob = bob match {
    case Person(x,y) => s"Guy of age $y is named $x " //this matches the singleton object not a class
      // but in practice it maches the class companion OBJECT
    case _ => "Don't bother, it is somebody else"
  }
  println(greetBob)

  val legalStatus = bob.age match {
    case Person(status)  => s"$status"
    case _ => "What the?"
  }
  println(s"legal status of ${bob.name} is $legalStatus")

  //--------------------------------------------------
}
