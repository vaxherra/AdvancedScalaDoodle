package exercises.part4Implicits


object TypeClassesPart2 extends App{
  /*
  Ex 2: Implement type class patter for the equality type class from part 1
   */

  case class User(name:String,age:Int,email:String){
  }

  val user1 = User("John",33, "johntravolta@office.com")
  val user2 = User("John",33, "johntravoltaFake@office.com")
  val user3 = User("Mark",33, "marktravolta@office.com")


  trait Equal[T]{
    def equal(v1:T,v2:T): Boolean
  }

  // compare users by name AND (other type instace) by name and email

  // an implicit object, a single one
  implicit object NameEquality extends Equal[User]{
    override def equal(v1: User, v2: User): Boolean = v1.name == v2.name
  }

  object NameAndEmailEquality extends Equal[User]{
    override def equal(v1: User, v2: User): Boolean = {
      (v1.name==v2.name) && (v1.email== v2.email)
    }
  }


  object Equal{
    def apply[T](v1:T,v2:T)(implicit equalizer: Equal[T]) : Boolean = {
      equalizer.equal(v1,v2)
    }
  }

  // AD-HOC POLYMORPHISM !!!
  // depending on the types we want to compare equals, the COMPILER fetches the correct TYPE CLASS INSTANCE
  // for that types
  println(    Equal(user1,user2)   )

}
