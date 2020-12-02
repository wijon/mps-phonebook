package PhoneBook

import java.io.File

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.language.postfixOps
import scala.util.control.Breaks.{break, breakable}

object Main {
  private def getFileList(dir: String): Array[String] = {
    val file = new File(dir)
    file.listFiles
      .filter(_.isFile)
      .filter(_.getName.endsWith(".csv"))
      .map(_.getPath)
  }

  private def createActorForFile(system: ActorSystem, filePath: String): ActorRef = {
    val actorName = filePath.split("\\\\").last.split("\\.")(0)
    system.actorOf(Props(PhoneBookActor(filePath)), actorName)
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("demo-system")
    val filePaths = getFileList("src\\main\\scala\\PhoneBook\\data")

    val phoneBookActors = filePaths.map(fp => createActorForFile(system, fp))

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
          case "5" => println("Geben Sie ein Suchwort ein:")
          case _ =>
            println("Falsche Eingabe!")
            break // Continue
        }

        val pattern = StdIn.readLine()
        var messageType: SearchMessage = null

        input match {
          case "1" => messageType = SearchFirstname(pattern)
          case "2" => messageType = SearchLastname(pattern)
          case "3" => messageType = SearchStreetName(pattern)
          case "4" => messageType = SearchCity(pattern)
          case "5" => messageType = SearchAll(pattern)
        }

        implicit val timeout: Timeout = Timeout(5 seconds)
        val futureSequenceResults = Future.sequence(phoneBookActors.map(actor => actor ? messageType).toVector)
        val results = Await.result(futureSequenceResults, 5 seconds)

        println()

        results.foreach(r => {
          r.asInstanceOf[Vector[String]].foreach(println(_))
        })

        println()
        println()
      }
    }
  }
}
