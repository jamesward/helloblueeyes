package net.interdoodle.hbe

import net.interdoodle.hbe.domain._
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterEach

/** @see http://www.scalatest.org/scaladoc/1.6.1/#org.scalatest.FunSuite */
class HelloBlueEyesSuite extends FunSuite {
  var monkey:Monkey = _

  test("default constructor values") {
    try {
      monkey = new Monkey()
    } catch {
      case e: Exception => println(e)
    }
    println(monkey.letterWeightMap)
    println(monkey.weightArray)
    assert(1==1)
  }
}