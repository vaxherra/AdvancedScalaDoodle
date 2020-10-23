package lectures.part4implicits

object TypeClassesPart2 extends App{
  // providing implicit values and parameters to the type classess

  case class User(name:String,age:Int,email:String)

  // TYPE CLASS
  trait HTMLSerializer[T]{
    def serialize(value:T): String
  }

  // we can define implicit objects that would detect a specific type
  // a: Int serializer

  // TYPE CLASS INSTANCE
  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div>$value</div>"
  }
  // b: User serializer
  // TYPE CLASS INSTANCE
  implicit object UserSerializer extends HTMLSerializer[User]{
    override def serialize(value: User): String = s"<div>User name: ${value.name} of age ${value.age} with email: ${value.email}</div>"
  }

  object HTMLSerializer{
    def serialize[T](value:T)(implicit serializer: HTMLSerializer[T]): String = {
      serializer.serialize(value)
    }

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }


  val john = User("John",33,"john@office.com")

  // a very neat and nice API design
  println(    HTMLSerializer.serialize(42)    )
  println(    HTMLSerializer.serialize(john)    )

  // using the apply method
  // with this we have access to entire TYPE CLASS INTERFACE!
  println(    HTMLSerializer[User].serialize(john))


 // ----------------------------------------------------------------------

  trait MyTypeClassTemplate[T]{
    def action(value:T): String

    // here goes the rest of the interface we'd like to use
  }

  object MyTypeClassTemplate{
    def apply[T](implicit  instance : MyTypeClassTemplate[T]) = instance
  }
  // when we later use MyTypeClass we can use a simple apply method, and then use its entire interface



}
