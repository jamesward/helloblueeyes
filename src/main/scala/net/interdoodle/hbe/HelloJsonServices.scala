package net.interdoodle.hbe

import blueeyes._
import blueeyes.concurrent.Future
import blueeyes.core.data.{BijectionsChunkJson, BijectionsChunkString, ByteChunk}
import blueeyes.core.http.{HttpRequest, HttpResponse}
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.service.{HttpService, HttpServiceContext}
import blueeyes.json.JsonAST._


/** JSON service
 * @author Mike Slinn */

 trait HelloJsonServices extends BlueEyesServiceBuilder
  with HttpRequestCombinators
  with BijectionsChunkString
  with BijectionsChunkJson {
  val helloJson: HttpService[ByteChunk] = service("helloJson", "0.1") {
    requestLogging {
      logging {
        log =>
          context: HttpServiceContext =>
            request {
              path("/json/") {
                jvalue {
                  get {
                    requestParam:HttpRequest[JValue] =>
                      val json = JString("Hello World!")
                      val response = HttpResponse[JValue](content = Some(json))
                      log.info(response.toString())
                      Future.sync(response)
                  } ~
                  path('operation) {
                    get { request =>
                      val operation = request.parameters('operation)
                      Future.sync(HttpResponse(content = Some("operation=" + operation)))
                    }
                  } ~
                  path('operation/'id) {
                    get { request =>
                      val operation = request.parameters('operation)
                      val id = request.parameters('id)
                      Future.sync(HttpResponse(content = Some("session ID=" + id + "; operation=" + operation)))
                    }
                  }
                }
              }
            }
      }
    }
  }
}
