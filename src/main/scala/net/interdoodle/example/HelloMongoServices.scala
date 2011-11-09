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
          jvalue {
            get { request: HttpRequest[JValue] =>
              helloConfig.database(selectAll.from("bars")) map { records =>
                HttpResponse[JValue](content = Some(JArray(records.toList)))
              }
            }
          } ~
          jvalue {
            post { request: HttpRequest[JValue] =>
              request.content map { jv: JValue =>
                helloConfig.database(insert(jv --> classOf[JObject]).into("bars"))
                Future.sync(HttpResponse[JValue](content = request.content))
              } getOrElse {
                Future.sync(HttpResponse[JValue](status = HttpStatus(BadRequest)))
              }
            }
          } ~
          produce(text/html) {
            get { request: HttpRequest[ByteChunk] =>
              val content = """<!DOCTYPE html>
                <html xmlns="http://www.w3.org/1999/xhtml">
                <head>
                  <script type="text/javascript" src="http://code.jquery.com/jquery-1.7.min.js"></script>
                  <script type="text/javascript">
                    $(function() {

                      $("#submit").click(addBar);
                      $("#bar").keyup(function(key) {
                        if (key.which == 13) {
                          addBar();
                        }
                      });

                      loadBars();

                    });

                    function loadBars() {
                      $.ajax("/mongo", {
                        contentType: "application/json",
                        success: function(data) {
                          $("#bars").children().remove();
                          $.each(data, function(index, item) {
                            $("#bars").append("<li>" + item.name + "</li>");
                          });
                        }
                      });
                    }

                    function addBar() {
                      $.ajax({
                        url: "/mongo",
                        type: 'post',
                        dataType: 'json',
                        success: loadBars,
                        data: '{"name": "' + $("#bar").val() + '"}',
                        contentType: 'application/json'
                      });
                    }
                  </script>
                </head>
                <body>
                  <h4>Bars:</h4>
                  <ul id="bars">
                  </ul>
                  <h4>Add a Bar:</h4>
                  <input id="bar"/>
                  <button id="submit">GO!</button>
                </body>
                </html>
                """;
              val response = HttpResponse[String](content = Some(content))
              Future.sync(response)
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