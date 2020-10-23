package lectures.part3concurency

import java.util.concurrent.Executors

object intro extends App{
  // JVM threads are instances of a class;

  val aThred = new Thread(new Runnable {
    override def run(): Unit = println("Running in parallel")
  })

  // to start a thread we need to call .start on a thread INSTANCE!
  // not a run on thread!!!!
  aThred.start()
  aThred.join()//blocks further exec until aThread finishes running

  val thred1 = new Thread( () => (1 to 5).foreach(_ => println("thread 1"))  )
  val thred2 = new Thread( () => (1 to 5).foreach(_ => println("thread 2"))  )

  thred1.start()
  thred2.start()


  // executors;
  // threads are expensive to start and kill;
  val pool = Executors.newFixedThreadPool(10)
  pool.execute( () => println("Something in the thread pool")  )
  pool.execute( () => println("Something ELSE in the thread pool")  )

  // shutting down pools
  pool.shutdown() // no more actions can be submitted
  // pool.execute( () => println("throws an exception) ) // cannot do, throws an exception

  // pool.shutdownNow() // interrupts current threads that run at the moment!

  pool.isShutdown // will return True, even in the case that pools execute something at the moment,
  // this means if pool is NOT ACCEPTING any more pools at the moment;

  def runInParallel = {
    var x = 0

    val thread1 = new Thread( () => x=1)
    val thread2 = new Thread( () => x=5)
    // RACE CONDITION: two threads attempt to write to the same variable
    // hard to diagnose and fix
    thread1.start()
    thread2.start()

    print(""+x+" ")
  }

  for (_ <- 1 to 100) runInParallel


  //-------------------------//
  class BankAccount(var amount : Int) {
    override def toString: String = "" + amount

  }

    def buy(account: BankAccount, thing:String, price :Int): Unit = {
      account.amount -= price
      //println(f"I've bought ${thing} for ${price}. My account is now ${account}")
    }

    for (_ <- 1 to 1000){
      val acc = new BankAccount(amount=50000)
      val thread1 = new Thread( () => buy(acc, "macbook pro 16", 3500))
      val thread2 = new Thread( () => buy(acc, "ajfon", 1500))

      // more race conditions
      thread1.start()
      thread2.start()
      Thread.sleep(100)
      if(acc.amount != (50000-3500-1500) ) println("AHA! " + acc) // we can find the RACE CONDITIONS
    }

  // SOLUTION #1: USE SYNCHRONIZED()
  def buySafe(acc: BankAccount, thing: String, price : Int): Unit ={
    acc.synchronized({
      // no two threads can evaluate this at the same time
      acc.amount -= price
      println(s"I've bought ${thing}, my balance is ${acc}")
    }
    )
  }
  // thread safe method


  // SOLUTION 2: use @volatile annotatio;
  // used on val or var, means that the references are synchronized!
  // we'd re-write the class definition

  class BankAccountSafe( @volatile var amount:Int){
    override def toString: String = "" + amount
  }

  // synchronized option (solution 1) is more flexible, as we can use multiple expressions in a code block

}
