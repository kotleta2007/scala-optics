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
