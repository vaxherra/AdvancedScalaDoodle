package exercises.part3concurrency

object ProducerConsumer extends App{
  /*
  1. Think of an example where notify all acts in a different way than notify

  2. DEADLOCK : two threads block each other;

  3. A LIVELOCK: threads cannot continue: they yield results to each other and nobody can continue;
   */



  // -------------------------- 01. Notify ALL
  def testNotifyAll() = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread( ()=> {
      bell.synchronized({
        println(s"[thread $i] waiting for a bell to ring;")
        bell.wait()
        println(s"[thread $i] the bell rang!")
      })
    }).start() )

    new Thread( () => {
      Thread.sleep(2000)
      println("[announcer] the bell is being RUNG")
      bell.synchronized{
          bell.notifyAll() //all threads may continue!
        // using a simple "notify" would only wake one thread, and the remaining won't be woken EVER!
      }
    }  ).start()

    /// end of testNotifyAll
  }
  //  testNotifyAll()

  // -------------------------- 02. DEADLOCK expample
  // a toy example of people first bowing to each other, and then rising from the bow, if
  // other person raises too
  case class Friend(name:String ){
    def bow(other:Friend) = {
      this.synchronized{
        println(s"$this: bows to $other")
        other.rise(this) // other rieses to me
        println(s"$this says: my friend $other has risen")
      }
    }

    def rise(other:Friend) = {
      this.synchronized({
        println(s"$this says: I am rising to my friend $other")
      })
    }

    var side = "right"
    def switchSides(): Unit = {
      if(side=="right") side = "left"
      else side="right"
    }

    def pass(other:Friend): Unit = {
      while(this.side==other.side) {
        println(s"$this says: Please $other, feel free to pass on side $side")
        switchSides()
        Thread.sleep(1000)
      }
    }

    /// end of case class Friend
  }

  def deadlockExample() = {
    val sam = Friend("Sam")
    val pierre = Friend("Pierre")

    new Thread(() => sam.bow(pierre)).start() // sam's lock is remaining blocked by "synchronized", and then locks pierres
    new Thread(() => pierre.bow(sam)).start() // this attemps to lock pierres, and then wants to lock sams
    // full lock
  }

  // deadlockExample() // nobody rises...


  // -------------------------- 03. LIVELOCK
  // people always GIVE way other people to cross (a street)
  // the thing is all people are very "polite" in this manner, switching sides
  // for other people to cross

  // simle scenario or two sides of the road

  def liveLockExample() : Unit = {
    val sam = Friend("Sam")
    val pierre = Friend("Pierre")

    new Thread( ()=> sam.pass(pierre)).start()
    new Thread( ()=> pierre.pass(sam)).start()
  }

  liveLockExample()
  // in LIVELOCK no threads are actually blocked (are LIVE), but they are not free to run;


}
