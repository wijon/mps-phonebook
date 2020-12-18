package TestSpark

import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.appName("Simple Application").config("spark.master", "local").getOrCreate()

    val logData = spark.read.textFile("src\\main\\scala\\TestSpark\\Text.txt").cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()

    println(s"Lines with a: $numAs, Lines with b: $numBs")

    spark.stop()
  }
}
