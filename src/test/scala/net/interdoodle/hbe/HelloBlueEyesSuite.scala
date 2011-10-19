package net.interdoodle.hbe

import net.interdoodle.hbe.domain._
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterEach

/** @see http://www.scalatest.org/scaladoc/1.6.1/#org.scalatest.FunSuite */
class HelloBlueEyesSuite extends FunSuite {
  var monkey:Monkey = _

  test("default constructor values") {
    val letters:IndexedSeq[Char] = ('A' to 'Z') ++ ('a' to 'z') ++ ('0' to '9')
    val weights:Array[Double] = new Array[Double](letters.length)
    try {
      monkey = new Monkey(letters, weights)
    } catch {
      case e: Exception => println(e)
    }
    println(monkey.letterWeightMap)
    println(monkey.weightArray.toString)
    assert(1==1)
  }
}