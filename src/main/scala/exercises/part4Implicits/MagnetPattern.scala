package exercises.part4Implicits

object MagnetPattern extends App{

  // the problem: call by name does not work properly

  class Handler{
    def handle(s: => String) = {
      // calling unit func TWO TIMES
      println(s)
      println(s)
    }
    // other overloads here
  }

  trait HandleMagnet{
    def apply():Unit
  }

  def handle(magnet:HandleMagnet) = magnet()


  implicit class StringHandle(s: => String) extends HandleMagnet{
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod():String = {
    println("A problematic case...")
    "Ugh, the headache!"
  }

  handle(sideEffectMethod())

  println("----")
  handle{
    println("Some other side effect")
    "The headache grows!" //only this value is converted to a magnet class not the above
  }
  // we have to be careful with methods doing side effects; (like logging...) and is hard to trace!

}
