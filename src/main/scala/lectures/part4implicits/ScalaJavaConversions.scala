package lectures.part4implicits

import java.{util => ju}
import scala.collection.mutable.ArrayBuffer

object ScalaJavaConversions extends App{
  //
  import collection.JavaConverters._ // imports among others 'asScala' method

  println("JAVA set and its conversion to Scala")
  val javaSet : ju.Set[Int] = new ju.HashSet[Int]()

  (1 to 6).foreach(javaSet.add)
  println(javaSet)

  //convert to scala set
  val scalaSet = javaSet.asScala
  println(scalaSet)

  //---------------------------------------------

  println("Buffers: scala and its java converted companion")
  val numbersBuffer = ArrayBuffer[Int](1,2,3) // a scala buffer
  val juNumbersBuffer = numbersBuffer.asJava
  println(numbersBuffer)
  println(juNumbersBuffer)

  println(numbersBuffer eq juNumbersBuffer.asScala) // true: shallow equality, reference equality

  //--------------------------------------------------
  // but NOT ALL conversions would give us equalities!
  println("immutable lists conversions")
  val numbers = List(1,2,3) //immutable
  val juNumbers = numbers.asJava //mutable

  println(juNumbersBuffer.asScala eq numbers) // false!
  println(juNumbersBuffer.asScala == numbers) // true, they are the same collection

  // juNumbers.add(7) // java list is mutable, however juNumbers is supposed to be immutable as converted from scala
  // the above will throw an exception: 'UnsupportedOperationException'



}
