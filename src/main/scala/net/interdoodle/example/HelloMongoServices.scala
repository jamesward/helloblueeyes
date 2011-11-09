package net.interdoodle.example

import blueeyes.BlueEyesServiceBuilder
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpStatus, HttpRequest, HttpResponse}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.HttpStatusCodes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.{ByteChunk, BijectionsChunkString, BijectionsChunkJson}
import blueeyes.json.Printer;
import blueeyes.json.JsonAST.{JField, JString, JObject, JArray, JValue}
import blueeyes.persistence.mongo.MongoQueryBuilder
import blueeyes.persistence.mongo.EnvMongo


import com.mongodb.MongoURI

trait HelloMongoServices extends BlueEyesServiceBuilder with MongoQueryBuilder with BijectionsChunkJson with BijectionsChunkString {

  val helloMongo = service("helloMongo", "0.1") {
    logging { log => context =>
      startup {
        // use MONGOLAB_URI in form: mongodb://username:password@host:port/database
        val mongolabUri = System.getenv("MONGOLAB_URI")
        if (mongolabUri==null)
          throw new Exception("MONGOLAB_URI environment variable was not defined");
        val mongoURI = new MongoURI(mongolabUri)

        HelloConfig(new EnvMongo(mongoURI, context.config.configMap("mongo"))).future
      } ->
      request { helloConfig: HelloConfig =>
        path("/mongo") {
          accept(application/json) {
            produce(application/json) {
              get { request: HttpRequest[ByteChunk] =>
                helloConfig.database(selectAll.from("ticks")) map { records =>
                  HttpResponse[JValue](content = Some(JArray(records.toList)))
                }
              }
            }
          }
        }
          /*
          {
            get { request: HttpRequest[ByteChunk] =>
              val content = <html>
                              <body>Hello, world!</body>
                            </html>

              val response = HttpResponse[String](content = Some(content.buildString(true)))
              Future.sync(response)
            }
          } ~
          produce(application/json) {
            get { request: HttpRequest[ByteChunk] =>
              helloConfig.database(selectAll.from("ticks")) map { records =>
                HttpResponse[String](
                  content = Some("parseData(" +  Printer.compact(Printer.render(JArray(records.toList))) + ")")
                )
              }
            }
          }
           */
           /*~
              post { request: HttpRequest[String] =>

                request.content map { c =>
                  c.foreach( bc =>
                    println(bc)
                  )
                }

                /*{

                  val s:JString = JString("testing")
                  val f:JField = JField("foo", s)
                  var o:JObject = JObject(List(f))
                  log.info(Printer.pretty(Printer.render(o)))

                  _.flatMap(v => helloConfig.database(insert(v --> classOf[JObject]).into("ticks")) map (_ => HttpResponse[JValue]()))

                  //helloConfig.database(insert(o).into("ticks"))



                } getOrElse {

                }
                */
                Future.sync(HttpResponse[JValue](status = HttpStatus(BadRequest)))
              }
              */
      } ->
      shutdown { helloConfig: HelloConfig =>
        Future.sync(())
      }
    }
  }
}

case class HelloConfig(envMongo: EnvMongo) {
  val database = envMongo.database(envMongo.mongoURI.getDatabase)
}