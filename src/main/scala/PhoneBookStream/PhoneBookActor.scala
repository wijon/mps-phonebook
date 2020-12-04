package PhoneBookStream

import java.io.File
import java.nio.file.{Path, Paths}

import akka.Done
import akka.actor.{Actor, ActorSystem}
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.FileIO

import scala.language.postfixOps

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.io.Source

case class PhoneBookActor(filename: String) extends Actor {
  def receive: Receive = {
    case SearchFirstname(firstname) =>
      val file = new File(filename)

      val future = processCsvFile(file, "Name", firstname)
      val results = Await.result(future, 5 seconds)
      sender ! results

    //      val bufferedSource = Source.fromFile(filename)
    //      val returnValues = bufferedSource.getLines().toVector.filter(r => r.split("\\|")(0).contains(firstname))
    //      bufferedSource.close
    //      sender ! returnValues
    case SearchLastname(lastname) =>
      val bufferedSource = Source.fromFile(filename)
      val returnValues = bufferedSource.getLines().toVector.filter(r => r.split("\\|")(1).contains(lastname))
      bufferedSource.close
      sender ! returnValues
    case SearchStreetName(streetName) =>
      val bufferedSource = Source.fromFile(filename)
      val returnValues = bufferedSource.getLines().toVector.filter(r => r.split("\\|")(2).equals(streetName))
      bufferedSource.close
      sender ! returnValues
    case SearchCity(city) =>
      val bufferedSource = Source.fromFile(filename)
      val returnValues = bufferedSource.getLines().toVector.filter(r => r.split("\\|")(3).contains(city))
      bufferedSource.close
      sender ! returnValues
    case SearchAll(searchString) =>
      val bufferedSource = Source.fromFile(filename)
      val returnValues = bufferedSource.getLines().toVector.filter(_.contains(searchString))
      bufferedSource.close
      sender ! returnValues
  }

  def processCsvFile(inputFile: File, filterCol: String, pattern: String): Future[Vector[String]] = {
    val inputFileName = inputFile.getName
    val csvFile: Path = Paths.get(inputFile.getPath)
    val source = FileIO.fromPath(csvFile)

    implicit val system: ActorSystem = context.system

    val results: mutable.HashSet[String] = mutable.HashSet.empty

    source
      .via(CsvParsing.lineScanner('|'))
      .via(CsvToMap.toMapAsStrings())
      .filter(row => row.getOrElse(filterCol, "") == pattern)
      .runForeach(t => results += t.values.mkString("|"))
      .map { _ =>
        println(s"Successfully processed the file $inputFileName")
        results.toVector
      }
      .recover {
        case _: Exception => println(s"Error in processing the file $inputFileName")
          Vector.empty
      }
  }
}
