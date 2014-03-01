package actors

import akka.actor.{Props, Actor}
import play.api.libs.json.{JsArray, JsValue}
import models.{Index, Data}

case class DataValue(a: String, decode: String, region: String, result: JsValue)
case class UnitValue(dbcode: String, result: JsValue)

object DataActor {
  def props(): Props = Props(classOf[DataActor])
}

class DataActor extends Actor {

  implicit def js2String(js: JsValue):String = js.toString().replaceAll("\"","")

  override def receive: Actor.Receive = {
    case DataValue(a, decode, region, result) => {
      val s = result.toString().replace("{", "").replace("}", "").trim
      if (!s.isEmpty) {
        val values = s.split("\",\"").map { v =>
            val vs = v.replaceAll("\\\"", "").split(":")
            val keys = vs(0).split("_")
            if(keys.length > 2){
              Some(Data(a, decode, region, keys(0), keys(2), vs(1).replace(",", "").toDouble))
            } else {
              println("WARN data:"+v)
              None
            }
        }.filter(_.isDefined).map(_.get)

        Data.insert(values)
      }
    }

    case UnitValue(dbcode: String, result: JsValue) => {
      val array = result.asInstanceOf[JsArray].value
      array.foreach{ elem =>
        val indexId = elem \ "id"
        val unit = elem \ "unit"
        if(Index.getIndex(dbcode, indexId).map(_.unit.isEmpty).getOrElse(false)) {
          Index.updateUnit(dbcode,indexId,unit)
        }
      }
    }
  }
}
