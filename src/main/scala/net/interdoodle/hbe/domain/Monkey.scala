package net.interdoodle.hbe.domain

/** Random or semi-random typist
 * @author Mike Slinn */

class Monkey (corpus:String) {
  val letterProbability = new LetterProbabilities();
  letterProbability.add(corpus)
  letterProbability.computeValues()
  

  def generateChar = letterProbability.letter(math.random)

  /** Generate 1000 random characters */
  def generatePage = {
    val sb = new StringBuilder();
    { for (i <- 0 to 1000)
        yield(generateChar.toString)
    }.addString(sb)
    sb.toString()
  }
}