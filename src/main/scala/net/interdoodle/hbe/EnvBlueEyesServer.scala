package net.interdoodle.hbe

import blueeyes.core.service.{HttpReflectiveServiceList, HttpServer}
import blueeyes.core.data._
import blueeyes.core.service.engines.NettyEngine

/**
 * Created by IntelliJ IDEA.
 * User: jamesw
 * Date: 10/17/11
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */

trait EnvBlueEyesServer extends EnvHttpServer with HttpReflectiveServiceList[ByteChunk] with NettyEngine {



}