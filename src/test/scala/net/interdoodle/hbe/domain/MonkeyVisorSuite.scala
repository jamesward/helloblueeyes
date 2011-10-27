package net.interdoodle.hbe.domain

import org.scalatest.FunSuite
import org.scalatest.Assertions._
import akka.actor.Actor
import akka.stm.Ref
import net.interdoodle.hbe.message.MonkeyResult
import collection.mutable.HashMap


/** @see http://www.scalatest.org/scaladoc/1.6.1/#org.scalatest.FunSuite
 * @author Mike Slinn */
class MonkeyVisorSuite extends FunSuite {
  test("generatePage") {
    val simulationID:String = "bogusSimulationID"
    val monkeyResult:MonkeyResult = new MonkeyResult()
    var monkeyResultRef = Ref(monkeyResult)
    val monkeyResultRefMap = new HashMap[String, Ref[MonkeyResult]]()
    monkeyResultRefMap.put(simulationID, monkeyResultRef)

    /** Rough character frequency approximation */
    val document = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"*5 +
      "abcdefghijklmnopqrstuvwxyz"*25 +
      "0123456789"*2 +
      "`~!@#$%^&*()_-+={[}]|\\\"':;<,>.?/"
    val hanuman = Actor.actorOf(new MonkeyVisor(simulationID, document, 10, monkeyResultRefMap)).start()
    val future = hanuman !!! "generatePages"
    val result:Any = future.get
    // todo write more tests and MonkeyVisor business logic
  }
}