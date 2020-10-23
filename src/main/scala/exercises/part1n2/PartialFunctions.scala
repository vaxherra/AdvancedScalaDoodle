package exercises.part1n2

object PartialFunctions extends App {
  // well...
  //--------------------------------------------------//
  // 01. Construct a partial function with anonymous function

  val aS = new PartialFunction[Int, Int] {
    override def isDefinedAt(x: Int): Boolean = x > 0

    override def apply(v1: Int): Int = -v1
  }

  val asLifted = aS.lift
  println(s"Positive values only to negative: PartialFunction(12)=${aS(12)}")
  println(s"Positive values only to negative: PartialFunctionLifted(-12)=${asLifted(-12)}")

  val aSOrElse = aS.orElse[Int, Int] {
    case -666 => -666
    case _ => 0
  }

  println(s"Positive values only to negative, but a single case for -666 => -666 \n\t PartialFunctionOrElsed(-666)=${aSOrElse(-666)}")


  //--------------------------------------------------//
  // 02. Implement a small, dumb chatbot as a partial function
  //

  val chatbot: PartialFunction[String, String] = {
    case "hi" => "hello"
    case "hello" => "hello"
    case "goodbye" => "goodbye muggot!"
    case "call mom" => "I called cops"
    case _ => "I have nothing for ya"
  }
  //scala.io.Source.stdin.getLines().foreach(line => println(s"For your like: $line I have a response: \n\t ${chatbot(line)}"))
  scala.io.Source.stdin.getLines().map(x => s"I say: ${chatbot(x)}").foreach(println)
  // both solutions work
}
