package exercises.part4Implicits

object Pimping extends App{
  /*
  1. Enrich the string class:
    - asInt method
    - encrypt method, Caesar cipher

  2. Enrich the int class:
    - times(function)
       * 3.times(function)
    - multiply(list)
      3 * (1,2) => List(1,2,1,2,1,2)
   */


  // 01. Enriching string class

  implicit class richString(value:String){
    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    def asInt: Int = Integer.valueOf(value)

    def caesarEnrypt(shift:Int = 1): String = value.map( char => {
      val char_idx = alphabet.indexOf(char.toUpper)
      if(char_idx<0) char else {
        alphabet((char_idx+shift) % alphabet.size)
      }
    })

    def caesarDecrypt(shift:Int = 1): String = value.map(char => {
      val char_idx = alphabet.indexOf(char.toUpper)
      if(char_idx<0) char else {
        if(char_idx-shift>0) alphabet((char_idx-shift)% alphabet.size) else {
          alphabet((char_idx+alphabet.size-shift)%alphabet.size)
        }
      }
    })

  }

  println( "42".asInt==42)
  println( "Robert".caesarEnrypt(25))
  println("QNADQS".caesarDecrypt(25))


  // 2. ENRICHING INT CLASS

  implicit class RichInt(value:Int) {

    def times(func: () => Unit): Unit = {

      def timesAux(n:Int): Unit = {
        if(n<=0) ()
        else {
          func()
          timesAux(n-1)
        }
      }
      timesAux(value)
    }

    def multiply[T](aList: List[T]):List[T] = {

      def mAux(n:Int):List[T] = {
        if(n<0) List()
        else
          mAux(n-1) ++ aList
      }
      mAux(value)
    }


  }

  3.times(()=>println("Apply 3 times!"))
  println(3.multiply(List(1,2,3)))


  /*
  3. IMPLICIT CONVERSIONS BY IMPLICIT METHODS:

  we'd like to implement something silly:   "3" / 4 to be a float.
   */

  implicit def StringToInt(x:String): Int =  x.asInt
  println("6"/2)

  // EQUIVALENT IMPLEMENTATION OF AN IMPLICIT CLASS,
  // is where we define an implicit conversion from, ex. Type Int to our own AlternativeInt

  class AlternativeInt(value:Int)
  implicit def enrich(value:Int): AlternativeInt = new AlternativeInt(value)
  // is equivalent to
  // implicit class AlternativeInt(value:Int)


  // although the "enrich method" approach is more powerfull, they ARE DICSCOURAGED;
  // an example of the danger posed

  implicit def intToBoolean(i:Int): Boolean =  i==1 // should be i>=1
  // IF THERE IS A BUG IN IMPLICIT CONVERSION METHOD THEN IT IS SUPER HARD
  // TO TRACE IT BACK

  val aConditionValue = if(3) "OK" else "Not OK"
  println(aConditionValue)


  /*
  BEST PRACTICES FOR PIMPING LIBRARIES:
  * keep type enrichement to implicit classes and type classess
  * avoid implicit defs as much as possible
  * package implicits clearly, bring into SCOPE only what you need;
  * if you need type conversions, make them SPECIFIC

   */
}
