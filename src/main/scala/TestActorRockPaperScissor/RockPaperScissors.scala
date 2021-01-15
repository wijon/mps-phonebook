package TestActorRockPaperScissor

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.language.postfixOps

object RockPaperScissors {
  def main(args: Array[String]): Unit = {
    System.out.println("Ihr Zug")
    val input = StdIn.readLine()

    val system = ActorSystem("demo-system")

    val ki = system.actorOf(Props(new Ki), "ki")

    implicit val timeout: Timeout = Timeout(5 seconds)
    val future = ki ? "Schere"

    // Mit Wait
    //    val result = Await.result(future, 5 seconds).asInstanceOf[String]
    //    println(result)

    // Ohne wait
    future.andThen(
      x => x.get match {
        case 0 =>
          println("KI sagt Schere")

          input match {
            case "Schere" => println("Unentschieden")
            case "Stein" => println("Gewonnen")
            case "Papier" => println("Verloren")
            case _ => println("Ungültige Eingabe")
          }
        case 1 =>
          println("KI sagt Stein")

          input match {
            case "Schere" => println("Verloren")
            case "Stein" => println("Unentschieden")
            case "Papier" => println("Gewonnen")
            case _ => println("Ungültige Eingabe")
          }
        case _ =>
          println("KI sagt Papier")

          input match {
            case "Schere" => println("Gewonnen")
            case "Stein" => println("Verloren")
            case "Papier" => println("Unentschieden")
            case _ => println("Ungültige Eingabe")
          }
      }
    )

    System.out.println("Programm wartet auf KI...")
    StdIn.readLine()
  }
}
