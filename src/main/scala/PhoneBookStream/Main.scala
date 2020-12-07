package PhoneBookStream

import java.io.File

import akka.actor.ActorSystem
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Sink}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.io.StdIn
import scala.language.postfixOps
import scala.util.control.Breaks.{break, breakable}



object Main {
  private def getFiles(dir: String): Array[File] = {
    val file = new File(dir)
    file.listFiles
      .filter(_.isFile)
      .filter(_.getName.endsWith(".csv"))
  }

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    val files = getFiles("src\\main\\scala\\PhoneBookStream\\data")
    val sources = files.map(f => FileIO.fromPath(f.toPath))
    val sink = Sink.foreach[Map[String, String]](x => println(x.values.mkString("|")))

    while (true) {
      System.out.println("Was möchten Sie tun?")
      System.out.println("(1) nach einem Vorname suchen")
      System.out.println("(2) nach einem Nachname suchen")
      System.out.println("(3) nach einer Strasse suchen")
      System.out.println("(4) nach einem Ortsnamen suchen")
      System.out.println("(5) alle Daten durchsuchen")

      breakable {
        val input = StdIn.readLine()

        input match {
          case "1" => println("Geben Sie einen Vornamen ein:")
          case "2" => println("Geben Sie einen Nachnamen ein:")
          case "3" => println("Geben Sie eine Strasse ein:")
          case "4" => println("Geben Sie einen Ortsnamen ein:")
          case _ =>
            println("Falsche Eingabe!")
            break // Continue
        }

        val pattern = StdIn.readLine()
        var column: String = null

        input match {
          case "1" => column = "Name"
          case "2" => column = "Surname"
          case "3" => column = "Street"
          case "4" => column = "City"
        }

        val futures = sources.map(source => source
          .via(CsvParsing.lineScanner('|'))
          .via(CsvToMap.toMapAsStrings())
          .filter(row => row.getOrElse(column, "") == pattern)
          .runWith(Sink.fold(List[String]())((list, m) => list :+ m.values.mkString("|")))
        )

        val futureSequenceResults = Future.sequence(futures.toVector)
        val results = Await.result(futureSequenceResults, 5 seconds)

        println()

        results.foreach(r => {
          r.foreach(println(_))
        })

        println()
        println()
      }
    }
  }
}
