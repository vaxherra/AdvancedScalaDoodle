package exercises.part4Implicits

object TypeClassesPart1 extends App{
  /*
  Ex. 1
  The equality type class
   */

  case class User(name:String,age:Int,email:String){
  }

  trait Equal[T]{
    def equal(v1:T,v2:T): Boolean
  }

  // compare users by name AND (other type instace) by name and email
  object NameEquality extends Equal[User]{
    override def equal(v1: User, v2: User): Boolean = v1.name == v2.name
  }

  object NameAndEmailEquality extends Equal[User]{
    override def equal(v1: User, v2: User): Boolean = {
      (v1.name==v2.name) && (v1.email== v2.email)
    }
  }

  val user1 = User("John",33, "johntravolta@office.com")
  val user2 = User("John",33, "johntravoltaFake@office.com")
  val user3 = User("Mark",33, "marktravolta@office.com")

  println(s"01. ${NameEquality.equal(user1,user2)}")
  println(s"02. ${NameAndEmailEquality.equal(user1,user2)}")
  println(s"03. ${NameEquality.equal(user1,user3)}")

}
