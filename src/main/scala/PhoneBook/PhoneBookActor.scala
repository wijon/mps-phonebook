package PhoneBook

import akka.actor.Actor

import scala.io.Source

case class PhoneBookActor(filename: String) extends Actor{
  def receive: Receive = {
    case SearchFirstname(firstname) =>
      val bufferedSource = Source.fromFile(filename)
      val returnValues = bufferedSource.getLines().toVector.filter(r => r.split("\\t")(0).contains(firstname))
      bufferedSource.close
      sender ! returnValues
    case SearchLastname(lastname) =>
      val bufferedSource = Source.fromFile(filename)
      val returnValues = bufferedSource.getLines().toVector.filter(r => r.split("\\t")(1).contains(lastname))
      bufferedSource.close
      sender ! returnValues
    case SearchPostcode(postcode) =>
      val bufferedSource = Source.fromFile(filename)
      val returnValues = bufferedSource.getLines().toVector.filter(r => r.split("\\t")(2).equals(postcode.toString))
      bufferedSource.close
      sender ! returnValues
    case SearchCity(city) =>
      val bufferedSource = Source.fromFile(filename)
      val returnValues = bufferedSource.getLines().toVector.filter(r => r.split("\\t")(3).contains(city))
      bufferedSource.close
      sender ! returnValues
    case SearchAll(searchString) =>
      val bufferedSource = Source.fromFile(filename)
      val returnValues = bufferedSource.getLines().toVector.filter(_.contains(searchString))
      bufferedSource.close
      sender ! returnValues
  }
}
