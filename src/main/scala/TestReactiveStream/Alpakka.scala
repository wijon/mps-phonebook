package TestReactiveStream

import java.nio.file.{FileSystem, FileSystems}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.file.scaladsl.FileTailSource
import akka.stream.scaladsl.Source

import scala.concurrent.duration.DurationInt

object Alpakka {
  def main(args: Array[String]): Unit = {
    val filename = "Berlin.csv"
    val basePath = "src\\main\\scala\\PhoneBookStream\\data\\"

    println("Reading " + filename)

    implicit val system: ActorSystem = ActorSystem()

    val fs: FileSystem = FileSystems.getDefault
    val lines: Source[String, NotUsed] = FileTailSource.lines(
      path = fs.getPath(basePath + filename),
      maxLineSize = 8192,
      pollingInterval = 250.millis
    )

    lines.runForeach(line => println(line))
  }
}
