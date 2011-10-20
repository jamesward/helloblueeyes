package net.interdoodle.hbe.domain

import collection.mutable.LinkedList
import akka.event.EventHandler
import akka.actor.{Actor, ActorRef}


/** Monkey god (supervisor) creates many Akka Actor references (to type Monkey) with identical probability distributions.
 * Dispatches requests to generate semi-random text.
 * @author Mike Slinn */

 class Hanuman (corpus:String, number:Int=100) extends Actor {
  var busyMonkeyActorRefs = List[ActorRef]()
  var monkeyActorRefs = List[ActorRef]()

  val letterProbability = new LetterProbabilities()
  letterProbability.add(corpus)
  letterProbability.computeValues()

  for (i <- 1 to number) {
    val monkeyActorRef = Actor.actorOf(new Monkey(letterProbability)).start()
    monkeyActorRef.id = "monkey_" + i.toString
    monkeyActorRefs = monkeyActorRef :: monkeyActorRefs
  }

  
  def generatePages() {
    for (monkeyActorRef <- monkeyActorRefs) {
      monkeyActorRef ! "type"
    }
  }

  def receive = {
    case "generatePages" => {
      EventHandler.info(this, "Hanuman received 'generatePages' request")
      for (monkeyActorRef <- monkeyActorRefs) {
        monkeyActorRef ! TypingRequest(monkeyActorRef)
        busyMonkeyActorRefs = monkeyActorRef :: busyMonkeyActorRefs
      }
    }
    case TypingResult(monkeyActorRef, text) => {
      println(monkeyActorRef.id + " returned " + text)
      busyMonkeyActorRefs = remove(monkeyActorRef, busyMonkeyActorRefs)
      println(busyMonkeyActorRefs.length)
      if (busyMonkeyActorRefs.isEmpty)
        println("Monkeys are all finished typing!")
    }
    case _ => {
      EventHandler.info(this, "Hanuman received an unknown message")
    }
  }

  /** Removes an element from a list */
  private def remove[A](c:A, l:List[A]) = l indexOf c match {
    case -1 => l
    case n => (l take n) ++ (l drop (n + 1))
  }
}