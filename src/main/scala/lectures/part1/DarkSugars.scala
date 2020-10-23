package lectures.part1


object DarkSugars extends App{
  // 01. Method with single parameters

  def method1(x:Int): String = s"$x little fucks"

  val m1call = method1{
    // some code ...
    42 // call to method1 with 1 argument
  }
  println(s"01. Method with one argument")
  println(m1call)

  // 01. examples:

  val aTryInstance = try{ // try is a method with 1 argument
    throw new RuntimeException
  } catch {
    case e: Exception => "exeption caught"
  }
  println(aTryInstance)

  List(1,2,3,4,5).map {
      _+5
    }.foreach(print)

  //--------------------------------------------------
  println("02. Instances of traits with a single method can be reduced to lambdas ")

  trait Action {
    def act(x:Int): Int
  }
  val anActionInstance: Action = (x:Int) => x*10 // a lambda of a trait

  // this also works when only one method is abstract, and other are implemented
  abstract class AnAbstractType {
    def implemented: Int = 33
    def f(a:Int): Unit
  }

  // ad hoc lambda definition of the "f" method, while the "implemented" is already done
  val anInstanceOfAnAbstractType: AnAbstractType    = (a:Int) => println("Sweee, sweet syntax sugar!")


  //--------------------------------------------------
  println("03. the ::  and #:: methods")

  val prependedList: List[Int] = 2 :: List(3,4)
  val prependedList2: List[Int] =  List(3,4) :+ 5
  println(prependedList :: prependedList2)

  // last character defines the associativity of a method
  // "::" is an operator that is right associative, AS IT ENDS WITH A COLON! , I.E. ":"
  println(  1 :: 2 :: 3 :: List(4,5) == List(4,5).::(3).::(2).::(1) )


  case class myStream[T]() {
    def -->:(a:T): myStream[T] = this // no implementetion
  }
  val myStreames = 1-->: 2 -->: 3 -->: new myStream[Int]
  println(myStream)



  //--------------------------------------------------
  println("\n 04. Multi word method naming")

  class TeenGirl(name:String){
    def `and then said`(gossip:String) = println(s"$name said : $gossip ...")
  }

  val lily = new TeenGirl("Lilly")
  lily `and then said` "Scala is so Sweet"

  //--------------------------------------------------
  print("\n 05. Generics")

  class Composite[A,B]
  val cmposite : Composite[Int,String] =       new Composite[Int,String]
  // or
  val composite2: Int Composite String =       new Composite[Int,String]

  // very permisive syntax with infix types; often used for type-level programming;
  class ->>[A,B]
  val towards: Int ->> String =       new ->>[Int,String]

  //--------------------------------------------------
  print("\n 06. Update method (special method like apply)")

  val exArray = new Array[Int](3)
  exArray(2) = 7 // rewritten  exArray.update(2,7), and returns unit
  // when you want to implement a mutable container, consider using update special method

  //--------------------------------------------------
  println("\n 07. SETTERS")

  class Mutable {
    private var internalMember: Int = 0 // OOP encapsulation

    // A SPECIAL CASE EXISTS, if I define a getter an a setter like these below:
    def hiddenShit: Int = internalMember // the "getter"
    // setter is the getter name with the sufix "_="...
    def hiddenShit_=(x:Int): Unit = internalMember=x
  }

  // then we can create a mutable container:
  val aMutableContainer = new Mutable
  aMutableContainer.hiddenShit = 42 // rewritten as aMutableContainer.member_=(42)
  println(aMutableContainer.hiddenShit)
  //--------------------------------------------------
}
