package exercises.part1n2

//-------------------------------------------------
// ---  Exercises Scope ---
//-------------------------------------------------

/* exercise 1.
unit/apply
flatMap
 */


/* exercise 2.

Monads = unit + flatMap == unit + map + flatten

  Monad[T} {
  def flatMap[B](f: T=>Monad[B]): Monad[B] = ... (already given and implemented)

  def map[B](f: T => B): Monad[B] = ???
  def flatten(m: Monad[Monad[T]]): Monad[T] = ???
}

have a list in mind;

 */


object lazyMonad extends App {
    // lazy monad will abstract a calculation that is executed only when it is needed
  // ---------
  // EXERCISE 1
  // ---------

  val lazyInstance = Lazy {
   // companions object apply method
   println("Today is a lazy day")
    42
  } // won't get printed, as it is lazily evaluated expression

  // it will get printed here, when actually called.
  println(lazyInstance.use)


  // flatmap
  val flatMappedIstance = lazyInstance.flatMap(x=> Lazy{x*10} ) // evaluates, and WONT call the print second time

  val flatMappedIstance2 = lazyInstance.flatMap(x=> Lazy{x*10} ) // evaluates, and WONT call the print second time

  println("Flatmapped instance #1 use")
  println(flatMappedIstance.use)
  println("Flatmapped instance #2 use")
  println(flatMappedIstance2.use)


  // provoing MONADIC LAWS

  // 1. LEFT IDENTITY
  // in theory: unit.flatMap(v) = f(v), and in our case it holds, as:
  // Lazy(v).flatMap(f) = f(v)

  /*
  2. RIGHT IDENTITY
  IN THEORY:          l.flatMap(unit) = l
  IN OUR EXAMPLE:   Lazy(v).flatMap(x=>Lazy(x)) = Lazy(v)
   */

  /*
  3. ASSOCIATIVITY
  IN THEORY: l.flatMap(f).flatMap(g) = l.flatMap(x=> f(x).flatMap(g) )
  IN PRACTICE: Lazy(v).flatMap(f).flatMap(g) = f(v).flatMap(g) = LHS
        RHS = Lazy(v).flatMap(x => f(x).flatMap(g) )  = f(v).flatMap(g) ) = RHS
        LHS = RHS

        associativity is maintained
   */

  // ---------
  // EXERCISE 2
  // ---------

}

//    LAZY MONAD, bear minimum
class Lazy[+A](value: => A) { // value provided by NAME
  private lazy val priviet = value
  def use: A = priviet
  def flatMap[B](f: (=> A) => Lazy[B]): Lazy[B] = f(priviet)
  // f is of type function, from BY-NAME A (to =>) Lazy[B]
}

object Lazy {
  def apply[A](value: => A ): Lazy[A] = new Lazy(value) // monadic UNIT: as a companion apply method
}


// ex:2

/*
Monad[T] {
    def flatMap[B](f: T => Monad[B]): Monad[B] = ( ... already implemented )

    def map[B](f : T => B): Monad[B] =  flatMap(x => unit( f(x) )  )

    def flatten( m : Monad[Monad[T]] ): Monad[T] = m.flatMap( (x:Monad[T]) => x )


    PROVES:
    1) map:
      List(1,2,3).map(_*2) = List(1,2,3).flatMap( x => List(x*2) )

    2) flatten:
      List( List(1,2), List(3,4) ).flatten    =       List( List(1,2), List(3,4) ).flatMap( (x:List) => x )
}
 */