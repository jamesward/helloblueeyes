package net.interdoodle.hbe.domain

import org.scalatest.FunSuite
import org.scalatest.Assertions._


/** @see http://www.scalatest.org/scaladoc/1.6.1/#org.scalatest.FunSuite
 * @author Mike Slinn */
class MonkeySuite extends FunSuite {
  test("generatePage") {
    /** Rough character frequency approximation */
    val document = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"*5 +
      "abcdefghijklmnopqrstuvwxyz"*25 +
      "0123456789"*2 +
      "`~!@#$%^&*()_-+={[}]|\\\"':;<,>.?/"
    val letterProbability = new LetterProbabilities()

    letterProbability.add(document)
    letterProbability.computeValues()
    assert(letterProbability.size==94)

    //val monkey = new Monkey(letterProbability)
    //val page = monkey.generatePage
    // todo write more tests and Monkey business logic
  }

}