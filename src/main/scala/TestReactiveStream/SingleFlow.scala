package TestReactiveStream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

object SingleFlow {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    val code = Array(141, 201, 207, 201, 209, 217, 201, 227, 63, 133, 221, 199, 201)

    println("Vor der Entschlüsselung: ")
    code.foreach(x => print(x.toChar))
    println()
    println("Nach der Entschlüsselung: ")

    val source = Source(code)
    val flow: Flow[Int, Char, NotUsed] = Flow[Int].map(x => ((x + 1) / 2).toChar)
    val sink = Sink.foreach[Char](print)

    val graph = source.via(flow).to(sink)
    graph.run()
  }
}
