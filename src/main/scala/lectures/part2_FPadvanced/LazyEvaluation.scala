package lectures.part2_FPadvanced

object LazyEvaluation extends App{
  // lazy eval
  lazy val test : Int = throw new RuntimeException


  // CALL BY NEED: evaluate the parameters once, only when you need it;

  def byNameMethod(x : => Int): Int = { // call by NAME
    lazy val t: Int = x // evaluate only once
    t + t + t + 3 // i,e 3*x + 3, but t is evaluated only once
  }

  def retriveMagic: Int = {
    println("waiting")
    Thread.sleep(100) // sleep 1s
    3
  }
  println(byNameMethod(retriveMagic))

 //--------------------------------------------------//
  // LAZY FILTERING
  def lessThan30(i:Int):Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }
  def greaterThan20(i:Int):Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  println("---------------- NON LAZY EVALS ---------------")
  val numbers = List(1,35,40,25,5,23)

  val lt30 = numbers.filter(lessThan30)
  println(lt30)
  println("--- \n\t")
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  println("--- \n\t")

  val lt30lazy = numbers.withFilter(lessThan30)
  val gt20lazy = lt30lazy.withFilter(greaterThan20)

  println("---------------- LAZY EVALS ---------------")
  //println(gt20lazy) // does not return values!
  gt20lazy.foreach(println)
  // for comprehensions use WITH FILTER with guards conditions!

  //--------------------------------------------------//

}

