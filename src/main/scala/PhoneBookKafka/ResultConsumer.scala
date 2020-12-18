package PhoneBookKafka

import java.util
import java.util.Properties
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.jdk.CollectionConverters.iterableAsScalaIterableConverter

object ResultConsumer extends App {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "something")

  val consumer = new KafkaConsumer[String, String](props)
  val TOPIC = "filter-result"

  consumer.subscribe(util.Collections.singletonList(TOPIC))

  println(s"Subscribed to topic: '$TOPIC'")
  println("Start polling")
  println()
  println()

  while (true) {
    val records = consumer.poll(100)
    for (record <- records.asScala) {
      println(record.value())
    }
  }
}
