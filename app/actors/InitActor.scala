package actors

import play.api.libs.json.{JsArray, JsValue}
import akka.actor.{ActorRef, Actor, Props}
import models._
import actors.IndexValue
import actors.DataGet
import play.api.libs.json.JsArray
import actors.InitValue

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
            fetchData("l",dbcode,"000000",indexId,date)
          }
        } else if (dbcode == "fsndks" | dbcode == "fsjdks" | dbcode == "fsydks") {
          for {
            indexId <- Index.fetchDataIndexId(dbcode)
            date <- inits.map(_.value)
            region <- Index.fetchRegion()
          } {
            fetchData("l",dbcode,region.id,indexId,date)
          }
          println(dbcode)
        }

      } else if (dimension == zb.toString) {
        indexActor ! IndexValue(dimension, dbcode, result)
      }

    }
  }

  private def fetchData(a:String, dbcode:String,regionId:String,indexId:String,date:String):Unit = {
    if(Data.isNotExist(Data("l",dbcode,regionId,indexId,date,0))) {
      wsActor ! DataGet("l", dbcode, indexId, regionId, s"-1,$date",regionId, "region")
    }
  }

}
