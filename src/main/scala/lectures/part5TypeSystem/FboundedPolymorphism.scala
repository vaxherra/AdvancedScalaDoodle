package lectures.part5TypeSystem

object FboundedPolymorphism extends App{
  // F-BOUNDED POLYMORPHISMS: recursive Types and Auto-bounded Types

  // the design problem:
  // in which we'd like the compilers to FORCE the return type of breed for subclasses


// the simple case - solution #1: naive
  trait Animal1{
    def breed: List[Animal1]
  }

  class Cat1 extends Animal1 {
    override def breed: List[Cat1] = ??? // we'd like to return list of cats
  }

  class Dog1 extends Animal1{
    override def breed: List[Dog1] = ???
  }

  // the above works just fine (as list is COVARIANT), however we PERSONALLY NEED TO TRACK the return types, there
  // is not auto-inference of the types; and the COMPILER DOES NOT PREVENT YOU FROM MAKING MISTAKES


  // ---------------------------------------------------------------------------------------------------
  // SOLUTION #2

  // F - B O U N D E D     P O L Y M O R P H I S M
  // this solution has also some limitations: one can make mistakes in return types...
  trait Animal2[A <: Animal2[A]] { // Animal appears in its own type signature! A RECURSIVE TYPE:
    def breed: List[Animal2[A]]
  }

  class Cat2 extends Animal2[Cat2]{
    override def breed: List[Animal2[Cat2]] = ??? // forcing the return Type
  }

  class Dog2 extends Animal2[Dog2]{
    override def breed: List[Animal2[Dog2]] = ???
  }

  // often present in Database APIs (ORMs)
  // often used for comparisons as well
  /*
  class Person extends Comparable[Person]{ // comparable if Java entity
      override def compareTo(p:Person):Int = ???
  }
   */

  // the problematic case:
  class Crocodile2 extends Animal2[Dog2]{
    override def breed: List[Animal2[Dog2]] = ??? // crocodile extends dog?!
  }

  // how to make the compiler for the class you're defining and the TYPE you're annotating with are the same?

  // ---------------------------------------------------------------------------------------------------
  // SOLUTION #3 - F-bounded polymorphisms in conjunction with SELF TYPES!


  trait Animal3[A <: Animal3[A]] { self : A =>
    // Animal appears in its own type signature! A RECURSIVE TYPE:
    def breed: List[Animal3[A]]
  }
  // whatever descendants of Animal3 I may implement, they must also be an of ITS OWN TYPE [A]

  class Cat3 extends Animal3[Cat3]{
    override def breed: List[Animal3[Cat3]] = ??? // forcing the return Type
  }

  class Dog3 extends Animal3[Dog3]{
    override def breed: List[Animal3[Dog3]] = ???
  }

/*
  class Crocodile3 extends Animal3[Dog3]{
    // throws an error!: illigal inheritence: self-type crocodile3 does not conform to Animal3[Dog3] self type
    override def breed: List[Animal3[Dog3]] = ???
  }
*/
  // solution #3 limitations:
  /*

   */

  trait Fish3 extends Animal3[Fish3]{  // this conforms to the F-bounder polymorphism + Self type constraint
    override def breed: List[Animal3[Fish3]] = ???
  }

  class Shark3 extends Fish3 {
    override def breed: List[Animal3[Fish3]] = ??? // the auto-completed type signature!
    // it still returns not the type we want... list of Animal[Fish] not of Animal[Shark] we want
  }

  class Cod3 extends Fish3{
    override def breed: List[Animal3[Fish3]] = List(new Shark3) // this is (wrongly so) OK by the compiler standards
  }
  // but for our use case, we dont want Cods breeding sharks...
  // so once we bring our class hierarchy one level, then F-bounded polymorphisms stops being effective....


  //----------------------------------------------------------------------------------------------------
  // SOLUTION 4: strictly enforcing the types WITHOUT F-BOUNDED POLYMORPHISMS AND SELF TYPES;
  // TYPE CLASSES
/*

  trait Animal4

  trait CanBreed[A]{
    def breed(a: A): List[A] // enforce the "breeding" return type
    // defining type class methods
  }


  class Dog4 extends Animal4

  object Dog4 {
    implicit object DogsCanBreed extends CanBreed[Dog4]{
      override def breed(a: Dog4): List[Dog4] = List() // dog must breed to a list of dog;
    }
  }

  implicit class CanBreedEnrichment[A](animal: A){
    // defining type class instances as implicit values
    def breed(implicit canDo: CanBreed[A]): List[A] = {
      canDo.breed(animal)
    }
  }

  val dog4 = new Dog4
  dog4.breed // we are SURE IT WILL RETURN A LIST OF DOGS !!!
  /*
  this involves the following

  new CanBreedEnrichment[Dog4](do4).breed(Dog4.DogsCanBreed)
   */
  // solution 4 : TESTING, ensuring proper types

  class Cat4 extends Animal4

  // this seemingly compiles, however ...
  object Cat4 {
    implicit object CatsCanBreed extends CanBreed[Dog4] {
      override def breed(a: Dog4): List[Dog4] = List()
    }
  }

  // ... not until I try to "breed cats"
  val aCat = new Cat4
  //aCat.breed // throws an error:
  /*
  could not find implicit value for parameter 'canDo'.

  compiler tries to find an implicit candidate to pass in to 'canDo' method, and it cannot find an implicit can breed
  cat instance! So if we make mistakes, the compiler catches the error before we run the code!

   */


*/

  // --------------------------------------------------------------------------------------------------------
  // solution #5: trait 'Animal' being the type class itself
  // WARNING : comment out solution 4 with implicits, as otherwise it will not work! (too many impicits) using the
  // 'breed' method
  trait Animal5[A]{ // pure type class, keeping the API in the concept we want to represent;
    def breed(a:A): List[A]
  }

  class Dog5 // no need to extend here anything
  object Dog5{ // a companion object for the above class
    implicit object aDogIsAnAnimal extends Animal5[Dog5]{
      override def breed(a: Dog5): List[Dog5] = List()
    }
  }

  // and some conversion class
  implicit class AnimalEnrichement[A](animal : A){
    def breed(implicit animalTypeClassInstance : Animal5[A]): List[A] = animalTypeClassInstance.breed(animal)
  }

  val aDog5 = new Dog5
  aDog5.breed


  // the below class and object will compile, however, ....
  class Cat5 // no need to extend here anything
  object Cat5{ // a companion object for the above class
    implicit object aDogIsAnAnimal extends Animal5[Dog5]{
      override def breed(a: Dog5): List[Dog5] = List()
    }
  }

  // you cannot "breed cat and get dogs" (as the above signature defines by "controlled accident")
  val aCat5 = new Cat5
  //aCat5.breed // "could not find implicit value for parameter animalTypeClassInstance<...> aCat5.breed
  //----------------------------------------------------------------------------------------------------


}
