package lectures.part5TypeSystem

object Variance extends App{

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
    class Kitty extends Cat
  class Croc extends Animal


  // -------------------------------------------------------------------------------------------------------------------
  // what is variance?
  /*
  It is the problem of "inheritence" (type substitution) of generics;
   */

  class Cage[T]
  // should a Cage[Cat] inherit from Cage[Animal]?:
  // yes -> covariance
  class CovariantCage[+T]
  val covariantCage : CovariantCage[Animal] = new CovariantCage[Cat]

  // no -> invariance
  class InvariantCage[T]
  // val invariantCage : InvariantCage[Animal] = new InvariantCage[Cat] // does not work
  val invariantCage : InvariantCage[Cat] = new InvariantCage[Cat]


  // contravariance
  class ContraVariantCage[-T]
  val contraVariantCage1 : ContraVariantCage[Cat] = new ContraVariantCage[Cat]
  val contraVariantCage2 : ContraVariantCage[Cat] = new ContraVariantCage[Animal]
  //val contraVariantCage3 : ContraVariantCage[Animal] = new ContraVariantCage[Cat] // does not work

  // -------------------------------------------------------------------------------------------------------------------

  class InvariantCage2[T](animal: T) // invariant
  // class InvariantCage2[T](val animal: T) // invariant, also works OK

  // COVARIANT POSITIONS
  class CovariantCage2[+T](val animal:T) // WE SAY THAT THE GENERIC TYPE OF VALS ARE IN COVARIANT POSITION

  class ContraVariantCage2[-T](animal :T) // compiles OK

  // class ContraVariantCage3[-T](val animal :T)
  /*
   this val would raise an error: "contravariant type T occurs in covariant position in type T of value animal
  class ContraVariantCage3[-T](val animal :T)

  otherwise something like this would be possible (but it does not make sense!):
  class ContraVariantCage[-T]
  val cage : ContraVariantCage[Cat] = new ContraVariantCage[Animal](new Crocodile)

  although the RHS (right-hand side) makes sense: a crocodile can be passed to an animal cage,
  the LFH (left-hand-side) does not make sense, as the resulting type is a Cat Cage.
  */


  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // similar logic applies to VARs
    // the ONLY POSSIBLE TYPE FOR A VARIABLE FIELD IS INVARIANT!!!

  //class CovariantCage3[+T](var animal:T) // raises an error!:
  /* the variable is in covariant position
  covariant type T occurs in contravariant position in type T of value animal_=
  class CovariantCage3[+T](var animal:T)

  VAR types are in contravariant position. Essentially we could change the var of Type T to its sub-types.

  example:
  val exampleCage :  CovariantCage3[Animal]  = new CovariantCage3[Cat](new Cat) // seems OK, as LHS is compatible with RHS,

  however the "new Cat" part is a var animal, and can be changed
  and we can put in another animal inside (by definition)

  exampleCage.animal = new Crocodile // however we specified above (lin 69) new CovariantCage3[Cat], a cage for a cat,

  not a crocodile. Hence the error occurs;
   */

  // class ContraVariantVariableCage[-T](var animal:T) // also would not compile
  // the variable is in covariant position

  class InvariantCage3[T](var animal : T) // ok

  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  trait CovariantCage4[+T]{ // classical covariant collection;
    //def addAnimal(animal:T) //METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITIONS !!!
  }

  /* motivating example why method arguments cannot be covariant for covariant type

  trait CovariantCage[+T]
  val someCage : CovariantCage[Animal]  = new CovariantCage[Dog]
  someCage.addAnimal(new Cat) // forbidden by compiler!
   */

  class ContraVariantCage4[-T]{
    def addAnimal(animal:T): Boolean = true // implementation not important,
    // compiler allows!
  }

  val someCage4 : ContraVariantCage4[Cat] = new ContraVariantCage4[Animal]
  //someCage4.addAnimal( new Dog)  // will rise of course type mismatch: found Dog, expected Cat
  someCage4.addAnimal(new Cat)
  someCage4.addAnimal(new Kitty) // kitty is a subtype of a Cat type

  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /*
  let's say we want to create a COVARIANT collection and then add elements to it....

  we'll use the hierarchy, such that all elements have a common type

  again: METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITIONS !!!
   */

  class myList[+A] {
    def add[B >: A](element:B): myList[B] = new myList[B] // widening the type!
  }

  val someMyList = new myList[Kitty] // some empty list of type myList[Kitty]
  val someMyList2 = someMyList.add(new Cat) // this changed the type to myList[Cat], essentially widening our ad-hoc list;

  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // METHOD RETURN TYPES;
  // METHOD RETURN TYPES ARE IN COVARIANT POSITION

  class PetShop[-T]{ // contravariant
    //def get(isItaPuppy:Boolean) : T // here a contravariant type T occurs in COVARIANT position T !
  /* otherwise we'd be able to write

  val catShop : PetShop[Animal] = new PetShop[Animal] {
    // here the def get retuns a Cat of type Animal
    def get(isITaPuppy:Boolean) : Animal = new Cat
  }

  // we'd be able to write as well

  val dogShop : PetShop[Dog]  = catShop
  // here a 'catShop' is a PetShop[Animal], so can be assigned to a dogShop of 'PetShop[Dog]' since dog is a subtype

  // BUT!
  dogShop.get(true) returns me a CAT! and it doesn't match the return type

  in other words: RETURNS TYPES ARE IN COVARIANT POSITION
   */

    // the solution

    def get[S <: T](isItaPuppy:Boolean, defaultanimal : S): S = defaultanimal // implementation is not important here
  }

  val shop : PetShop[Dog]  = new PetShop[Animal]
  // val someCat = shop.get(true, new Cat) // won't be allowed by the compiler, the error:
  /* as cat does not extend Dog (duh!)
  inferred type arguments [lectures.part5TypeSystem.Variance.Cat] do not conform to method get's type parameter bounds
   [S <: lectures.part5TypeSystem.Variance.Dog]
  val someCat = shop.get(true, new Cat) // won't be allowed by the compiler

   */

  class TerraNova extends Dog // some sub-type of dog (dog race)
  val bigFurry = shop.get(true,new TerraNova) // OK!

  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /*
  SUMMARY OF RULES:

  1. METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION
  2. RETURN TYPES ARE IN COVARIANT POSITION

  */

}
