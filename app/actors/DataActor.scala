package actors

import akka.actor.{Props, Actor}
import play.api.libs.json.JsValue
import models.Data

case class DataValue(a: String, decode: String, regin: String, result: JsValue)

object DataActor {
  def props(): Props = Props(classOf[DataActor])
}

class DataActor extends Actor {
  override def receive: Actor.Receive = {
    case DataValue(a, decode, regin, result) => {
      val s = result.toString().replace("{", "").replace("}", "").trim
      if (!s.isEmpty) {
        val values = s.split("\",\"").map { v =>
            val vs = v.replaceAll("\\\"", "").split(":")
            val keys = vs(0).split("_")
            Data(a, decode, regin, keys(0), keys(2), vs(1).replace(",", "").toDouble)
        }

        Data.insert(values)
      }
    }
  }
}
