package net.interdoodle.hbe.message

import akka.actor.ActorRef
import net.interdoodle.hbe.domain.Monkey


/**
 * @author Mike Slinn */

case class PageGenerated(monkeyRef:ActorRef, totalText:String, page:String)