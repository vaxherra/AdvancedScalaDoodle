package lectures.part3concurency

import scala.collection.mutable
import scala.util.Random

object interThreadComms extends App{
  /*
  the PRODUCER-CONSUMER PROBLEM;

    a small container that wraps a single value

    produces -> [ x ] sets a value inside container
    consumer extracts value

    producer and consumer run in parallel and don't know know when producer finishes

    we have to force consumer to wait for the product

    although the threads might not be orderer, we have to force them to run in a guaraneed (certain) order;


   */

  class SimpleContainer {
    private var value:Int = 0

    def isEmpty: Boolean = value==0

    def get: Int = { //get and reset to default -> "CONSUME"
    val result = value
    value = 0
    result
    }
    // PRODUCE METHOD
    def set(newValue:Int) = value = newValue
  }


  // --------------------------------------------
  def naiveProducerConsumer():Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(()=> {
      println("[Karen the consumer]: I am waiting")
      while(container.isEmpty) { // "BUSY WAITING" A COMPUTING WASTE!
        println("[Karen the consumer]: Can I speak to your manager?")
      }

      // at this point the PRODUCER produces stuff, Karen can get what she wants
      println("[Karen the consumer]: I have consumed "+container.get)

    })

    val producer = new Thread(()=>{
      println("[Producer]: Hard at work")
      Thread.sleep(1)
      val value = 42
      println(s"[Producer]: I have produced a value $value")
      container.set(value)
    })

    consumer.start()
    producer.start()

  }

  //naiveProducerConsumer() // uncomment to run


  // NEW TOOLS: WAIT AND NOTIFY;

  /*
  "wait()" on an object's monitor suspends the thread indefinitely...

  //thread 1
  val someObject = "hello"

  someObject.synchronized{ <- lock the object's monitor
    //code part 1
    someObject.wait() <- release the lock and WAIT
    //code part 2 <- when allowed to proceed, lock the monitor again and continue
  }

  //thread 2
   someObject.synchronized{ <- lock the object's monitor
    //code part 1
    someObject.notify() <- signal THREAD 1, that they may continue with thread1.code part 2
    // if there are multiple, we don't know which thread is notified
    // ".notifyAll()" notifies all threads
    //code part 2 <-
  }

WAITING AND NOTIFYING ARE ONLY ALLOWED IN SYNCHRONIZED EXPRESSIONS;
OTHERWISE THEY CRUSH YOUR PROGRAM;



   */

  def smartProducerConsumer(): Unit = {

    val container = new SimpleContainer

    val consumer = new Thread( ()=>{
      println("Consumer waiting")
      container.synchronized{
        container.wait() // the consumer RELEASES the lock on the CONTAINER
        // and suspends further execution, until something else "notifies" the consumer that the container
        // is ready to go
      }

      // HERE, the container must HAVE some value
      println(s"[Consumer] I have consumed ${container.get}")
    })

    val producer = new Thread( ()=> {
      println("Producer producing...")
      Thread.sleep(1500)
      val value = 42

      container.synchronized{
        println(s"I am roducing ${value}")
        container.set(value)
        container.notify() // gives the signal that the "wait" is over
      }
    })


    consumer.start()
    producer.start()

  }


  // running
  //println("\n\n Running smart producer consumer -------------------->")
  //smartProducerConsumer()


  // --------------------------------------------------
  /*
  In this example we have a buffer when consumers consume, and producers produce to;

  producer -> [ X Y Z Empty ... ] -> conusumer (consumes X)

  both producer and consumer can both block each other
   */

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity: Int = 5 // producer can produces max capacity without blocking, and consumer can consume capacity without blocking;

    val consumer = new Thread( () => {
      val random = new Random() // simulating sleeping times;
      while(true){
         //synchronize on the buffer
        buffer.synchronized{
          // if the buffer is empty, then wait
          if(buffer.isEmpty){
            println("[consumer] Buffer empty; Waiting ...")
            buffer.wait()
          }
            // at least one value in the buffer here: either from the start or woken up by the producer;
          val x = buffer.dequeue()
          println(s"[Consumer]: I have consumed $x")
          buffer.notify() // the consumer has finished extracting value; just in case a producer is sleeping
          // notify the producer to resume

        }
        Thread.sleep(random.nextInt(500)) //simulates a long computation
      }
    })

    val producer = new Thread(() => {

      val random = new Random() // simulating sleeping times;
      var i = 0 // tracking which values are produced

      while(true){
        buffer.synchronized{
          // if buffer full,
          if(buffer.size == capacity){
            println(s"[Producer]: Buffer is full. Waiting....")
            buffer.wait()
          }
          // here there must be one empty space in the buffer to produce a value to;
          println(s"[Producer] producing $i")
          buffer.enqueue(i)
          buffer.notify() // in case the consumer is sleeping, as there are not values to consume (but not necessarily the case)
          i += 1
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()

  }

  //print("--------- Producer Consumer with Large Buffer -------------")
  //prodConsLargeBuffer()

  //--------------------------------------------------
  /*
  In an advanced scenario we have multiple producers and multiple consumers that use THE SAME BUFFER;



   */

  class Consumer(id: Int , buffer: mutable.Queue[Int], max_wait : Int = 500) extends Thread {
    override def run(): Unit = {

      val random = new Random() // simulating sleeping times;
      while(true){
        //synchronize on the buffer
        buffer.synchronized{
          // if the buffer is empty, then wait
          while(buffer.isEmpty){
            // WHILE LOOP: as after being notified (woken up) we have to check "who" notified, prouducer or other consumer
            // so if buffer is empty, it waits for notification, after being notified, it checks second time the while
            // condition, if it is not empty, then we consume it, and notify.
            println(s"[consumer id $id] Buffer empty; Waiting ...")
            buffer.wait()
          }
          // at least one value in the buffer here: either from the start or woken up by the producer;
          val x = buffer.dequeue()
          println(s"[consumer id $id]: I have consumed $x")
          buffer.notify()  // notifies somebody: either producer or a consumer to wake up;

        }
        Thread.sleep(random.nextInt(max_wait)) //simulates a long computation
      }
      //---
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int, max_wait :Int = 500) extends Thread {
    override def run(): Unit = {


      val random = new Random() // simulating sleeping times;
      var i = 0 // tracking which values are produced

      while(true){
        buffer.synchronized{
          // if buffer full,
          while(buffer.size == capacity){//same logic as with consumer
            // we have to have both conditions: being woken up, and re-check if buffer is not over the capacity
            // as we might have been woken up by other producing filling buffer to the capacity;
            println(s"[Producer $id]: Buffer is full. Waiting....")
            buffer.wait()
          }
          // here there must be one empty space in the buffer to produce a value to;
          println(s"[Producer $id] producing $i")
          buffer.enqueue(i)
          buffer.notify() // notofies somebody (either producer or a consumer) to wake up.
          i += 1
        }
        Thread.sleep(random.nextInt(max_wait))
      }
      // <-- end of run
    }
  }


  // demo multi producers and consumers

  def multiProdCons(nConsumers:Int, nProdcusers:Int, capacity : Int  = 5,max_prod_wait:Int=500, max_cons_wait:Int=500): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]

    // START multiple consumers and producers;
    (1 to nConsumers).foreach(i => new Consumer(id=i, buffer=buffer,max_cons_wait).start() )
    (1 to nProdcusers).foreach(i => new Producer(id=i, buffer=buffer, capacity=capacity,max_prod_wait).start() )
  }

  multiProdCons(30,10,capacity = 3, max_cons_wait = 2500, max_prod_wait = 100)
}
