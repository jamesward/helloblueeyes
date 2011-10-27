package net.interdoodle.hbe.message

import akka.actor.ActorRef
import net.interdoodle.hbe.domain.Monkey


/**
 * @author Mike Slinn */

case class PageGenerated(monkeyRef:ActorRef, monkey:Monkey, text:String)