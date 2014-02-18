package actors

import play.api.libs.json.{JsArray, JsValue}
import akka.actor.{ActorRef, Actor, Props}
import models.{Index, sj, zb, Moment}

case class InitValue(dimension: String, dbcode: String, result: JsValue)

object InitActor {
  def props(wsActor: ActorRef): Props = Props(classOf[InitActor], wsActor)
}

class InitActor(wsActor: ActorRef) extends Actor {
  val indexActor = context.actorOf(IndexActor.props())

  override def receive: Actor.Receive = {
    case InitValue(dimension: String, dbcode: String, result: JsValue) => {
      val array = result.asInstanceOf[JsArray].value
      val inits = array.map(js => Moment(dimension, dbcode, (js \ "AYEARMON").toString().replaceAll("\"", "")))

      if (dimension == sj.toString) {
        Moment.insert(inits)
        if (dbcode == "hgndks" | dbcode == "hgjdks" | dbcode == "hgydks") {
          for {
            indexId <- Index.fetchDataIndexId(dbcode)
            date <- inits.map(_.value)
          } {
            wsActor ! DataGet("l", dbcode, indexId, "000000", s"-1,$date", "000000", "region")
          }
        } else if (dbcode == "fsndks" | dbcode == "fsjdks" | dbcode == "fsydks") {
          println(dbcode)
        }

      } else if (dimension == zb.toString) {
        indexActor ! IndexValue(dimension, dbcode, result)
      }

    }
  }
}
