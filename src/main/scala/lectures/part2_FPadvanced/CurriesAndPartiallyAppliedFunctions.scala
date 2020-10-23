package lectures.part2_FPadvanced

object CurriesAndPartiallyAppliedFunctions extends App{
  // curried functions recap
  val superAdder: Int => Int => Int = { // a curried function
    x=>y=> x+y
  }
  val add5 = superAdder(5)
  println(s"5+4=${add5(4)}")

  //--------------------------------------------------
  // curried method
  def curriedAdder(x:Int)(y:Int): Int = x+y
  val add4: Int=>Int  = curriedAdder(4) // we need to specify types
  println(s"4+9 = ${add4(9)}")

  //behind the scence, the add4 is LIFTED
  // a.k.a "ETA-EXPANSION"
  // creating FUNCTIONS out of methods
    // FUNCTIONS != METHODS         (!!!)

  // alternatively without the types, but we have to specify the ETA-expansion

  val add4ETA = curriedAdder(4) _ // with the underscore
  println(s"4+9 = ${add4ETA(9)}")
}
