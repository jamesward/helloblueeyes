package net.interdoodle.hbe.domain

import org.scalatest.FunSuite
import org.scalatest.Assertions._
import akka.actor.Actor


/** @see http://www.scalatest.org/scaladoc/1.6.1/#org.scalatest.FunSuite
 * @author Mike Slinn */
class HanumanSuite extends FunSuite {
  test("generatePage") {
    /** Rough character frequency approximation */
    val document = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"*5 +
      "abcdefghijklmnopqrstuvwxyz"*25 +
      "0123456789"*2 +
      "`~!@#$%^&*()_-+={[}]|\\\"':;<,>.?/"
    val hanuman = Actor.actorOf(new Hanuman(document, 10)).start()
    val future = hanuman !!! "generatePages"
    val result:Any = future.get
    // todo write more tests and Hanuman business logic
  }
}