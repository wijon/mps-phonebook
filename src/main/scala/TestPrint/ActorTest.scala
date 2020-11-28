package TestPrint

import akka.actor.{ActorSystem, Props}

import scala.io.StdIn

object ActorTest {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("demo-system")
    val printer = system.actorOf(Props(new Printer), "printer")
    printer ! "TestPrint.Printer 1!"

    val printer2 = system.actorOf(Props(new Printer), "printer2")
    printer2 ! "TestPrint.Printer 2!"

    val printer3 = system.actorOf(Props(new Printer), "printer3")
    printer3 ! "TestPrint.Printer 3!"

    val printer4 = system.actorOf(Props(new Printer), "printer4")
    printer4 ! "TestPrint.Printer 4!"

    val printer5 = system.actorOf(Props(new Printer), "printer5")
    printer5 ! "TestPrint.Printer 5!"

    System.out.println("Programm wartet...")
    StdIn.readLine()
  }
}
