package lectures.part3concurency

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object futures2 extends App{

  // mini social network example;
  case class Profile(id:String, name: String){
    def poke(anotherProfile:Profile) = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }


  object SocialNetwork {
    // has a database of profiles hold as a map
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy Profile"
    )

    val friendShips = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill",
    )

    val random = new Random()

    // social network has two potentially expensive API calls
    def fetchProfile(id:String): Future[Profile]  = Future{
      // this will hold a future profile at some point
      // simulates fetching from the database;
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile:Profile): Future[Profile] = Future{
      Thread.sleep(random.nextInt(400))
      val bestFriendID = friendShips(profile.id)
      Profile(bestFriendID, names(bestFriendID))
    }

  }


  /// outside of object


  // an UGLY approach with NESTED FUTURES;
  // does not scale well for more complicated situations;

  // in a CLIENT application we want Mark to poke Bill;
  // 1. create a future by fetching Zuck's profile;
  // 2. obtain zuck's best friend (bill)
  // 3. fetch Bill's profile
  // 4. call poke on mark and provide bill profile;


  // 1. obtain a profile future for a given id
  val mark : Future[Profile] = SocialNetwork.fetchProfile("fb.id.1-zuck")

  println("01. NESTED FUTURES")
  mark.onComplete({
    case Success(markProfile) => {
      // 2. obtain zuck's best friend -> profile future
      val bill : Future[Profile] = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete({
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(exception)   => exception.printStackTrace()
      })
    }
    case Failure(exception) => exception.printStackTrace()
  })



  // THE SOLUTION TO THE UGLY APPROACH:
  // FUNCTIONAL COMPOSITION OF THE FUTURES;
  // map, flatMap, filter;

  //:::EXAMPLES:::
  // MAP::: converts a future of type X to a future of type Y;
  val marksName: Future[String]  = mark.map(_.name) // Future[Profile] -> Future[Name]
  // but map is of type Profile -> String

  //FLATMAP:::
  val marksBestFriend  = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  // here flatmap is of type Profile -> Future[Profile]

  //FILTER:::
  // filters a future, returning a future of the same time OR no such element exception;
  val zucksBestFriendRestricted = marksBestFriend.filter(p => p.name.startsWith("B"))

  //Thread.sleep(5500)
  //println(zucksBestFriendRestricted)

  // with map, flatMap and filter, we're ready to use for-comprehensions!!!
  println("02. FOR COMPREHENSIONS FOR FUTURES:")
  for {
    mark <-  SocialNetwork.fetchProfile("fb.id.1-zuck") // .recover .recoverWith to capture errors ...
    markBestie <- SocialNetwork.fetchBestFriend(mark) // ... can also use fallbacks to capture errors, and define ...
    // .. default behaviour
  }  mark.poke(markBestie)

  Thread.sleep(5500)

  //######################### FALLBACKS (in case the things go sour)
  // FALLBACK PATTERNS
  // pattern 1: RECOVERY PATTERN:

  val aProfileNoMatterWhat : Future[Profile]    =    SocialNetwork.fetchProfile("some-unknown-id").recover({
    // a partial function
    case e: Throwable => Profile("fb.id.0-dummy","Unknown Profile")
  })

  // there is also 'recoverWith' functions

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("some-unknown-id").recoverWith({
    case someError : Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
      // we ARE NOT protected if this other function call throws an exception;
  })


  // pattern 2: "FALLBACK"
  val fallbackResult = SocialNetwork.fetchProfile("some-other-unknown-id").fallbackTo({
    SocialNetwork.fetchProfile("fb.id.0-dummy")
  })


}
