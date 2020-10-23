package exercises.part4Implicits

import java.{util => ju}

object ScalaJavaConversions extends App{
  /*
  quick scala to java conversion with Option type
   */

  class MyToScala[T](value : => T){
    def myAsScala:T = value
  }

  implicit def myAsScalaOption[T](o:ju.Optional[T]): MyToScala[Option[T]] = {
    new MyToScala[Option[T]]({
      if(o.isPresent) Some(o.get)
      else None
    })
  }

  val juOptional : ju.Optional[Int] = ju.Optional.of(42)
  val scalaOption = juOptional.myAsScala

  println(juOptional) // Optional[42]
  println(scalaOption) // Some(42)

}
