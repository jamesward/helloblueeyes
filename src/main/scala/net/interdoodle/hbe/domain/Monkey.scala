package net.interdoodle.hbe.domain

import akka.actor.Actor
import akka.config.Supervision.Permanent
import akka.event.EventHandler
import net.interdoodle.hbe.message.{PageGenerated, TypingRequest}


/** Random or semi-random typist
 * @author Mike Slinn */

class Monkey (val letterProbability:LetterProbabilities) extends Actor {
  var generatedText = ""

  self.lifeCycle = Permanent


  // TODO register with MonkeyVisor after restart


  /** @return a semi-random character */
  def generateChar = letterProbability.letter(math.random)

  /** @return 1000 semi-random characters */
  def generatePage = {
    val sb = new StringBuilder();
    { for (i <- 1 to 1000)
        yield(generateChar.toString)
    }.addString(sb)
    sb.toString()
  }

  def receive = {
    case TypingRequest(monkeyRef) => {
      EventHandler.info(this, monkeyRef.uuid + " received TypingRequest")
      var page = generatePage
      generatedText += page
      self.sender.foreach(_ ! PageGenerated(monkeyRef, generatedText, page))
    }

    case _ =>
      EventHandler.info(this, "Monkey received an unknown message")
  }
}