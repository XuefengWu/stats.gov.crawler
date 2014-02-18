package controllers

import play.api.mvc._
import akka.actor.{Props, ActorSystem}
import actors.{DataGet, InitGet, WGetActor}
import models.Index
import play.api.libs.json.Json

object Application extends Controller {

  implicit val indexWriter = Json.writes[Index]

  def index = Action {
    Ok(views.html.index("Gov stat index"))
  }

  def crawler = Action {
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
    Ok("starting...")
  }


  def indexs = Action {
    val indexs = Index.fetchDataIndexs("hgndks")
    Ok(Json.toJson(indexs))
  }

}