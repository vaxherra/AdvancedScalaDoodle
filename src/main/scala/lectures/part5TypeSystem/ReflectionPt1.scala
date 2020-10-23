package lectures.part5TypeSystem

object ReflectionPt1 extends App{
  // how to invoke a class method by calling its name dynamically at run-time?

  // reflections + macros + quasiquotes === METAPROGRAMMING!
  // metaprogramming allows to inspect and modify other programs and even themselves. A huge topic;

  case class Person(name:String) {
    def sayMyName():Unit = println(s"My name is $name, you're god damn right!")
  }

  // -------------------------------------------------------------------- USE CASE #1
  println("---------------------------- \nUse case #1  " )
  // step 0: import (built.sbt import might be necessary!)
  import scala.reflect.runtime.{universe => ru}

  // step 1: instantiate a "mirror"
  // a mirror is something that can reflect mirror
  val m = ru.runtimeMirror(getClass.getClassLoader) // a JVM instance of class loader;

  // step 2: create a class object
  val claZz = m.staticClass("lectures.part5TypeSystem.ReflectionPt1.Person") // the case class defined above; creating a class object by name

  // --- entering reflection territory -- :)
  // step 3: a reflected mirror
   val claZzMirror = m.reflectClass(claZz)

  // claZz is a class symbol (a description of a class)
  // claZzMirror can access its members (constructors,methods) and can apply them - instantiate objects, apply methods...

  // step 4: get the constructor
  val constructor  = claZz.primaryConstructor.asMethod // it is of a type 'MethodSymbol'

  // step 5: we want to reflect the constructor
  val constructorMirror = claZzMirror.reflectConstructor(constructor) // is of a type 'MethodMirror'

  // step 6: invoking the constructor
  val instance = constructorMirror.apply("Robert K.")
  println(instance)


  // -------------------------------------------------------------------- USE CASE #2
  println("---------------------------- \nUse case #2  " )
  // ex: I have an instance already computed
  val p = Person("Marc") // obtained from something else (like a wire)

  // method name is computed from somewhere else
  val methodName = "sayMyName"


  // step 1 - obtain a genera mirror (above)
  //val m = ru.runtimeMirror(getClass.getClassLoader) // a JVM instance of class loader;

  // step 2 - reflect the instance
  val reflected = m.reflect(p) // p is an instance, 'reflected' is of type 'InstanceMirror'

  // step 3 - method symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod // a 'MethodSymbol'

  // step 4 - reflect the method; as a result you can do "things" with it
  val method = reflected.reflectMethod(methodSymbol) // a 'MethodMirror'

  // step 5
  method.apply()

}
