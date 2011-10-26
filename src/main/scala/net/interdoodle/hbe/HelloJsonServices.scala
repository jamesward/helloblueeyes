package net.interdoodle.hbe

import blueeyes._
import blueeyes.concurrent.Future
import blueeyes.core.data.{BijectionsChunkJson, BijectionsChunkString, ByteChunk}
import blueeyes.core.http.{HttpRequest, HttpResponse, HttpStatus}
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.http.HttpStatusCodes
import blueeyes.json.JsonAST._
import core.service._
import net.interdoodle.hbe.domain.{Hanuman, SimulationResult}
import scala.collection.mutable.HashMap
import java.util.UUID
import akka.actor.{Actor, ActorRef}
import akka.stm.Ref
import net.lag.logging.Logger


/** JSON service
 * @author Mike Slinn */

  trait HelloJsonServices extends BlueEyesServiceBuilder
    with HttpRequestCombinators
with BijectionsChunkString
with BijectionsChunkJson {
  val simulationResult:SimulationResult = new SimulationResult(Nil)
  val sessions = new HashMap[String, Option[ActorRef]]

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
    if (operation=="newSession") {
      val sessionID = UUID.randomUUID().toString
      sessions += sessionID -> None
      Future.sync(HttpResponse(
        /*headers = HttpHeaders.Empty + sessionCookie(sessionID),*/
        content = Some(sessionID)))
    } else {
      val msg = "The only operation that can be without a sessionID is newSession. You specified '" + operation + "'"
      Future.sync(HttpResponse(status=HttpStatus(400, msg), content = Some(msg)))
    }
  }

  private def reqDoCommand[T, S](log:Logger, request:HttpRequest[T]):Future[HttpResponse[JValue]] = {
    val operation = request.parameters('operation).toString
    val sessionID = request.parameters('id).toString
    val session = sessions.getOrElse(sessionID, None)
    Future.sync(HttpResponse(
      /*headers = HttpHeaders.Empty + sessionCookie(sessionID),*/
      content = Some(if (session==null)
        "Session with ID " + sessionID + " does not exist"
      else
        doCommand(operation, sessions, sessionID)
    )))
  }

  private def reqDoCommandParam[T, S](log:Logger, request:HttpRequest[T]):Future[HttpResponse[JValue]] = {
    val operation = request.parameters('operation)
    val sessionID = request.parameters('id)
    val param = request.parameters('param)
    val session = sessions.getOrElse(sessionID, None)
    Future.sync(HttpResponse(
      /*headers = HttpHeaders.Empty + sessionCookie(sessionID),*/
      content = if (session==null)
        Some("Session with ID " + sessionID + " does not exist")
      else
        Some("session ID=" + sessionID + "; operation: '" + operation + "'"))
    )
  }
  private def doCommand(command:String, sessions:HashMap[String, Option[ActorRef]], key:String):String = {
    command match {
      case "run" =>
        val document = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"*5 +
          "abcdefghijklmnopqrstuvwxyz"*25 +
          "0123456789"*2 +
          "`~!@#$%^&*()_-+={[}]|\\\"':;<,>.?/"
        val hanumanRef = Actor.actorOf(new Hanuman(document, 10, Ref(simulationResult))).start()
        sessions += key -> Some(hanumanRef)
        "Updated session with new Hanuman instance " + hanumanRef.uuid

      case "status" =>
        // TODO return simulationResult object in JSON format to client
        simulationResult.msg
        ""

      case _ =>
        command + "is an unknown command"
    }
  }

   /** Not sure we need cookies */
  /*private def sessionCookie(sessionID:String) = {
    val cookie = new HttpCookie {
      def name = "SessionID"
      def cookieValue = sessionID
      def expires = Some(HttpDateTime.parseHttpDateTimes("MON, 01-JAN-2001 00:00:00 UTC"))
      def domain = Option("")
      def path = Option("")
      // TODO add "; HttpOnly"
    }
    `Set-Cookie`(cookie :: Nil)
  }*/
}
