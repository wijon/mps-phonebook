package PhoneBookSpark

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

object ResultConsumer extends App {
  val TOPIC = "filter-result"

  val spark = SparkSession.builder.appName("Spark with Kafka").config("spark.master", "local").getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")
  val sc = new StreamingContext(spark.sparkContext, Seconds(1))

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
    "group.id" -> "something"
  )

  val topics = Array(TOPIC)
  val stream = KafkaUtils.createDirectStream[String, String](
    sc,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams)
  )

  println(s"Subscribed to topic: '$TOPIC'")
  println("Start polling")
  println()
  println()

//  stream.foreachRDD(rdd => rdd.foreach(cr => println(cr.value)))  // Prints results
  stream.foreachRDD(rdd => {
    val count = rdd.count()
    if(count > 0)
      println(count)
  })  // Prints result count

  sc.start
  sc.awaitTermination
}
