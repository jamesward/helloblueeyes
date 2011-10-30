package net.interdoodle.hbe.domain

import akka.config.Supervision.{OneForOneStrategy, Permanent}
import akka.event.EventHandler
import akka.stm.Ref
import akka.actor.{Actor, ActorRef}
import collection.mutable.HashMap
import net.interdoodle.hbe.message.{PageGenerated, TypingRequest, MonkeyResult}
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
      EventHandler.info(this, monkeyActorRef.uuid + " returned " + totalText)

      val textAnalysis = new TextAnalysis()
      textAnalysis.text = totalText
      //textAnalysis.resultMap += // TODO figure this out
      //textAnalysis.addTextMatch() // TODO figure this out

      val monkeyResult = monkeyResultRefMap.getOrElse(monkeyActorRef.uuid.toString, Ref[MonkeyResult]).get()
      monkeyResult.generatedText = totalText
      monkeyResult.results = textAnalysis :: monkeyResult.results
      monkeyResult.complete = false // TODO see if monkey is finished because they matched all the text
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