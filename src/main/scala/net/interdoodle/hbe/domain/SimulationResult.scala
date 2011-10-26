package net.interdoodle.hbe.domain

/**
 * @author Mike Slinn */

class SimulationResult(val results:List[TypingResult]=Nil, var msg:String="", var complete:Boolean = false)