package actors

import akka.actor.{Props, Actor}
import play.api.libs.ws.{Response, WS}

import scala.util.{Try, Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.{JsArray, JsNull}

case class InitGet(dimension:String, dbcode:String,count:Int = 0)
case class IndexPost(dimension:String,dbcode:String,code:String,level:String,count:Int = 0)
case class DataGet(a:String,decode:String,index:String,region:String,time:String,selectId:String,third:String,count:Int=0)

object WGetActor {
  def props(): Props = Props(classOf[WGetActor])
}

class WGetActor extends Actor {

  val base = "http://data.stats.gov.cn"

  override def receive: Actor.Receive = {

    case InitGet(dimension,dbcode,count) => {
      val getFuture = WS.url(base + s"/region/init?dbcode=${dbcode}&dimension=$dimension").get()
      getFuture.onComplete{
        case Success(res:Response) => {
          val dataActor = context.actorOf(InitActor.props(context.self))
          Try(res.json) match {
            case Success(json)  => dataActor ! InitValue(dimension,dbcode,json)
            case Failure(_) => if(count < 3) {context.self ! InitGet(dimension,dbcode,count + 1)}
          }

        }
        case Failure(e) => println(e)
      }
    }

    case IndexPost(dimension,dbcode,code,level,count) => {
      val postFuture = WS.url(base + "/quotas/getchildren")
        .withHeaders("Content-Type"->"application/x-www-form-urlencoded; charset=UTF-8 application/x-www-form-urlencoded")
        .post(s"dbcode=${dbcode}&dimension=${dimension}&code=${code}&level=${level}")

      postFuture.onComplete{
        case Success(res:Response) => {
          val dataActor = context.actorOf(IndexActor.props())
          Try(res.json) match {
            case Success(json) => dataActor ! IndexValue(dimension,dbcode, res.json)
            case Failure(_) => if(count < 3) {context.self !  IndexPost(dimension,dbcode,code,level,count + 1)}
          }

        }
        case Failure(e) => println(e)
      }
    }

    case DataGet(a,m,index,region,time,selectId,third,count) => {
      println(DataGet(a,m,index,region,time,selectId,third,count))
      val getFuture = WS.url(base + s"/workspace/index?a=${a}&m=${m}&index=${index}&region=${region}&time=${time}&selectId=${selectId}&third=${third}").get()
      getFuture.onComplete{
        case Success(res:Response) => {
          val dataActor = context.actorOf(DataActor.props())
          Try(res.json) match {
            case Success(json) => {
              dataActor ! DataValue(a,m,region, res.json \ "tableData")
              dataActor ! UnitValue(m,res.json \ "value" \ "index")
            }
            case Failure(_) => if(count < 3) {context.self ! DataGet(a,m,index,region,time,selectId,third,count + 1)}
          }

        }
        case Failure(e) => println(e)
      }
    }

  }

}
