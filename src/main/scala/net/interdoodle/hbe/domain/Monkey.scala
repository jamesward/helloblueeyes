package net.interdoodle.hbe.domain

/** Random or semi-random typist
 * @author Mike Slinn */

class Monkey (letterProbability:LetterProbabilities) {

  /** @return a semi-random character */
  def generateChar = letterProbability.letter(math.random)

  /** @return 1000 semi-random characters */
  def generatePage = {
    val sb = new StringBuilder();
    { for (i <- 0 to 1000)
        yield(generateChar.toString)
    }.addString(sb)
    sb.toString()
  }
}