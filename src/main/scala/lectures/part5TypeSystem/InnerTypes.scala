package lectures.part5TypeSystem

object InnerTypes extends App{
  // Inner Types & Path Dependent Types;

  class Outer{
    class Inner
    object InnerObject
    type InnerType

    def print(i:Inner) = println(i)
    def printGeneral(i : Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass // we can define classes almost anywhere, with the exception of types;
    type HelperType = String
    2
  }

  // using the types nested inside classes -> are defined per instance

  val outerInstance1 = new Outer
  val outerInstance2 = new Outer
  // val inner = new Inner // 'Inner' does not exist within this scope;
  // val inner = new Outer.inner // also is not allowed, because it is defined per instance;
  val inner = new outerInstance1.Inner

  val otherInner : outerInstance2.Inner = new outerInstance2.Inner
  // val otherInne : outerInstance2.Inner = new outerInstance1.Inner // self-explantory: it not allowed!

  // and this is possible:
  outerInstance1.print(inner)
  // outerInstance1.print(otherInner) // and intuitively this is wrong, and compiler complains (of course)

  // this is PATH DEPENDENT TYPEs


  //----------------------------------------------------------------------------------------------------
  // all the inner types, 'inner' and 'otherinner' have a common super-type: Outer#Inner
  println("Outher#Inner types")
  outerInstance1.printGeneral(inner)
  outerInstance1.printGeneral(otherInner)

  //----------------------------------------------------------------------------------------------------


}
