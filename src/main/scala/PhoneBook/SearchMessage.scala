package PhoneBook

trait SearchMessage

case class SearchFirstname(firstname: String) extends SearchMessage
case class SearchLastname(lastname: String) extends SearchMessage
case class SearchPostcode(postcode: Int) extends SearchMessage
case class SearchCity(city: String) extends SearchMessage
case class SearchAll(searchString: String) extends SearchMessage