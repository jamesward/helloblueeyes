package net.interdoodle.hbe.domain

import akka.event.EventHandler
import akka.stm.Ref
import akka.actor.{Actor, ActorRef}
import akka.config.Supervision.OneForOneStrategy
import net.interdoodle.hbe.message.{PageGenerated, TypingRequest, MonkeyResult}
import collection.mutable.HashMap


/** Monkey supervisor creates 'monkeysPerVisor' Akka Actor references (to type Monkey) with identical probability distributions.
 * Dispatches requests to generate semi-random text.
 * @author Mike Slinn */
class MonkeyVisor(val simulationID:String,
                  val corpus:String,
                  val monkeysPerVisor:Int,
                  val monkeyResultRefMap:HashMap[String, Ref[MonkeyResult]]) extends Actor {
  var busyMonkeyActorRefs = List[ActorRef]()
  var monkeyRefList = List[ActorRef]()
  val letterProbability = new LetterProbabilities()
  letterProbability.add(corpus)
  letterProbability.computeValues()

  self.faultHandler = OneForOneStrategy(List(classOf[Throwable]), 5, 5000)


  override def postStop() = {
    // TODO how to delete Monkeys?
  }

  override def preStart() = {
    for (i <- 1 to monkeysPerVisor) {
      val monkeyRef = Actor.actorOf(new Monkey(letterProbability))
      self.link(monkeyRef)
      monkeyRefList = monkeyRef :: monkeyRefList
      monkeyRef.start()
    }
  }

  def generatePages() {
    for (monkeyActorRef <- monkeyRefList) {
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
      println(monkeyActorRef.uuid + " returned " + text)
      // TODO add last monkey's results to simulationResult.list
      busyMonkeyActorRefs = remove(monkeyActorRef, busyMonkeyActorRefs)
      monkeyActorRef.stop
      val monkeyResult = monkeyResultRefMap.get(monkeyActorRef.uuid.toString).get()
      if (busyMonkeyActorRefs.isEmpty) {
        monkeyResult.msg = "Monkeys are all finished"
        monkeyResult.complete = true
        self.sender ! "MonkeyVisor is done"
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