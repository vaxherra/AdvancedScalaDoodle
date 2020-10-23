package lectures.part4implicits


object PimpingALibrary extends App{
  // enrichements allow us to decorate existing classes (that we might not have access to)
  // with additional methods and properties.

  // implicit classess take ONE and ONLY ONE argument
  // for memory optimization purposes this implicit class often extends AnyVal
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)
  }

  // TYPE ENRICHEMENT, "PIMPING"
  println(42.isEven)

  //compiler doesn't do multiple implicit searches
  /*
  so defining
  implicit class RicherInt(richInt:RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }
  42.isOdd // WOULD NOT WORK!
  // the compiler doesn't expand the search to enrich the "42' in this example to richerInt
   */
}
