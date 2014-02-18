package actors

import akka.actor.{Props, Actor}
import play.api.libs.json.{JsArray, JsValue}
import models.Index

case class IndexValue(dimension: String,dbcode: String, result:JsValue)

object IndexActor {
  def props(): Props = Props(classOf[IndexActor])
}

class IndexActor extends Actor {
  val wsActor = context.actorOf(WGetActor.props())

  implicit def js2String(js: JsValue):String = js.toString().replaceAll("\"","")

  override def receive: Actor.Receive = {
    case IndexValue(dimension,dbcode,result) => {
      val array = result.asInstanceOf[JsArray].value
      def ifData(js: JsValue): Option[Int] = {

        if(!(js \\ "ifData").isEmpty) {
          val v =  js \ "ifData"
          Some(v.replaceAll("\"","").toInt)
        } else {
          None
        }
      }
      def isParent(js: JsValue) = (js \ "isParent").toString() == "true"
      val indexs = array.map(v => Index(dbcode, v \ "id", v \ "pId", v \ "name", isParent(v),ifData(v)))
      Index.insert(indexs)

      array.foreach{ js =>
        if(ifData(js) != Some(1)) {
          wsActor ! IndexPost(dimension,dbcode, js \ "id", "2")
        }
      }
    }

  }
}
