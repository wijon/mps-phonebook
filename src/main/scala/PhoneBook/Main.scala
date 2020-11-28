package PhoneBook

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.language.postfixOps
import scala.util.{Failure, Success}
import scala.util.control.Breaks.{break, breakable}

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("demo-system")
    implicit val timeout: Timeout = Timeout(5 seconds)

    val phoneBookTuttlingen = system.actorOf(Props(PhoneBookActor("src\\main\\scala\\PhoneBook\\Tuttlingen.csv")), "pbTut")
    val phoneBookRottweil = system.actorOf(Props(PhoneBookActor("src\\main\\scala\\PhoneBook\\Rottweil.csv")), "pbRw")
    val phoneBookKonstanz = system.actorOf(Props(PhoneBookActor("src\\main\\scala\\PhoneBook\\Konstanz.csv")), "pbKn")

    val phoneBookActors = Vector(phoneBookTuttlingen, phoneBookRottweil, phoneBookKonstanz)

    System.out.println("Was möchten Sie tun?")
    System.out.println("(1) nach einem Vorname suchen")
    System.out.println("(2) nach einem Nachname suchen")
    System.out.println("(3) nach einer PLZ suchen")
    System.out.println("(4) nach einem Ortsnamen suchen")
    System.out.println("(5) alle Daten durchsuchen")

    while (true) {
      breakable {
        val input = StdIn.readLine()

        input match {
          case "1" => println("Geben Sie einen Vornamen ein:")
          case "2" => println("Geben Sie einen Nachnamen ein:")
          case "3" => println("Geben Sie eine PLZ ein:")
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
          case "3" => messageType = SearchPostcode(pattern.toInt)
          case "4" => messageType = SearchCity(pattern)
          case "5" => messageType = SearchAll(pattern)
        }

        phoneBookActors.foreach(actor => (actor ? messageType).andThen(
          r => {
            if (r.isSuccess)
              r.get.asInstanceOf[Vector[String]].foreach(println(_))
          }))

        //        Await.result(Future.sequence(phoneBookActors.map(actor => actor ? messageType)), 10 seconds)
        //          .foreach(results => )
        //
        //
        //
        //        Future.sequence(phoneBookActors.map(actor => actor ? messageType))
        //          .andThen(result => result match {
        //            case Success(value) => value.foreach(res => println(res))
        //            case Failure(value) => println("Suche fehlgeschlagen")
        //          })
      }
    }
  }
}
