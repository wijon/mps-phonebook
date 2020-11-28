package PhoneBook

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.language.postfixOps

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("demo-system")
    implicit val timeout: Timeout = Timeout(5 seconds)

    val phoneBookTuttlingen = system.actorOf(Props(PhoneBookActor("C:\\Users\\weber_jon\\IdeaProjects\\ScalaActors\\src\\main\\scala\\PhoneBook\\Tuttlingen.csv")), "pbTut")
    val phoneBookRottweil = system.actorOf(Props(PhoneBookActor("C:\\Users\\weber_jon\\IdeaProjects\\ScalaActors\\src\\main\\scala\\PhoneBook\\Rottweil.csv")), "pbRw")
    val phoneBookKonstanz = system.actorOf(Props(PhoneBookActor("C:\\Users\\weber_jon\\IdeaProjects\\ScalaActors\\src\\main\\scala\\PhoneBook\\Konstanz.csv")), "pbKn")

    val phoneBookActors = Vector(phoneBookTuttlingen, phoneBookRottweil, phoneBookKonstanz)

    System.out.println("Was möchten sie tun?")
    System.out.println("(1) nach einem Vorname suchen")
    System.out.println("(2) nach einem Nachname suchen")
    System.out.println("(3) nach einer PLZ suchen")
    System.out.println("(4) nach einem Ortsnamen suchen")
    System.out.println("(5) alle Daten durchsuchen")

    while (true) {
      val input = StdIn.readLine()
      val inputParts = input.split("[()]")

      inputParts(1) match {
        case "1" => phoneBookActors.foreach(actor => (actor ? SearchFirstname(inputParts(2))).andThen(
          r => if (r.isSuccess) r.get.asInstanceOf[Vector[String]].foreach(println(_))))
        case "2" => phoneBookActors.foreach(actor => (actor ? SearchLastname(inputParts(2))).andThen(
          r => if (r.isSuccess) r.get.asInstanceOf[Vector[String]].foreach(println(_))))
        case "3" => phoneBookActors.foreach(actor => (actor ? SearchPostcode(inputParts(2).toInt)).andThen(
          r => if (r.isSuccess) r.get.asInstanceOf[Vector[String]].foreach(println(_))))
        case "4" => phoneBookActors.foreach(actor => (actor ? SearchCity(inputParts(2))).andThen(
          r => if (r.isSuccess) r.get.asInstanceOf[Vector[String]].foreach(println(_))))
        case "5" => phoneBookActors.foreach(actor => (actor ? SearchAll(inputParts(2))).andThen(
          r => if (r.isSuccess) r.get.asInstanceOf[Vector[String]].foreach(println(_))))
      }
    }
  }
}
