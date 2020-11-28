package TestPrint

import akka.actor.Actor

class Printer extends Actor {
  override def receive: Receive = {
    case message => println(message)
  }
}