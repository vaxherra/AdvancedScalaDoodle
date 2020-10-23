package lectures.part5TypeSystem

object SelfTypes extends App{
  // SELF TYPES - a way of requiring a type to be MIXED IN

  // example - API for a music band, where every singer MUST know how to play an instrument;

  trait Instrumentalist{
    def play():Unit
  }

  trait Singer{ self: Instrumentalist => // SELF TYPE is a marker, telling woever implements a singer. to implement instrumentalists as we;;
  // this is not a lambda, but a SCALA feature'
    // rest of the API goes here ....
    def sing():Unit
  }


  // whenever we exten a singer, we have to extend instrumentalist as well
  class LeadSinger extends Singer with Instrumentalist {
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

/*  class Vocalist extends Singer{
    override def sing(): Unit = ???
    def play():Unit
  }*/
  /*
  the above would raise illegal inheritance; self type does not conform to singers self type
   */

  val jamesDoodle = new Singer with Instrumentalist{
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  class Guitarist extends Instrumentalist{
    override def play(): Unit = println("playing a guitar")
  }

  var EricaCalpton = new Guitarist with Singer {
    override def sing(): Unit = ???
    override def play(): Unit = super.play()
  }

  /*
  more intuition: SELF TYPES VS INHERITANCE

  class A
  class B extends A
  -> here B is an A, just more "specific"

  trait A
  trait B { self:A => <some functions> }
  -> this says that B REQUIRES A, but B is NOT A!
   */

  // ----------------------------------------------------------------------------------------------------
  // self types are used in CAKE PATTERN (scala equivalent to DEPENDENCY INJECTION)


  // first look at CLASSICAL DEPENDENCY INJECTION
  class Component {
    // some API
  }

  class ComponentA extends Component
  class ComponentB extends Component
  class ComponentC extends Component
  // and so on...

  class DependentComponent( val cmpt: Component)

  // ---------------
  // in scala there is the CAKE PATTERN

  trait ScalaComponent {
    // some API
    def action(x:Int): String
  }

  // layer 1 - small components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent
  //...

  trait ScalaDependentComponent { self : ScalaComponent =>
    def dependentAction(x:Int): String = action(x)+" is dependent..."
    // calling action method from dependent component AS IF IT WERE MY OWN METHOD
    // compiler know in advance
  }

  // layer 2 - compose components
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  // layer 3
  trait ScalaApplication { self : ScalaDependentComponent=>
  //...
  }

  trait AnalyticsApp extends ScalaApplication with Analytics
  //... and so on... baking the application with layers

  /*
  the Dependency Injection is checked at RUNTIME (as in standard java)

  the Cake Pattern is checked at COMPILE TIME! Huge, important difference
   */

  //---------------------------------------------------------------------------------------------------
  // cyclical dependencies

  /* a cyclical dependency that would not compile
  class X extends Y
  class Y extends X

  // throws an error: illegal cyclic reference involving class X,
*/

  // but with SELF TYPES it is possible
  trait X { self : Y => }
  trait Y { self : X => }

  // however, this cyclic dependency is only APPARENT, as we're saying whoever implements X must also implement Y,
  // and whoever implements Y must lso implement X, so no true cycles, NO CONTRADICTION




}
