package net.interdoodle.hbe

import blueeyes.BlueEyesServiceBuilder
import blueeyes.concurrent.Future
import blueeyes.core.http.HttpResponse
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.BijectionsChunkString
import blueeyes.json.Printer;
import blueeyes.json.JsonAST.{JField, JString, JObject, JArray}
import com.mongodb._
import blueeyes.persistence.mongo._

trait HelloMongoServices extends BlueEyesServiceBuilder with HttpRequestCombinators with BijectionsChunkString with MongoQueryBuilder {

    val helloMongo = service("helloMongo", "0.1") {
      logging { log => context =>
          startup {
            // use MONGOLAB_URI in form: mongodb://username:password@host:port/database
            val mongoURI = new MongoURI(System.getenv("MONGOLAB_URI"))
            
            HelloConfig(new EnvMongo(mongoURI, context.config.configMap("mongo"))).future
          } ->
          request { helloConfig: HelloConfig =>
              path("/mongo") {
                  produce(application/javascript) {
                      get { request =>
                        val s:JString = JString("testing")
                        val f:JField = JField("foo", s)
                        var o:JObject = JObject(List(f))
                        log.info(Printer.pretty(Printer.render(o)))

                        helloConfig.database(insert(o).into("ticks"))

                        helloConfig.database(selectAll.from("ticks")) map { records =>
                          HttpResponse[String](
                            content = Some("parseResponse(" + Printer.compact(Printer.render(JArray(records.toList))) + ");")
                          )
                        }
                      }
                  }
              }
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