package exercises.part1n2

object AdvancedPM extends App {

  /*
  Create own pattern match for matching INTEGERS, matching against multiple conditions

   */
  // create a singleton for each condition

  // alternatively we could return Boolean without the option and true/false
  // and this would be matched against case singleDigit(), case even()
  object even {
    def unapply(arg: Int): Option[Boolean] = if (arg % 2 == 0) Some(true) else None
  }

  object singleDigit {
    def unapply(arg: Int): Option[Boolean] = if (arg > -10 && arg < 10) Some(true) else None
  }

  val n: Int = 4

  val matchProperty = n match {
    case singleDigit(x) => s"$n is single digit"
    case even(x) => s"$n is even"
    case x => s"I just got $x that doesn't match nothing"
  }
  println(matchProperty)

}
