package net.interdoodle.hbe

import domain._
import org.scalatest.SuperSuite

/** @see http://www.artima.com/sdp/original/org/scalatest/Suite.html
 * Other docs say SuperSuite is deprecated, and that Suites should be used instead, but Suites is not defined
 * @author Mike Slinn */
class HelloBlueEyesSuite extends SuperSuite(
  List(
    new LetterProbabilitiesSuite,
    new MonkeySuite,
    new HanumanSuite
  )
) { }