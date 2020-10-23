package exercises.part4Implicits

object OrganizingImplicits extends App{
  // EXERCISES FOR IMPLICITS

  /*
  01. implement implicit ordering for class person
   */
  println("EXERCISE #1")
  case class Person(name:String, age:Int)

  implicit val peopleOrdering : Ordering[Person] = Ordering.fromLessThan( (x,y) => x.name.compareTo(y.name)<0)
  //implicit val peopleOrdering : Ordering[Person] = Ordering.fromLessThan( (x,y) => x.name < y.name)

  val people = List(
  Person("Robert Kwapich",29),
  Person("John Dough", 35),
  Person("Mariah Theressay", 55)
  )

  println(people.sorted)

  // --------------------------------------------------
  println("EXERCISE #2")
  /*
  Ordering purchases in a store

  add 3 ordering by 3 different criteria
  1. total price (most used - 50%)
  2. by unit count, sometime used for analytics 25%
  3. by unit price, 25%

  organize them in their proper place
   */
  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase{
    implicit val totalPriceOrdering : Ordering[Purchase] = Ordering.fromLessThan((p1,p2) =>  p1.nUnits*p1.unitPrice < p2.nUnits*p2.unitPrice  )
  }

  object UnitCountSorting {
    implicit val unitCountSorting: Ordering[Purchase] = Ordering.fromLessThan( _.nUnits < _.nUnits )
  }

  object UnitPriceSorting{
    implicit val unitPriceSorting: Ordering[Purchase] = Ordering.fromLessThan( _.unitPrice < _.unitPrice )
  }

  val purchases = List(
    Purchase(1,15),
    Purchase(15,5),
    Purchase(25,5),
    Purchase(5,5),
    Purchase(15,6),
    Purchase(15,1),
    Purchase(9,2),
    Purchase(2,1),
    Purchase(3,125),
    Purchase(2,14),
    Purchase(6,55),
    Purchase(7,13),
  )

  // import UnitCountSorting._ // it uncommented, then the companion object implicit is used by default
  println(s"Purchases $purchases")
  println(s"Purchases ordered: ${purchases.sorted}")


}
