package RockPaperScissors

import akka.actor.Actor

class Ki extends Actor {
  override def receive: Receive = {
    case _ => sender ! scala.util.Random.nextInt(2)
  }
}