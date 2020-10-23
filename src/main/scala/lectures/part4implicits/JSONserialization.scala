package lectures.part4implicits

import java.util.Date

object JSONserialization extends App{
  /*
  Imagine we have users, posts, feeds in the social network example
  we want to serialize them to JSON
   */

  // serialize these 3 to JSON, because we assume the codebase is huge, we don't implement these JSON serialization
  // functions here, we want this to be extensible -> TYPE CLASS PATTERN
  case class User(name:String, age:Int, email:String)
  case class Post(content:String, createdAt:Date)
  case class Feed(user:User, posts: List[Post])

  /*
  1. Intermediate data types: Int, String, List, Date
  2. Type classes for conversion from case classes to intermediate data types;
  3 Serialize intermediate data types to JSON
   */

  // 1. Intermediate data type starting our hierarchy
  sealed trait JSONValue{
    def stringify: String
  }


  final case class JSONString(value:String) extends JSONValue {
    override def stringify: String = "\""+value+"\"" // a value with some strings (simplified)
  }
  final case class JSONNumber(value:Int) extends JSONValue {
    override def stringify: String = value.toString
  }
  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String = values.map(_.stringify).mkString("[",",","]") // stringify each element with map,
    // and make one simple string with mkString
  }
  final case class JSONObject(values: Map[String,JSONValue]) extends JSONValue{
    // intermediate representation
    /* example
    {
    name : "John",
    age : age,
    email : john@office.com
    latestPost : {
        content : "  ... ",
        createdAt: <date>
       ... and so on
      }
    }
     */
    override def stringify: String = values.map{
      case (key,value) => "\"" + key +"\":" + value.stringify
    }.mkString("{", "," , "}")
  }

  // 2. Type class to convert case classess to JSONValue
  /*
    1 - type class itself
    2 - type class instances which are implicit
    3 - method "pimp library" to use type class instances
   */

  // 2.1. type class
  trait JSONConverter[T]{
    def convert(value:T): JSONValue
  }

  // 2.c. conversion
  implicit class JSONEnrichement[T](value:T) { // a.k.a. JSONOps <OPS, ENRICHEMENT>
    def toJSON(implicit converter : JSONConverter[T]): JSONValue = {
      converter.convert(value)
    }
  }

  // 2.2. type class instances
  // a. for existing data types
  implicit object StringConverter extends JSONConverter[String] {
    override def convert(value: String): JSONValue = JSONString(value)
  }

  implicit object NumberConverter extends JSONConverter[Int] {
    override def convert(value: Int): JSONValue = JSONNumber(value)
  }

  // b. for custom data types *users, posts, feeds*

  implicit object UserConverter extends JSONConverter[User]{
    override def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(user.name),
      "age" -> JSONNumber(user.age),
      "email" -> JSONString(user.email)
    ))
  }

  implicit object PostConverter extends JSONConverter[Post] {
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(post.content),
      "createdAt" -> JSONString(post.createdAt.toString)
    ))
  }

  implicit object FeedConverter extends JSONConverter[Feed]{
    override def convert(value: Feed): JSONValue = JSONObject(Map(
/*      "user" -> UserConverter.convert(value.user), //TODO refactor
      "posts" -> JSONArray(value.posts.map(PostConverter.convert)) // TODO unecessary dependency*/


      "user" -> value.user.toJSON, // REFACTORED here
      "posts" -> JSONArray(value.posts.map(_.toJSON)) // REFACTORED here

    ))
  }

/*  // 2.c. conversion
  implicit class JSONEnrichement[T](value:T) { // a.k.a. JSONOps <OPS, ENRICHEMENT>
      def toJSON(implicit converter : JSONConverter[T]): JSONValue = {
        converter.convert(value)
      }
  }
 */
  // 3. PIMP LIBRARY


 // TESTS
  // 1. test intermediate data types
  println("01. testing intermediate data types")
  val data = JSONObject(Map(
    "user" -> JSONString("Robert"),
    "posts" -> JSONArray(List(
      JSONString("Some first post"),
      JSONNumber(42)
    ))
  ))
  println(data.stringify)

  // 2. test type classes
  val nowDate = new Date(System.currentTimeMillis())
  val john = User("John",29,"john@office.com")
  val feed = Feed(john, List(
    Post("Hello, my second post", nowDate),
    Post("I am in not in danger, I am the danger!", nowDate)
  ))

  println("02. Testing type classes")
  println(feed.toJSON.stringify)
}
