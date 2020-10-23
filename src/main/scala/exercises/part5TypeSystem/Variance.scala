package exercises.part5TypeSystem

import org.graalvm.compiler.nodeinfo.StructuralInput.Condition

object Variance extends App{
  // VARIANCE EXERCISES


  /* API design with invariant, co/contra-variant types
  1. Parking application that checks for the illegal parking - API DESIGN

  Invariant, Covariant, Contravariant parking of Things [T] that takes a List[T] as an arguments
  3. What if we wanted to make the PARKING a MONDAD!?
      -> add a flatMap
   */


  //-----------------------------------------------------------------------------------------
  // class hierarchy
  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle

  class Motorbike extends Bike
  class MountainBike extends Bike

  class Lorry extends Car
  class Sedan extends Car

  //-----------------------------------------------------------------------------------------

  //------------------------------------- Invariant Parking
  class ParkingInvariant[T](vehicles:List[T]){
    def park(vehicle: T): ParkingInvariant[T] = ???
    def impound(vehicles: List[T]) : ParkingInvariant[T] = ???
    def checkVehicles(conditions:String): List[T] = ???

    def flatMap[B](v : T => ParkingInvariant[B]): ParkingInvariant[B] = ???
  }

  //------------------------------------- Covariant Parking
  class ParkingCovariant[+T](vehicles:List[T]){
    def park[B >: T](vehicle:B): ParkingCovariant[B] = ??? // widening
    def impound[B >: T](vehicles: List[B]): ParkingCovariant[B] = ???
    def checkVehicles(conditions:String): List[T] = ???

    def flatMap[B](f : T => ParkingCovariant[B]) : ParkingCovariant[B] = ???
  }
  // in practice if our list has motorbikes and mountainbikes that adhere to class Bike, then "impounding" (removing)
  // one bike from the list WOULD NEVER widen our data type. But nothing stops us from negative impounding (i.e. adding
  // element to the list ("parking"), and hence we the compiler needs to secure for the fact that the
  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITIONS

  //------------------------------------- Contravariant Parking
  class ParkingContravariant[-T](vehicles: List[T]){
    def park(vehicle : T) : ParkingContravariant[T] = ???
    def impound(vehicles: List[T]): ParkingContravariant[T] = ???
    def checkVehicles[B <: T](conditions : String): List[B] = ???

    def flatMap[S <: T,B](someFunction : S => ParkingContravariant[B]): ParkingContravariant[B] = ???
    // the 'someFunction' (function type 'Function1[T, ParkingContravariant[B]')
    // but if you look at Function1 signature, it is contravariant in T, this all comes down to:
    // a. ParkinContravariant[-T] being contravariant in -T, and the Function1 being contravariant in T,
    // essentially APPLYING DOUBLE THE CONTRAVARIANCE GIVES US COVARIANCE!
  }
  // the scala "List" is covariant, while we have defined the parking as ContraVariant,
  // so a contravariants type occurs in covariant position, subtyping solves the issue

  /*
  GENERAL RULE OF THUMB:
  A) USE COVARIANCE - for a collection of things (as parking is a collection of vehicles)
  B) USE CONTRAVARIANCE - as a collection of ACTIONS on things (like parking, impounding, checking)

  for this example the CONTRAVARIANCE makes more sense;
   */

  //-----------------------------------------------------------------------------------------
  /*
  2. What if we didn't use List[T] but somebody else's API? InvariantList[T]
  3. What if we wanted to make the PARKING a MONDAD!?
      -> add a flatMap
   */
  // steps 2 an 3 at once (step 3 adds flatMap)
  class InvariantList[T]

  // a. the invariant parking stays the same
  class ParkingInvariant2[T](vehicles:InvariantList[T]){
    def park(vehicle: T): ParkingInvariant2[T] = ???
    def impound(vehicles: List[T]) : ParkingInvariant2[T] = ???
    def checkVehicles(conditions:String): InvariantList[T] = ???

    def flatMap[B](  v : T => ParkingInvariant2[B] ): ParkingInvariant2[T] = ???
  }

  // b. the covariant parking is modified for input invariant list of vehicles
  class ParkingCovariant2[+T](vehicles:InvariantList[T]){
    def park[B >: T](vehicle:B): ParkingCovariant2[B] = ??? // widening
    def impound[B >: T](vehicles: InvariantList[B]): ParkingCovariant2[B] = ???
    def checkVehicles[B >: T](conditions:String): InvariantList[B] = ???

    def flatMap[B](f : T => ParkingCovariant2[B]): ParkingCovariant2[B] = ???
  }
    // since the types returned are in Covariant positions, and types of the InvariantList is INVARIANT,
    // we need to widen the types;

  // c. the contravariant parking for invariant list of vehicles:

  class ParkingContravariant2[-T](vehicles: InvariantList[T]){
    def park(vehicle : T) : ParkingContravariant2[T] = ???
    def impound[B <: T](vehicles: InvariantList[B]): ParkingContravariant2[B] = ??? // a contravariant type -T occurs in invariant position ...
    // (InvariantList is... well ... invariant
    def checkVehicles[B <: T](conditions : String): InvariantList[B] = ???

    def flatMap[S <: T,B](someFunction : S => ParkingContravariant2[B]): ParkingContravariant2[B] = ???
  }





}
