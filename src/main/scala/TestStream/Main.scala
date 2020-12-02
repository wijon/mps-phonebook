package TestStream

import scala.language.postfixOps

object Main {
  def main(args: Array[String]): Unit = {
    println("Stream (deprecated):")
    val test = (Stream.from(7,7) take 10).toList
    test.foreach(println(_))

    println("")
    println("LazyList:")
    val test2 = LazyList.from(7,7) take 10
    test2.foreach(println(_))
  }
}
