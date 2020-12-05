package TestReactiveStream

import java.io.File

import akka.actor.ActorSystem
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Sink}

object AlpakkaMultipleFiles {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()

    val basePath = "src\\main\\scala\\PhoneBookStream\\data\\"
    val file1 = new File(basePath + "Konstanz.csv")
    val file2 = new File(basePath + "Muenchen.csv")

    val sources = Vector(FileIO.fromPath(file1.toPath), FileIO.fromPath(file2.toPath))

    val sink = Sink.foreach[Map[String, String]](x => println(x.values.mkString("|")))

    sources.foreach(source => source
      .via(CsvParsing.lineScanner('|'))
      .via(CsvToMap.toMapAsStrings())
      .filter(row => row.getOrElse("Vorname", "") == "Tarik")
      .to(sink)
      .run())
  }
}
