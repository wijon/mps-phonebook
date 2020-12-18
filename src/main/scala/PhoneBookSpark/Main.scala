package PhoneBookSpark

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import java.io.File
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
    val spark = SparkSession.builder.appName("Spark with Kafka").config("spark.master", "local").getOrCreate()
    val sc = new StreamingContext(spark.sparkContext, Seconds(1))

    // This is to provide SPARK from printing so much garbage
    // Still not recommended... Without the garbage printing, the shell locks for multiple seconds without any indicator, why :P
    //spark.sparkContext.setLogLevel("ERROR")

    val files = getFiles("src\\main\\scala\\PhoneBookSpark\\data")

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
        if (input.toInt > 4) {
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

        val kafkaParams = Map[String, Object](
          "bootstrap.servers" -> "localhost:9092",
          "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
          "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
          "group.id" -> "please_do_work",
          "auto.offset.reset" -> "latest",
          "enable.auto.commit" -> (false: java.lang.Boolean)
        )

        val topics = Array("filter-result")
        val stream = KafkaUtils.createDirectStream[String, String](
          sc,
          PreferConsistent,
          Subscribe[String, String](topics, kafkaParams)
        )

        stream.foreachRDD(rdd => println(rdd.toString()))

        // Kann man so eine Nachricht schicken?!
        val arrayRDD = spark.sparkContext.parallelize(Array(1,2,3,4,5,6,7,8))

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
