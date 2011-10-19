package net.interdoodle.hbe.domain

import scala.collection.mutable.HashMap
import scala.math.{abs, min, max}
import collection.immutable.TreeMap

/** This class is initialized with a String containing text to analyze for letter frequency. A normalized probability
 * distribution is computed; a random number generator can then be used to generate weighted probabilities for generated
 * text. Class contains letter counts and normalized probabilities (0..1)
 * @author Mike Slinn */

class LetterProbabilities {
  /** Count of each letter added so far */
  protected val letterCountMap = HashMap.empty[Char, Int]

  /** Probability (0..1) that a letter might be randomly generated */
  var normalizedValues = scala.collection.immutable.TreeMap.empty[Char, Double]

  /** Running sum of normalized values that a letter might be randomly generated */
  var continuousValues = scala.collection.immutable.TreeMap.empty[Char, Double]


  def add(char:Char) {
    val letterCount:Int = letterCountMap.getOrElse(char, 0)
    letterCountMap += char -> (letterCount + 1)
  }

  /** Update the probabilities for each letter in the string */
  def add(str:String) {
    str.foreach(add(_))
  }

  def clear() {
    letterCountMap.clear()
    normalizedValues = scala.collection.immutable.TreeMap.empty[Char, Double]
    continuousValues = scala.collection.immutable.TreeMap.empty[Char, Double]
  }

  def computeValues() {
    if (letterCountMap.size==0)
      throw new Error("No characters have been provided")
    normalizedValues = collection.immutable.TreeMap(
      (for { (k, v) <- letterCountMap;
         t = (k, v.toDouble / letterCountMap.size)
    } yield t).toSeq: _*)
    var sum = 0.0
    for (kv <- normalizedValues) {
      sum += kv._2;
      continuousValues += kv._1 -> sum
    }
  }

  /** Find entry in continuousValues with value>=specified probability.
   * @param probability is forced into the range 0..1
   * @return letter corresponding to the specified probability. */
  def letter(probability:Double):Char = {
    if (letterCountMap.size==0)
      throw new Error("No characters have been provided")
    if (continuousValues.size==0)
      throw new Error("No letter probabilities have been computer")
    val prob = min(1.0, max(0.0, probability))
    val (ltMap, gteMap) = continuousValues.partition(kv => (kv._2<prob))
    if (gteMap.size==0)
      ltMap.last._1
    else { // account for double inaccuracy
      val headValue = gteMap.head._1
      if (gteMap.size==1)
        return headValue;
      val nextValue = gteMap.slice(1, 2).head._1
      if (abs(prob - headValue) < abs(prob - nextValue))
        headValue
      else
        nextValue
  }
  }

  def size = letterCountMap.size
}