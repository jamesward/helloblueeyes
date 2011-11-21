package net.interdoodle.example

import blueeyes.core.service.{HttpReflectiveServiceList, HttpServer}
import blueeyes.core.data._
import blueeyes.core.service.engines.NettyEngine


/** Server definition
 * @author James Ward */

trait EnvBlueEyesServer extends EnvHttpServer with HttpReflectiveServiceList[ByteChunk] with NettyEngine