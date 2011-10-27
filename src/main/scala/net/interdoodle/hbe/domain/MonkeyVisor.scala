package net.interdoodle.hbe.domain

import akka.event.EventHandler
import akka.stm.Ref
import akka.actor.{Supervisor, Actor, ActorRef}
import akka.config.Supervision.{OneForOneStrategy, SupervisorConfig}
import net.interdoodle.hbe.message.{PageGenerated, TypingRequest, MonkeyResult}
import collection.mutable.{HashMap, LinkedList}


/** Monkey supervisor creates 'number' Akka Actor references (to type Monkey) with identical probability distributions.
 * Dispatches requests to generate semi-random text.
 * @author Mike Slinn */
class MonkeyVisor(val simulationID:String,
                  val corpus:String,
                  val number:Int=100,
                  var monkeyResultRefMap:HashMap[String, Ref[MonkeyResult]]) extends Actor {
  var busyMonkeyActorRefs = List[ActorRef]()
  var monkeyActorRefs = List[ActorRef]()
  val letterProbability = new LetterProbabilities()
  letterProbability.add(corpus)
  letterProbability.computeValues()

  self.faultHandler = OneForOneStrategy(List(classOf[Throwable]), 5, 5000)

  for (i <- 1 to number) {
    val monkeyActorRef = Actor.actorOf(new Monkey(letterProbability)).start()
    monkeyActorRef.id = "monkey_" + i.toString
    monkeyActorRefs = monkeyActorRef :: monkeyActorRefs
  }


  def generatePages() {
    for (monkeyActorRef <- monkeyActorRefs) {
      monkeyActorRef ! TypingRequest(monkeyActorRef)
      busyMonkeyActorRefs = monkeyActorRef :: busyMonkeyActorRefs
    }
  }

  def receive = {
    case "generatePages" => {
      EventHandler.info(this, "MonkeyVisor received 'generatePages' request")
      generatePages()
    }

    case PageGenerated(monkeyActorRef, monkey, text) => {
      println(monkeyActorRef.id + " returned " + text)
      // TODO add last monkey's results to simulationResult.list
      busyMonkeyActorRefs = remove(monkeyActorRef, busyMonkeyActorRefs)
      val monkeyResult = monkeyResultRefMap.get(monkeyActorRef.id).get()
      if (busyMonkeyActorRefs.isEmpty) {
        monkeyResult.msg = "Monkeys are all finished"
        monkeyResult.complete = true
        for (monkeyRef <- monkeyActorRefs) {
          monkeyRef.stop
        }
      } else {
        monkeyResult.msg = "" + busyMonkeyActorRefs.length + " monkeys are still typing"
      }
    }

    case _ => {
      EventHandler.info(this, "MonkeyVisor received an unknown message")
    }
  }

  /** Removes an element from a list */
  private def remove[A](c:A, l:List[A]) = l indexOf c match {
    case -1 => l
    case n => (l take n) ++ (l drop (n + 1))
  }
}