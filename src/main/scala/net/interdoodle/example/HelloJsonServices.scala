package net.interdoodle.example;

import blueeyes.BlueEyesServiceBuilder
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpRequest, HttpResponse}
import blueeyes.core.service.{HttpService, HttpServiceContext}
import blueeyes.json.JsonAST.{JString, JField, JObject, JValue}
import blueeyes.core.data.{ByteChunk, BijectionsChunkJson, BijectionsChunkString}
import blueeyes.core.http.MimeTypes._

trait HelloJsonServices extends BlueEyesServiceBuilder with BijectionsChunkJson with BijectionsChunkString {
  val helloJson:HttpService[ByteChunk] = service("helloJson", "0.1") { context:HttpServiceContext =>
    request {
      path("/json") {
        contentType(application/json) {
          get { request: HttpRequest[JValue] =>
            val jstring = JString("Hello World!")
            val jfield = JField("result", jstring)
            val jobject = JObject(jfield :: Nil)
            val response = HttpResponse[JValue](content = Some(jobject))
            Future.sync(response)
          }
        } ~
        produce(text/html) {
          get { request: HttpRequest[ByteChunk] =>
            val contentUrl = System.getenv("CONTENT_URL")

            val content = <html xmlns="http://www.w3.org/1999/xhtml">
              <head>
                <script type="text/javascript" src={contentUrl + "jquery-1.7.min.js"}></script>
                <script type="text/javascript" src={contentUrl + "hello_json.js"}></script>
              </head>
              <body>
              </body>
            </html>
            Future.sync(HttpResponse[String](content = Some(content.buildString(true))))
          }
        }
      }
    }
  }
}