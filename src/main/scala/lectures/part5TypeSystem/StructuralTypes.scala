package lectures.part5TypeSystem

import javax.sound.sampled.SourceDataLine

object StructuralTypes extends App{
  // Structural Types: Type Refinements, and Compile-time duct typing


  //-----------------------------------------------------------------------------------------------------------
  // Structural types
  /*
  Hypothetical situation: a huge team, somebody wrote a code with java + google guava

  Java has this io Closeable. But let's say some team also implemented a different closeable
   */
  println("------ Simple types")
  type JavaClosable = java.io.Closeable

  class aDifferentCloseable{
    def close():Unit = println("Closing a custom closeable")
    def closeSilently():Unit = println("aDifferentCloseable: close silently call")
  }

  // we want to write a method that both accepts java closeable and 'aDifferentCloseable' without duplicatig the code


  // def closeQuitely(closeable: JavaClosable OR aDifferentCloseable) // not a valid scala code

  type UnifiedCloseable = {
    def close(): Unit  // a STRUCTURAL TYPE
  }
  def closeQuietly(uc : UnifiedCloseable): Unit = uc.close()


  closeQuietly(new JavaClosable{
    override def close(): Unit = println("Closing java closeable")
  })

  closeQuietly(new aDifferentCloseable)

  //-----------------------------------------------------------------------------------------------------------
  // TYPE REFINEMENTS
  println("----- Type refinements")

  // refining a type
  type AdvancedCloseable = JavaClosable { // a type enrichement, as JavaCloseable is a type defined above (l:14)
      def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaClosable {
    override def close(): Unit = println("Advanced Java Closeable closes")
    def closeSilently(): Unit = println("Advanced Java Closeable: method call closeSilently")
  }

  def closeShh(advclsb : AdvancedCloseable): Unit = advclsb.closeSilently()

  closeShh(new AdvancedJavaCloseable) // compiler performs type checking here, and says OK!

  /*
  closeShh(new aDifferentCloseable)

  the above wont compile: type mismatch, although aDifferentCloseable has ALL THE METHODS implemented by the type,
  however it does not share any common Types!
   */

  //-----------------------------------------------------------------------------------------------------------
  // USING STRUCTURAL TYPES ARE THEIR OWN TYPES; (standalone types)

  def alternativeClose(clsbld: {def close():Unit}  ) : Unit = clsbld.close()
  // the code in the curly braces is its own type;

  //-----------------------------------------------------------------------------------------------------------
  // structural types in the context of compiler type checking;
  // the DUCK-TYPING MECHANISM (python reminiscence cringe..... ugh)

    type SoundMaker = {
      def makeSound():Unit
    }

  class Dog {
    def makeSound(): Unit = println("Bark!")
  }

  class Car {
    def makeSound(): Unit = println("I am in my mom's car! vroom! vroom!")
  }


  // type on the RHS conform to the types defined on the LHS by the type
  // STATIC DUCK-TYPING
  val dog : SoundMaker = new Dog
  val car : SoundMaker = new Car
  // scala performs the duck tests at COMPILE-TIME!

  // one CAVEAT: they are based on reflection (performance hit!) // use only when needed!


  //-----------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------
}
