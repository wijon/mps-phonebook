package PhoneBookSpark

import akka.actor.ActorSystem
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Sink}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.sql.SparkSession

import java.io.File
import java.util.Properties
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
    val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()

    // This is to provide SPARK from printing so much garbage
    // Still not recommended... Without the garbage printing, the shell locks for multiple seconds without any indicator, why :P
    //spark.sparkContext.setLogLevel("ERROR")

    implicit val system: ActorSystem = ActorSystem()
    val files = getFiles("src\\main\\scala\\PhoneBookKafka\\data")
    val sources = files.map(f => FileIO.fromPath(f.toPath))

    while (true) {
      System.out.println("Was möchten Sie tun?")
      System.out.println("(1) nach einem Vorname suchen")
      System.out.println("(2) nach einem Nachname suchen")
      System.out.println("(3) nach einer Strasse suchen")
      System.out.println("(4) nach einem Ortsnamen suchen")
      System.out.println("(5) SPARK ✨✨✨✨ Zeilen zählen")
      System.out.println("(6) SPARK ✨✨✨✨ Zeilen anhand eines Wortes zählen")

      breakable {
        val input = StdIn.readLine()

        input match {
          case "1" => println("Geben Sie einen Vornamen ein:")
          case "2" => println("Geben Sie einen Nachnamen ein:")
          case "3" => println("Geben Sie eine Strasse ein:")
          case "4" => println("Geben Sie einen Ortsnamen ein:")
          case "5" => println("Geben Sie einen Stadtnamen ein:")
          case "6" => println("Geben Sie einen Stadtnamen ein:")
          case _ =>
            println("Falsche Eingabe!")
            break
        }

        val pattern = StdIn.readLine()
        var column: String = null

        // SPARK only ✨✨✨✨
        if(input.toInt > 4) {
          val filename = pattern + ".csv"
          val textFile = spark.read.textFile("src\\main\\scala\\PhoneBookSpark\\data\\" + filename)

          input match {
            case "5" => System.out.println("Anzahl Zeilen in " + filename + ": " + textFile.count())
            case "6" =>
              println("Geben Sie ein Wort ein:")
              val wordToCount = StdIn.readLine()
              System.out.println("Anzahl Zeilen in " + filename + ", die " + wordToCount + " beinhalten: " + textFile.filter(line => line.contains(wordToCount)).count())
          }

          println()
          println()
          break
        }

        input match {
          case "1" => column = "Name"
          case "2" => column = "Surname"
          case "3" => column = "Street"
          case "4" => column = "City"
          case _ =>
            break
        }

        val props = new Properties()
        props.put("bootstrap.servers", "localhost:9092")
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

        val producer = new KafkaProducer[String, String](props)

        val TOPIC = "filter-result"
        val KEY = "result"

        val futures = sources.map(source => source
          .via(CsvParsing.lineScanner('|'))
          .via(CsvToMap.toMapAsStrings())
          .filter(row => row.getOrElse(column, "") == pattern)
          .runWith(Sink.foreach(map => {
            val result = map.values.mkString("|")
            val record = new ProducerRecord(TOPIC, KEY, result)
            producer.send(record)
          }))
        )

        val futureSequenceResults = Future.sequence(futures.toVector)
        Await.result(futureSequenceResults, 5 seconds)

        println()
        println()
      }
    }

    // Steht hier nur der Vollständigkeit halber... wird sowieso nie erreicht 🤷‍
    // Lustig, dass man im Quellcode Emojis verwenden kann 😅
    // Auch wenn man sie nicht richtig erkennen kann... 🤦‍
    spark.stop()
  }
}
