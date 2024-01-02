import monocle.Iso
import monocle.macros.GenIso

// Iso on custom classes

case class Person(name: String, age: Int)

val personToTuple = Iso[Person, (String, Int)](p => (p.name, p.age)){case (name, age) => Person(name, age)}

personToTuple.get(Person("Zoe", 25))
personToTuple.reverseGet(("Zoe", 25))
personToTuple(("Zoe", 25))

// Iso on collections

def listToVector[A] = Iso[List[A], Vector[A]](_.toVector)(_.toList)
listToVector.get(List(1, 2, 3))

def vectorToList[A] = listToVector[A].reverse
vectorToList.get(Vector(1, 2, 3))

val stringToList = Iso[String, List[Char]](_.toList)(_.mkString)
stringToList.modify(_.tail)("Hello")

// Iso generation

case class MyString(s: String)
case class Foo()
case object Bar

GenIso[MyString, String].get(MyString("Hello"))
GenIso.unit[Foo].reverseGet(())
GenIso.unit[Bar.type].reverseGet(())
GenIso.fields[Person].get(Person("John", 42)) == ("John", 42)

// .modify() and .replace()
val victor = Person("Victor", 51)

val increaseAge = personToTuple.modify{case (name, value) => (name, value + 1)}
increaseAge(victor)

val transformIntoCornelius = personToTuple.replace(("Cornelius", 100))
transformIntoCornelius(victor)

// Testing Isos
val isoLaws       = monocle.law.IsoLaws(personToTuple)
val personForTest = victor
val tupleForTest  = isoLaws.iso.get(personForTest)

val oneWayTest = isoLaws.roundTripOneWay(personForTest)
assert(oneWayTest.lhs == oneWayTest.rhs)

val otherWayTest = isoLaws.roundTripOtherWay(tupleForTest)
assert(otherWayTest.lhs == otherWayTest.rhs)
