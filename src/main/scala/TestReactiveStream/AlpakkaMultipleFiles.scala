package TestReactiveStream

import java.io.File

import akka.actor.ActorSystem
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.FileIO

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

object AlpakkaMultipleFiles {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()

    val basePath = "src\\main\\scala\\PhoneBookStream\\data\\"
    val file1 = new File(basePath + "Konstanz.csv")
    val file2 = new File(basePath + "Muenchen.csv")

    val source1 = FileIO.fromPath(file1.toPath)
    val source2 = FileIO.fromPath(file2.toPath)

    val results1: mutable.HashSet[String] = mutable.HashSet.empty
    val results2: mutable.HashSet[String] = mutable.HashSet.empty

    val future1 = source1
      .via(CsvParsing.lineScanner('|'))
      .via(CsvToMap.toMapAsStrings())
      .filter(row => row.getOrElse("Vorname", "") == "Jonas")
      .runForeach(t => results1 += t.values.mkString("|"))
      .map { _ => results1.toVector
      }
      .recover {
        case _: Exception => println(s"Error in processing file1")
          Vector.empty
      }

    val future2 = source2
      .via(CsvParsing.lineScanner('|'))
      .via(CsvToMap.toMapAsStrings())
      .filter(row => row.getOrElse("Vorname", "") == "Jonas")
      .runForeach(t => results2 += t.values.mkString("|"))
      .map { _ => results2.toVector
      }
      .recover {
        case _: Exception => println(s"Error in processing file1")
          Vector.empty
      }

    future1.andThen(r => r.get.foreach(println))
    future2.andThen(r => r.get.foreach(println))
  }
}
