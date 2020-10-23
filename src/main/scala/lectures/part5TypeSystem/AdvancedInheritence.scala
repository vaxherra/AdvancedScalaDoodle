package lectures.part5TypeSystem

object AdvancedInheritence extends App{
  //
  trait Writer[T]{
    def write(value:T):Unit
  }

  trait Closeable{
    def close(status:Int):Unit
  }

  trait GenericStream[T]{
    def foreach(f: T=> Unit):Unit
  }

  def processStream[T](stream: GenericStream[T] with Closeable with Writer[T]): Unit = {
    // the "stream" type is its own type with access to all API
    stream.foreach(println)
    stream.close(42)
  }


  // ==========================================================================================
  // diamond problem;
  trait Animal{
    def name:String
  }
  trait Tiger extends Animal {
    override def name: String = "Tiger"}
  trait Lion extends Animal {
    override def name: String = "Lion"}

  trait Mutant extends Lion with Tiger{
    //override def name: String = "Liger" // if not overwritten, the last "with"/override always gets picked
  }

  // ==========================================================================================
  // the super problem - type linearization;
  trait cold{
    def print = println("cold")
  }

  trait green extends cold{
    override def print: Unit = {
      println("green")
      super.print
    }
  }

  trait blue extends cold{
    override def print: Unit = {
      println("blue")
      super.print
    }
  }

  class Red{
    def print = println("red")
  }

  class White extends Red with green with blue {
    override def print: Unit = {
      println("white")
      super.print
    }
  }


  val myWhite = new White
  println("---")
  myWhite.print
  /*
  Type linearization;
  the white type is re-written by the compiler as:
>   AnyRef with <Red> with <Cold> with <green> with <blue> with <white>
        note that <...> is the body/methods of a type/trait/class

  and EACH SUPER.print call the print method backwards from the hierarchy defined above (line 76)

  so we should expect:
  white (first method call)
  blue (the "with blue" is the last expression"), and it also calls super which refers to...
  green, that then also calls super, which switcher to
  cold, and this doesn't call any super, so we stop,

  hence
  - white
  - blue
  - green
  - cold

  and no red
   */
}
