package lectures.part2_FPadvanced

object monads extends App{

 // PROVING MONADS LAWS

  // 01. LEFT-IDENTITY
  // unit.flatMap(f) = f(x), only for success cases holds
  // Success(x).flatMap(f) = f(x)

  // 02. RIGHT IDENTITY
  // attempt.flatMap(unit) = attempt
  // if we deal with sucess Succes(x).flatmap(x=>Attempt(x)) = Attempt(x) = Sucess(x)
  // Fail(e).flatMap(...) = Fail(e)

  // 03. ASSOCIATIVITY
  // def: attempt.flatMap(f).flatMap(g) = attempt.flatMap(x => flatMap(g))
  // Faile(e).flatMap(f).flatMap(g) = Faile(e) == Fail(e).flatMap(x=> f(x).flatMap(g) ) == Fail(e)

  // Success(v).flatMap(f).flatMap(g) = Fail(e) OR f(v).flatMap(g)
  // Success(v).flatMap ( x => f(x).flatMap(g) ) = ..
  // ... f(v).flatMap(g) or Faile(e)
  // Left hand side == Right Hand Sice (LHS=RHS)


  val myAttemp = Attempt {
    throw new RuntimeException("MY own monad!")
  }
  println(myAttemp) // Fail as expected
}

trait Attempt[+A] {// a self-implemented Try-like monad

  def flatMap[B](f: A=>Attempt[B]): Attempt[B]


}

object Attempt {
  def apply[A](a: => A): Attempt[A] = {
    try {
      Success(a)
    } catch {
      case e: Throwable => Fail(e)
    }
  } // call by name parameter a =>
}


case class Success[+A](value : A) extends Attempt[A] {
  override def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
    try {
      f(value) // returns an Attempt of type B, so no need to wrap it up in a Success and catch it that way,
      // but it still might throw errors
    } catch {
      case e : Throwable => Fail(e)
    }
  }
}

case class Fail(e: Throwable) extends Attempt[Nothing] {
  override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this

}