package net.interdoodle.hbe.domain

import collection.mutable.HashMap
import scala.Array

/** Random or semi-random typist
 * @author Mike Slinn */

class Monkey(letters:IndexedSeq[Char]=('a' to 'z') ++ ('a' to 'z') ++ ('0' to '9') /*++ ('.>,</?'";:[{]}\|=+-_\)\(*&^%$#@!~`')*/,
             weights:Array[Double]=Array[Double]()) {
  var letterWeightMap = new HashMap[Char, Double]
  var weightArray:Array[Double] = weights
  if (weights.length==0) { // all letters should have equal weight
    for (i <- 0 to  letters.length)
      weightArray(i) = 1.0 / letters.length;
  } else { // custom weighting was supplied
    for (letter <- letters)
      for (weight <- weights)
        letterWeightMap.put(letter, weight)
  }
}