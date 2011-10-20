package net.interdoodle.hbe.domain

import akka.actor.Actor
import akka.event.EventHandler


/** Random or semi-random typist
 * @author Mike Slinn */

class Monkey (letterProbability:LetterProbabilities) extends Actor {

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
      EventHandler.info(this, monkeyRef.id + " received TypingRequest")
      val page = generatePage
      self.sender.foreach(_ ! TypingResult(monkeyRef, page))
    }
    case _ => EventHandler.info(this, "Monkey received an unknown message")
  }
}