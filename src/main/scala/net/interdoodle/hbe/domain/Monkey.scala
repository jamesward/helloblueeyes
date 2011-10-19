package net.interdoodle.hbe.domain

import collection.mutable.HashMap
import scala.Array

/** Random or semi-random typist
 * @author Mike Slinn */

class Monkey(letters:IndexedSeq[Char]=('A' to 'Z') ++ ('a' to 'z') ++ ('0' to '9') /*++ ('.>,</?'";:[{]}\|=+-_\)\(*&^%$#@!~`')*/,
             weights:Array[Double]=new Array[Double](0)) {
  var letterWeightMap = new HashMap[Char, Double]
  var weightArray = weights
  if (weights.length==0) { // all letters should have equal weight
    for (i <- 0 to  letters.length)
      weightArray(i) = 1.0 / letters.length;
  } else { // custom weighting was supplied
    for (letter <- letters)
      for (weight <- weights)
        letterWeightMap.put(letter, weight)
  }
}