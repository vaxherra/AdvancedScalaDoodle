package exercises.part5TypeSystem

object TypeMembers extends App{
  /*
  Classical API designer problem: enforce a TYPE to be applicable to some types ONLY

  given this locked API:
   */
  trait myList{
    type A
    def head: A
    def tail : myList
  }

  /*
  in our implementation we want to apply myList only to numbers! We cannot change the above.

  We can for to check for this at COMPILE TIME
   */

  // we want this NOT TO COMPILE (by default it does compile)
/*
  class customList(hd:String, tl:customList) extends myList{
    type A = String
    def head = hd
    def tail = tl
  }

  // we only want it to compile properly with INT
  class customIntList(hd:Int, tl:customIntList) extends myList{
    type A = Int
    def head = hd
    def tail = tl
  }

*/

  // the SOLUTION:
  trait ApplicableToNumbers { // applicable to Ints
    type A <: Int // all subtypes A deriving from Int type

    // in SCALA 3 we' be able to write (still waiting for stable scala3)
    // type A <: Int | Double | Float | Long | Byte
  }

  // we just need to add a MIX-IN trait using "with"
  /*class customList(hd:String, tl:customList) extends myList with ApplicableToNumbers {
    type A = String
    def head = hd
    def tail = tl
  }*/ // throws an error "incompatible type in overriding" found string expected <: Number

  // we only want it to compile properly with INT
  class customIntList(hd:Int, tl:customIntList) extends myList with ApplicableToNumbers {
    type A = Int
    def head = hd
    def tail = tl
  }
}
