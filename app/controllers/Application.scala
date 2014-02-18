package controllers

import play.api._
import play.api.mvc._
import akka.actor.{Props, ActorSystem}
import actors.{DataGet, IndexPost, InitGet, WGetActor}

object Application extends Controller {

  def index = Action {

    val actor = ActorSystem("MyActors").actorOf(Props[WGetActor])
    actor ! InitGet("zb", "hgydks")
    actor ! InitGet("zb", "hgjdks")
    actor ! InitGet("zb", "hgndks")

    actor ! InitGet("zb", "fsydks")
    actor ! InitGet("zb", "fsjdks")
    actor ! InitGet("zb", "fsndks")

    actor ! InitGet("sj", "hgydks")
    actor ! InitGet("sj", "hgjdks")
    actor ! InitGet("sj", "hgndks")

    actor ! InitGet("sj", "fsydks")
    actor ! InitGet("sj", "fsjdks")
    actor ! InitGet("sj", "fsndks")

    actor ! DataGet("l", "hgndks", "A050101", "000000", "-1,1983", "000000", "region")

    Ok(views.html.index("Your new application is ready."))
  }

}