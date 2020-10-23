package lectures.part5TypeSystem

object TypeMembers extends App{
  class Animal
  class Dog extends Animal
  class Cat extends Animal

  // declaring types
  // bounding types
  // and creating type aliases
  // and using .type method of an instance

  class AnimalCollection {
     // type members; to help compilers do the inference for us
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal  // upper bounding
    type SuperBoundedAnimal >: Dog <: Animal

    // type aliases
    type AnimalC = Cat // another name for existing type, a 'Cat Alias'

    val animalCollection = new AnimalCollection
    val dog: animalCollection.AnimalType = ???

    ///val deadCat : animalCollection.BoundedAnimal = new Cat // does not compile, as the 'BoundedAnimal'  is undefined
    val pup : animalCollection.SuperBoundedAnimal = new Dog // works ok, as Dog is of Dog type (superbounded)
    val cat : animalCollection.AnimalC = new Cat // the type is an alias for a Cat type, so it works

    // type aliases are used frequently with NAME COLLISIONS with a lot of packages;

    // type members: an alternative to generics approach;
    trait MyList{
      type T
      def add(element : T): MyList
    }
    class MyNonEmptyList(value:Int) extends MyList {
      override type T = Int  // VALID OVERRRIDING OF A TYPE MEMBER; types is provided explicitly;
      def add(elem:Int): MyList = ???
    }

    // values types as TYPE alias
    // the val cat copied from above (and commented out)
    //val cat : animalCollection.AnimalC = new Cat // the type is an alias for a Cat type, so it works
    type CatsType = cat.type // defines a new type alias
    val aNewCat : CatsType = cat

    // the compiler cannt really find if this type is constructable or not, and hence does not let to compile
    // new CatsType // thorws an error: class type required but <...>.type found (...)

  }




}
