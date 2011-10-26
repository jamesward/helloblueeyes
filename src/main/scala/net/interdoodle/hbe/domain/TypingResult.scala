package net.interdoodle.hbe.domain

import akka.actor.ActorRef


/**
 * @author Mike Slinn */

case class TypingResult (monkeyRef:ActorRef, monkey:Monkey, text:String)