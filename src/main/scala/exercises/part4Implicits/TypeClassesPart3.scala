package exercises.part4Implicits


object TypeClassesPart3 extends App{
  // Exercise: improve the Equal Type Class with implicit conversion class
  /*
  def ===(another value: T) invokes the equal types class
  !==(another value : T)
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

  implicit class ObjectEqualities[T](value:T){
    def ===(anotherValue : T)(implicit equalizer : Equal[T]): Boolean=  equalizer.equal(value,anotherValue)

    def !==(anotherValue : T)(implicit equalizer : Equal[T]): Boolean=  !equalizer.equal(value,anotherValue)
  }

  println(user1===user2) // this uses the implicit equalizer, that by default is the implicit object NameEquality,
  // and for this reason user1 is equal to user2, as they both have the same 'name' parameter

  /*
  the above is re-written as
  new ObjectEqualities[User](user1 : User).===(user2)(NameEquality: Equal[User])
   */

  println(user1!==user3)

  /*
  they are TYPE SAFE!!!

  so a standard comparison user1==42 will work and return False, while
  user1===42, is going to THROW AN ERROR as compiler cannot find an implicit equalizer of this type

   */
}
