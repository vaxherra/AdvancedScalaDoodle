package lectures.part4implicits

object OrganizingImplicits extends App{
  println("Organizing implicits")
  //--------------------------------------------------

  // "sorted" on the list takes an implicit parameter value
  println("01. List sorting with implicitss")
  val aList = List(4,5,12,1,52,2,33,12,-5)
  println(f"A list ${aList}")
  println(aList.sorted)

  implicit val reverseOrdering : Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(aList.sorted)

  /*
  Implicits can be:
  - values/variables
  - objects
  - accessor methods (defs with no "parentheses' (), but parameters in methods can )
   */

  // IMPLICIT SCOPE;
  // there are rules of priority
  // 1. (highest priority) NORMAL/LOCAL SCOPE - where we write our code
  // 2. (med priority) IMPORTED SCOPE
  // 3. (...) companion objects of all types involved in the method signature;

  /*
  BEST PRACTICES for IMPLICIT SCOPE:
  1. When to define an implicit val:
    # IF there is a single possible value for it
    # and you can edit the code for the type
    -> then DEFINE IMPLICIT VALUES IN THE OBJECT COMPANION

  2. IF THERE ARE MANY POSSIBLE values for an implicit
    # but for most of the time there you're using a single "GOOD" one
    # and can edit the code for the type
    -> then define the "GOOD" implicit in the COMPANION OBJECT

  3. IF THERE ARE MANY POSSIBLE values for an implicit
    # but each of them is likely to be used in different scenarios
    -> then define an OBJECT (not a companion) with "NAME_X"
       and then use "import NAME_X._" to import implicit into the scope you want

   */
}
