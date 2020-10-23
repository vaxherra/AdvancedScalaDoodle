package exercises.part1n2

import scala.util.Random

object CurriesAndPartialFunctions extends App {
  /*
  EXERCISES #1
   */
  val simpleAddFunc = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  /*
  Define an add7 function value of type Int => Int
  for above 3 versions
   */

  val add7_1 = curriedAddMethod(7) _ // PAF:
  // Partially Applied Function

  val add7_2 = (x: Int) => simpleAddMethod(x, 7)

  val add7_3 = (x: Int) => simpleAddFunc(7, x)

  //--------------------------------------------------------------------------------------------------------------------
  // ".curried" converts a function to a curried version
  val add7_4 = simpleAddFunc.curried(7)

  val add7_5 = curriedAddMethod(7)(_)

  val add7_6 = (x: Int) => simpleAddMethod(x, _: Int) // simpleAddFunc(x,_:Int) works equivalently
  // turning methods into function values

  //--------------------------------------------------

  def concatenator(a: String, b: String, c: String): String = a + b + c

  val insertName = concatenator("Hello, I am ", _: String, ". Nice to meet ya!")
  println(insertName("Robert"))

  //--------------------------------------------------
  /*
  Exercises #2

  1. process a list of numbers, return their string reprs with different formats
    %4.2f %8.6f %14.12f
   */
  val r = Random
  val aListFloat: List[Float] = List.range(1, 10) map (x => r.nextFloat())
  println(aListFloat)

  def process(formatter: String)(x: List[Float]): List[String] = {
    x map (elem => formatter.format(elem))
  }

  println(s" %4.2f formatting: \n\t ${process("%4.2f")(aListFloat)}        ")
  println(s" %8.6f formatting: \n\t ${process("%8.6f")(aListFloat)}          ")
  println(s" %14.12f formatting: \n\t ${process("%14.12f")(aListFloat)}    ")

  val fourPointTwoformatter = process("%4.2f")(_: List[Float])
  val eightPointSixformatter = process("%8.6f")(_: List[Float])
  val fourteenPointTwelveformatter = process("%14.12f")(_: List[Float])

  println(s"%4.2f formatter used: \n\t ${fourPointTwoformatter(aListFloat)}")

  /*
  2nd exercise

  difference between:
  a. functions vs. methods
  b. parameters by name, vs. 0-lambda!

  define two small methods:

   */

  def byName(n: => Int): Int = n + 1

  def byFunction(f: () => Int): Int = f() + 1

  def mthd: Int = 42 // no parameters, cannot be passed to HOFs
  // compiler for the above does not do ETA expansion
  def parenthesisMethod(): Int = 42

  // calling by name and by functions
  /*
  -int
  - mthd
  - parenthesisMethod
  - lambda
  - Partially applied functions
   */


  println(byName(23)) // ok
  println(byName(mthd)) // ok

  println(byName(parenthesisMethod())) // ok


  println(byName(parenthesisMethod)) // ok

  println(
    byName((() => 5) ())
  ) // define a lambda ( ()=>5) and then call it with (), this magic works

  println(byFunction(() => 42))

  // byFunction(42) // error
  // byFunction(mthd) // not ok, a parameterless method
  println(
    byFunction(parenthesisMethod) //ok
  )
}
