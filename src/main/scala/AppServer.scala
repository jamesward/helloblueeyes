import blueeyes._

object AppServer extends BlueEyesServer with HelloHtmlServices with HelloJsonServices with HelloStartupShutdownServices {
  override def main(args: Array[String]) = super.main(Array("--configFile", "server.conf"))
}
