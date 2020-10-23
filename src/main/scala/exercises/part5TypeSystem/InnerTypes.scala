package exercises.part5TypeSystem

object InnerTypes extends App{
// DESIGNING TYPE SAFE API (for other people to use)

 /*
 We're a developer of a small DB key'd by Int's. We want to design a flexible API to possibly extend the keys to
 other data types.
  */

  // GIVEN, but able to modify
/*
  trait Item[K] // generic key type
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]
*/

  /*
  implement a method (get/extract/fetch/select) that receives a type parameter of 'ItemType' and a key of generic type;
   */

  // ----------------------------------------------------------------
  // THE SOLUTION
  // first introduce a new trait
  trait ItemLike{
    // with abstract TYPE mamber
    type Key
  }

  trait Item[K] extends ItemLike{
    // generic key type
    type Key= K

  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key :  ItemType#Key  ): ItemType =  ???


  get[IntItem](key=42) // ok
  get[StringItem](key="/home/") // ok

  // but we don't want to pollute the types, so the below would not be possible:
  //get[IntItem](key="/home/") // should throw an error:
  /*

   */

}
