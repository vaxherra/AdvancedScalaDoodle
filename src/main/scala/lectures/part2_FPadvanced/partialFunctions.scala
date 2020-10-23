package lectures.part2_FPadvanced

object partialFunctions extends App{
  //--------------------------------------------------
  println("01. Partial function definiton ")
  // PF (Partial Functions) are defined only on a subset of argument types
  val aPartialFunction : PartialFunction[Int,String] = {
    case 1 => "one"
    case 2 => "two"
    case 3 => "three"
      // not an EXHAUSTIVE match, as we only partially define it
  }

  println(aPartialFunction(1))
  println(aPartialFunction(2))
  println(aPartialFunction(3))
  //println(aPartialFunction(4)) // match error


  //--------------------------------------------------
  println("\n 02. Partial Function utilities:")

  println(s"Is aPartialFunction defined at 4? ${aPartialFunction.isDefinedAt(4)}")

  // lifted
  println("Partial Functions (PFs) can be lifted to a function returning Options")
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(s"Lifted for 3 and 4: ${lifted(3)} ${lifted(4)}")

  // composing partial functions
  val pdChained = aPartialFunction.orElse[Int,String]{
    case 54 => "fifty-four my brother"
    case _ => "Not defined" }
  println(s"Chained or else, result:  ${pdChained(54)}")
  println(s"Chained or else, result:  ${pdChained(94)}")

  //--------------------------------------------------
  println("\n A partial function extends normal functions")
  val aTotalfunction : Int => Int = {
    case 1 => 99
  }
  // HOFs accept partial functions as well

  val aMappedList = List(1,2,3).map {
    case 1=>42
    case 2=> 55
    case 3=> 1000
    //case _ => 0 // to make it full, not partial
  }
  println(aMappedList)
  //--------------------------------------------------
  println("\n Partial function can only have 1 parameter TYPE")

  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
  //--------------------------------------------------
}
