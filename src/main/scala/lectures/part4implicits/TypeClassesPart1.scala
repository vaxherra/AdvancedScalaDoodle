package lectures.part4implicits

object TypeClassesPart1 extends App{
 // a type class is a trait that takes a TYPE and describes what types of operations can be applied to that class;

  trait HTMLWritable{
    def toHTML: String
  }

  case class User(name:String, age:Int, email:String) extends HTMLWritable{
    override def toHTML: String = s"<div>${name} (age: $age years old) <a href=$email>email</a> </div>"
  }


  val johnTheUser = User("John",25,"john@malkovich.com")
  /*
  This method has two big disadvantages:
  1. Only works for the types WE write: we would need to write conversions to other types, and that wouldn't be pretty
  2. Only one implementation out of many possible.

   */

    // option 2: would be to use pattern matching
  object HTMLSerialized {
      def serializeToHTML(value:Any) = value match {
        case User(name,age,email) =>
        //case java.util.Date => //...
        case _ => // we can manipulate here any data type
          // BUT WE LOST TYPE SAFETY - VALUE CAN BE ANY
          // each time we'd need to modify this code frequently
          // it is still one implementation for a given TYPE
          // i.e. when somebody is logged-in or logged-out -> differnt outputs
      }
    }


  // solutions: IMPLEMENT A SMALL TRAIT: TYPE CLASS
  trait HTMLSerializer[T]{
    def serialize(value:T): String
  }

  object UserSerialized extends HTMLSerializer[User]{
    override def serialize(value: User): String =  s"<div>${value.name} (age: ${value.age} years old) <a href=${value.email}>email</a> </div>"
  }
  // 01. WE CAN DEFINE SERIALIZERS FOR OTHER TYPES!
  // 02. WE CAN DEFINE MULTIPLE SERIALIZERS FOR A CERTAIN TYPE

  // 01.
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date]{
    override def serialize(value: Date): String = s"<div>the date: ${value.toString()}</div>"
  }

  println(
    UserSerialized.serialize(johnTheUser)
  )

  // 02.
  object PartialUserSerialized extends  HTMLSerializer[User]{
    // in case the user is not logged in, just gimme name
    override def serialize(value: User): String = s"<div>${value.name}</div>"
  }

  // a TYPE CLASS (trait HTMLSerializer[T]) specifies a set of operations (abstract methods) that can be applied to
  // a given type.
  // TYPE CLASS INSTANCES -- are all the implementers of the type class

  trait MyTypeClassTemplate[T]{
    def action(value:T): String
  }



}
