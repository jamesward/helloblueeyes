package net.interdoodle.hbe.message

import net.interdoodle.hbe.domain.TextAnalysis


/**
 * @author Mike Slinn */

class MonkeyResult(
  var generatedText:String="",
  val results:List[TextAnalysis]=Nil,
  var msg:String="",
  var complete:Boolean = false)