package net.interdoodle.hbe

import akka.stm.Ref
import blueeyes._
import blueeyes.concurrent.Future
import blueeyes.core.data.{BijectionsChunkJson, BijectionsChunkString, ByteChunk}
import blueeyes.core.http.{HttpRequest, HttpResponse, HttpStatus}
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.http.HttpStatusCodes
import blueeyes.json.JsonAST._
import core.service._
import java.util.UUID
import net.interdoodle.hbe.message.SimulationStatus
import net.interdoodle.hbe.domain.Hanuman
import net.lag.logging.Logger
import akka.actor.{ActorRef, Actor}


/** JSON service
 * @author Mike Slinn */

trait HelloJsonServices extends BlueEyesServiceBuilder
  with HttpRequestCombinators
  with BijectionsChunkString
  with BijectionsChunkJson {

  /** Contains simulationID->Option[MonkeyVisorRef] map */
  val simulationStatus = new SimulationStatus()
  val simulationStatusRef = new Ref(simulationStatus)
  var hanuman:Option[Hanuman] = None
  var hanumanActorRef:Option[ActorRef] = None

    
  val helloJson: HttpService[ByteChunk] = service("helloJson", "0.1") {
    requestLogging {
      logging {
        log =>
          context: HttpServiceContext =>
            request {
              path("/json/") {
                jvalue {
                  get { requestParam:HttpRequest[JValue] => reqHello(log) } ~
                  path('operation) {
                    get { request => reqOperation(log, request) }
                  } ~
                  path('operation/'id) {
                    get { request => reqDoCommand(log, request) }
                  } ~
                  path('operation/'id/'param) {
                    get { request => reqDoCommandParam(log, request) }
                  } ~
                  orFail(HttpStatusCodes.NotFound, "No handler found that could handle this request.") // return HTTP status 404
                }
              }
            }
      }
    }
  }

  private def reqHello[T, S](log:Logger) = {
    val json = JString("Hello, world, from the JSON service!")
    val response = HttpResponse[JValue](content = Some(json))
    log.info(response.toString())
    Future.sync(response)
  }

  private def reqOperation[T, S](log:Logger, request:HttpRequest[T]):Future[HttpResponse[JValue]] = {
    val operation = request.parameters('operation)
    if (operation=="newSimulation") {
      val document = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"*5 + // faked for now because BlueEyes cannot parse POST parameters
        "abcdefghijklmnopqrstuvwxyz"*25 +
        "0123456789"*2 +
        "`~!@#$%^&*()_-+={[}]|\\\"':;<,>.?/"
      val simulationID = UUID.randomUUID().toString

      simulationStatus.putSimulation(simulationID, None)
      hanumanActorRef = Some(Actor.actorOf(new Hanuman(simulationID, document, simulationStatusRef)))
      simulationStatus.putSimulation(simulationID, hanumanActorRef)
      simulationStatusRef.set(simulationStatus)

      Future.sync(HttpResponse(
        /*headers = HttpHeaders.Empty + sessionCookie(simulationID),*/
        content = Some(simulationID)))
    } else {
      val msg = "The only operation that can be without a simulationID is newSimulation. You specified '" + operation + "'"
      Future.sync(HttpResponse(status=HttpStatus(400, msg), content = Some(msg)))
    }
  }

  private def reqDoCommand[T, S](log:Logger, request:HttpRequest[T]):Future[HttpResponse[JValue]] = {
    val operation = request.parameters('operation).toString
    val simulationID = request.parameters('id).toString
    val simulation = simulationStatus.getSimulation(simulationID)
    Future.sync(HttpResponse(
      /*headers = HttpHeaders.Empty + sessionCookie(simulationID),*/
      content = Some(if (simulation==None)
        "Simulation with ID " + simulationID + " does not exist"
      else
        doCommand(operation, simulationID)
    )))
  }

  private def reqDoCommandParam[T, S](log:Logger, request:HttpRequest[T]):Future[HttpResponse[JValue]] = {
    val operation = request.parameters('operation)
    val simulationID = request.parameters('id)
    val param = request.parameters('param)
    val simulation = simulationStatus.getSimulation(simulationID)
    Future.sync(HttpResponse(
      /*headers = HttpHeaders.Empty + sessionCookie(simulationID),*/
      content = if (simulation==None)
        Some("Simulation with ID " + simulationID + " does not exist")
      else
        Some("Simulation ID=" + simulationID + "; operation: '" + operation + "'"))
    )
  }

  private def doCommand(command:String, simulationID:String):String = {
    command match {
      case "run" =>
        hanumanActorRef.get.start
        "Updated simulationStatus with new Hanuman instance " + hanumanActorRef.get.id + " and started hanuman"

      case "status" =>
        val simulationStatus = simulationStatusRef.get
        // TODO return simulationStatus object (in JSON format?) to client
        //monkeyResult.msg
        ""

      case _ =>
        command + "is an unknown command"
    }
  }

   /** Not sure we need cookies */
  /*private def sessionCookie(simulationID:String) = {
    val cookie = new HttpCookie {
      def name = "SessionID"
      def cookieValue = simulationID
      def expires = Some(HttpDateTime.parseHttpDateTimes("MON, 01-JAN-2001 00:00:00 UTC"))
      def domain = Option("")
      def path = Option("")
      // TODO add "; HttpOnly"
    }
    `Set-Cookie`(cookie :: Nil)
  }*/
}
