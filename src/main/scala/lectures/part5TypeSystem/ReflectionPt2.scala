package lectures.part5TypeSystem



object ReflectionPt2 extends App{
  // Reflection in the context of TYPE ERASURE

  case class Person(name:String) {
    def sayMyName():Unit = println(s"My name is $name, you're god damn right!")
  }

  val methodName = "sayMyName"

  // generic types are erased at compile time, and this has to do with JVE, and its philosophy of operation
  // (note to self: is it also a feature in scala 3?)
  // java 5 introduced generics, and because it is backwards compatible - it erasures them at compile time (sic!)

  // point #1: you cannot differentiate between generic types at runtime
  val numbers: List[Int] = List(1,2,3)
  numbers match {
    case listofstrings: List[String] => println(" a list of strings ")
    case listofnums : List[Int] => println(" a list of numbers")
  }
  // we're gonna match first case!

  // point #2 : limitation on overloads
  def processList(a : List[Int]): Int = 43
  // def processList(a : List[String]): Int = 42
  //list types would get erased!. and an error would be thrown : double definition (if the above line is uncommented)
  // we can overcome this with MAGNET PATTERN;

  //-----------------------------------------------------------------------------------------
  // ALSO, WE CAN USE THE REFLECTION -> " T Y P E       T A G S "
  import scala.reflect.runtime.{universe => ru}
  import ru._ // TypeTag (among others)

  // step 1: creating a type tag manually
  val ttag = typeTag[Person] // Person from ReflectionPt1
  // with a type tag, we can use whatever we have define in that class, for example:
  println("Type tag .tpe method result:\n:>\t "+ttag.tpe) // tpe is the name of the tag

  class MyMap[K,V] // do not care about implementation here

  // step 2:
  // pass TypeTags as implicit parameters (the preferred way)
  def getTypeArguments[T](value : T)(implicit typeTag: TypeTag[T]) =  typeTag.tpe match {
    case TypeRef(_, _, typeArguments) => typeArguments
    // type ref has 3 parameters, (inspect the source code): Type (the whole type), Symbol (description, and List[Type] a generic type
    // used at runtime!
    case _ => List()
  }


  val myMap = new MyMap[Int,String]  // we want to have access to these two types at runtime, by calling ...
  val typeArgs = getTypeArguments(myMap) // implicitly passes a typeTag : TypeTag[MyMap[Int,String]]

  println(typeArgs) // List(Int,String)
  // at compile time, the compiler creates this 'typeTag' for us
  // the types can safely be erased, after compilation, by the typeTag will hold the info that we want (we print it here)
  // we can have access to the erased information at the run time, we can do a bunch of CHECKS!

  // step 3: all sorts of checks and operations;
  def isSubType[A,B](implicit ttagA: TypeTag[A], ttagB: TypeTag[B]): Boolean = { // test whether A <: B (A is a subtype of B)
  ttagA.tpe <:< ttagB.tpe  // an interesting method <:<
  }

  class Animal
  class Dog extends Animal
  println(isSubType[Dog,Animal]) // true

  // analogically to the code defined in 'ReflectionPat1', the only change is the methodSymbol val definition
  // that utilizes these typeTag[Person] istead of ru.typeOf[Person]
  val anotherMethodSymbol = typeTag[Person].tpe.decl(ru.TermName(methodName)).asMethod

  val m = ru.runtimeMirror(getClass.getClassLoader) // a JVM instance of class loader;
  val p = Person("Heisenberg") // obtained from something else (like a wire)
  val reflected = m.reflect(p) // p is an instance, 'reflected' is of type 'InstanceMirror'
  val someMethod = reflected.reflectMethod(anotherMethodSymbol)
  someMethod.apply() //
}
