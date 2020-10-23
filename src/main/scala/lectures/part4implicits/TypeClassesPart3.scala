package lectures.part4implicits

import lectures.part4implicits.TypeClassesPart2.{HTMLSerializer, UserSerializer,IntSerializer,User}

object TypeClassesPart3 extends App{

  val john = User("John",33,"john@office.com")

  // conversion with implicit class
  implicit class HTMLEnrichement[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }


  println(john.toHTML)
  /*
  the above gets expanded to:
  new HTMLEnrichement[User](john : User).toHTML(UserSerializer)

  with this cool mechanic we can extend the functionality to the new types;
  and choose the implementation; this solution is nicely expressive
   */

  println(2.toHTML)

  /// ------------------------------------- CONTEXT BOUNDS
  def htmlBoilerPlate[T](content:T)(implicit serializer : HTMLSerializer[T]): String  = {
    s"<html><body> ${content.toHTML(serializer)}</body></html>"
  }

  def htmlSugar[T : HTMLSerializer](content: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    // here use the API of the serializer as you want, and pass it explicitly
    s"<html><body> ${content.toHTML(serializer)}</body></html>"
  }


  // ------------ implicitly
  case class Permissions(mask:String)

  implicit val defaultPermissions: Permissions = Permissions("0744")

  // ... ... ...
  // in some other part of the code, we want to surface out what is the implicit value for permissions
  val standardPerms = implicitly[Permissions] // surfacing out


}


