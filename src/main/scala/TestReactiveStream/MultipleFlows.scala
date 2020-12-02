package TestReactiveStream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.GraphDSL.Implicits.{SourceShapeArrow, port2flow}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, ZipWith}

object MultipleFlows {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()

    val graph = GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      val input = builder.add(Source(1 to 10))
      val multiplier1 = builder.add(Flow[Int].map(x => x * 1))
      val multiplier2 = builder.add(Flow[Int].map(x => x * 2))
      val multiplier3 = builder.add(Flow[Int].map(x => x * 3))
      val multiplier4 = builder.add(Flow[Int].map(x => x * 4))
      val multiplier5 = builder.add(Flow[Int].map(x => x * 5))
      val multiplier6 = builder.add(Flow[Int].map(x => x * 6))
      val multiplier7 = builder.add(Flow[Int].map(x => x * 7))
      val multiplier8 = builder.add(Flow[Int].map(x => x * 8))
      val multiplier9 = builder.add(Flow[Int].map(x => x * 9))
      val multiplier10 = builder.add(Flow[Int].map(x => x * 10))
      val output = builder.add(Sink.foreach[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)](println))

      val broadcast = builder.add(Broadcast[Int](10))
      val zip = builder.add(ZipWith[Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]((a, b, c, d, e, f, g, h, i, j) => (a, b, c, d, e, f, g, h, i, j)))

      input ~> broadcast
      broadcast.out(0) ~> multiplier1 ~> zip.in0
      broadcast.out(1) ~> multiplier2 ~> zip.in1
      broadcast.out(2) ~> multiplier3 ~> zip.in2
      broadcast.out(3) ~> multiplier4 ~> zip.in3
      broadcast.out(4) ~> multiplier5 ~> zip.in4
      broadcast.out(5) ~> multiplier6 ~> zip.in5
      broadcast.out(6) ~> multiplier7 ~> zip.in6
      broadcast.out(7) ~> multiplier8 ~> zip.in7
      broadcast.out(8) ~> multiplier9 ~> zip.in8
      broadcast.out(9) ~> multiplier10 ~> zip.in9
      zip.out ~> output

      ClosedShape
    }

    println("Kleines 1x1")
    RunnableGraph.fromGraph(graph).run()
  }
}
