package exercises.part1n2

import scala.annotation.tailrec

///     CREATING COLLECTION AS A FUNCTION!
// TRULY GOOD EXERCISE

trait MySet[A] extends (A => Boolean) {
  /*
 EXERCISE
  */
  override def apply(v1: A): Boolean = contains(v1) // as normal sets do

    // abstract methods
    // exercise 1
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A] // add element
  def ++(other: MySet[A]): MySet[A] // union of sets

  def map[B](f: A=> B): MySet[B]
  def flatMap[B](f: A=>MySet[B]): MySet[B]
  def filter(predicate: A=>Boolean): MySet[A]
  def foreach(f: A=> Unit): Unit

    // exercise 2
  def -(elem:A):MySet[A] //remove element
  def &(otherSet: MySet[A]): MySet[A] // intersection
  def --(otherSet:MySet[A]): MySet[A] // difference with another set

  // exercise 3
  def unary_! : MySet[A]
}


// exercise 2: implement:
/*
  Set specific operators:
  - removing an element
  - intersection with another set
  - difference with another set
 */

class emptySet[A] extends MySet[A] {

  override def contains(elem: A): Boolean = false
  override def +(elem: A): MySet[A] = new someSet[A](head=elem, tail=this)
  override def ++(other: MySet[A]): MySet[A] = other

  override def map[B](f: A => B): MySet[B] = new emptySet[B]
  override def flatMap[B](f: A => MySet[B]): MySet[B] = new  emptySet[B]
  override def filter(predicate: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = () // () is the unit

  override def -(elem: A): MySet[A] = this
  override def &(otherSet: MySet[A]): MySet[A] = this
  override def --(otherSet: MySet[A]): MySet[A] = this

  // all inclusive SET!
  override def unary_! : MySet[A] = new PropertyBasedSet[A](x=>true) // all inclusive set
}

/* In order to implement a unary negation method, we need to define some kind of opposite of empty set:
i.e. all inclusive set. However the below implementation might be problematic for functions like map, flatMap, filter
foreach, remove etc...
We need some kind of PROPERTY-BASED stuff, that evaluates A=>Boolean.

Therefore the below is commented out;

class AllInclusiveSet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = true
  override def +(elem: A): MySet[A] = this
  override def ++(other: MySet[A]): MySet[A] = this

  override def map[B](f: A => B): MySet[B] = ???
  override def flatMap[B](f: A => MySet[B]): MySet[B] = ???

  override def filter(predicate: A => Boolean): MySet[A] = ??? //property-based SET

  override def foreach(f: A => Unit): Unit = ???

  override def -(elem: A): MySet[A] = ???

  override def &(otherSet: MySet[A]): MySet[A] = filter(otherSet)

  override def --(otherSet: MySet[A]): MySet[A] = filter(!otherSet)

  override def unary_! : MySet[A] = new emptySet[A]
}
*/

// PROPERTY BASED SET
class PropertyBasedSet[A](property: A=> Boolean) extends MySet[A]{
  // describes all elements of type "A" that satisfy property (in math terms)
  // abstract methods
  // exercise 1
  def contains(elem: A): Boolean = property(elem)
  // adds element, adds an OR clause
  def +(elem: A): MySet[A]  = new PropertyBasedSet[A](x => property(x) || x==elem)
  def ++(other: MySet[A]): MySet[A]  = {
     // union of sets: either orignal property holds, or the other set contains it
     // the apply method is a shorthand for contains
    new PropertyBasedSet[A](x => property(x) || other(x) )
  }
  // there is not an easy way, if a MAP, flatmap or filter, return a finite on an infinite set
  // ex: map _%3==0, will return [0,1,2], but map _*3 is infinite
  def map[B](f: A=> B): MySet[B] = politelyFail
  def flatMap[B](f: A=>MySet[B]): MySet[B] = politelyFail
  def foreach(f: A=> Unit): Unit = politelyFail

  // both property and predicate hold
  def filter(predicate: A=>Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))

  def politelyFail = throw new IllegalArgumentException("Illegal argument. Really deep hole!")

  // exercise 2
  def -(elem:A):MySet[A] = filter(x=> x!=elem)
  //remove element
  def &(otherSet: MySet[A]): MySet[A] = filter(otherSet)// intersection
  def --(otherSet:MySet[A]): MySet[A] = filter(!otherSet)// difference with another set

  // exercise 3
  def unary_! : MySet[A] = new PropertyBasedSet[A](x=> !property(x))

}

//--------------------------------------------------

class someSet[A](head:A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = (head==elem) || tail.contains(elem)

  override def +(elem: A): MySet[A] = {
    if(this.contains(elem)) this
    else new someSet[A](head=elem, tail=this)
  }


  override def ++(other: MySet[A]): MySet[A] = {
    tail ++ other + head //just... wow
  }

  /*
  overview:
  [1 2 3 ] ++ [4 5 6 ] =
  [2 3 ] ++ [4 5 ] + 1 =
   [ 3 ] ++ [4 5 ] + 1 + 2 =
   [ ] ++ [4 5]  + 1 + 2 + 3 =
   [4 5 ] + 1 + 2 + 3 =
   [ 1 4 5 ] + 2 + 3 =
   [ 2 1 4 5 ] +  3 =
   [ 3 2 1 4 5 ]

   order doesnt matter, as this is a SET, not a Sequence
   */

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    if(predicate(head)) tail.filter(predicate) + head
    else tail.filter(predicate)
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }


  override def -(elem: A): MySet[A] = {
    if(head==elem) tail
    else tail-elem+head
  }

  override def &(otherSet: MySet[A]): MySet[A] = filter(otherSet) // filter is intersection, mind blown
  // equivalent to filter(x=> otherSet.contains(x))
  // equivalent to filter(x=> otherSet(x) )

  override def --(otherSet: MySet[A]): MySet[A] = filter(x => !otherSet(x))
    // filter(x=> !otherSet.contains(x))

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x=> !this.contains(x))
}


object MySet { // companion object;
  def apply[A](vals: A*): MySet[A] = { // A* is a var-arg

    @tailrec
    def buildSet(valSeq: Seq[A], acc:MySet[A]): MySet[A] = {
      if(valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }
  // vals which is var-arg is converted to Seq
    buildSet(vals.toSeq, new emptySet[A])
  }
}

object demo extends App {

  val someSet = MySet[Int](1,2,3)
  //val someSet = new someSet[Int](1,new emptySet[Int])
  someSet.foreach(println)
  println("----")

  (someSet ++ MySet[Int](-1,-2,3)).foreach(println) // 3 should be contained once
  println("---")
  someSet flatMap (x=> MySet[Int](x,x*10,x*100)) filter (x=> x%2==0) foreach println

  println("---")
  val propTest = new PropertyBasedSet[Int](x => {
    // all negative, and even positives
    if(x%2==0) true
    else if (x < 0) true
    else false
  })

  println(s"All negative with even positives (contains method): 2 ${propTest.contains(2)}" +
    s" -521: ${propTest.contains(-521)},    15521: ${propTest.contains(15521)}")

  val propTestwith3 = propTest + 3
  println(s"Adding 3 to the set, is 3 contained? ${propTestwith3.contains(3)}")

  val negatedPropTest = !propTest

  println(s"All negative with even positives NEGATED! (contains method): 2 ${negatedPropTest.contains(2)}" +
    s" -521: ${negatedPropTest.contains(-521)},    15521: ${negatedPropTest.contains(15521)}")


}