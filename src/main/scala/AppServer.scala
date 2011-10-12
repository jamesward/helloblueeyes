import blueeyes._

object AppServer extends BlueEyesServer with HelloHtmlServices with HelloJsonServices {
  override def main(args: Array[String]) = super.main(Array("--configFile", "server.conf"))
}
