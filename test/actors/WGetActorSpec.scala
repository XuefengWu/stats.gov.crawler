package actors

import org.specs2.mutable.Specification
import akka.actor.{Props, ActorSystem}

import play.api.test._
import play.api.test.Helpers._

class WGetActorSpec extends Specification {
  "wget" should {

    val actor = ActorSystem("MyActors").actorOf(Props[WGetActor])

    "get init" in {
      actor ! InitGet("sj", "hgjdks")
      1 === 1
    }

  }
}

object WGetActorMain extends App {
  running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
    val actor = ActorSystem("MyActors").actorOf(Props[WGetActor])
    actor ! InitGet("sj", "hgjdks")
    actor ! IndexPost("zb", "hgydks", "A1201", "2")
    actor ! DataGet("l", "hgndks", "A050101", "000000", "-1,1983", "000000", "region")
  }
}