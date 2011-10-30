package net.interdoodle.hbe.domain

import akka.event.EventHandler
import akka.stm.Ref
import akka.actor.{Actor, ActorRef}
import net.interdoodle.hbe.message.{PageGenerated, TypingRequest, MonkeyResult}
import collection.mutable.HashMap
import akka.config.Supervision.{Permanent, OneForOneStrategy}
import scala.collection.JavaConversions._


/** Monkey supervisor creates 'monkeysPerVisor' Akka Actor references (to type Monkey) with identical probability distributions.
 * Dispatches requests to generate semi-random text.
 * @author Mike Slinn */
class MonkeyVisor(val simulationID:String,
                  val corpus:String,
                  val monkeysPerVisor:Int,
                  val monkeyResultRefMap:HashMap[String, Ref[MonkeyResult]]) extends Actor {
  var monkeyRefList = List[ActorRef]()
  val letterProbability = new LetterProbabilities()
  letterProbability.add(corpus)
  letterProbability.computeValues()

  self.lifeCycle = Permanent
  self.faultHandler = OneForOneStrategy(List(classOf[Throwable]), 5, 5000)


  /** If any monkey finishes, we are done */
  override def postStop() = {
    for (val monkeyRef <- self.linkedActors.values()) {
      monkeyRef.stop()
      self.unlink(monkeyRef)
      // TODO how to delete Monkeys?
    }
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
    }
  }

  def receive = {
    case "generatePages" => {
      EventHandler.info(this, "MonkeyVisor received 'generatePages' request")
      generatePages()
    }

    case PageGenerated(monkeyActorRef, totalText, page) => {
      EventHandler.info(this, monkeyActorRef.uuid + " returned " + text)
      // TODO add last monkey's results to simulationResult.list and see if they are finished
      val monkeyResult = monkeyResultRefMap.get(monkeyActorRef.uuid.toString).get()
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