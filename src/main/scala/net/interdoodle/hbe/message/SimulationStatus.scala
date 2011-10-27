package net.interdoodle.hbe.message

import collection.mutable.HashMap
import akka.actor.ActorRef


/** Contains simulationID->Option[MonkeyVisorRef] map
 * @author Mike Slinn */

class SimulationStatus {
  val simulations = new HashMap[String, Option[ActorRef]]

  def getSimulation(simulationID:String) = {
    simulations.getOrElse(simulationID, None)
  }

  def putSimulation(simulationID:String, monkeyRef:Option[ActorRef]) = {
    simulations += simulationID -> monkeyRef
  }
}