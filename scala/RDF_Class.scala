package class_based

import scala.util.Try
import scala.reflect.TypeTest

object ClassTypes {
  abstract class Node:
    def value: String

  abstract class Uri extends Node

  trait Factory:
    def mkUri(u: String): Uri

  object aFactory extends Factory:
    def mkUri(u: String): Uri =
      new Uri { def value = u }
}

object ClassRDF extends generic.RDF:
  import generic.*
  import class_based.ClassTypes as cz

  override opaque type rNode <: Matchable = cz.Node
  override opaque type rURI <: rNode = cz.Uri
  override opaque type Node <: rNode = cz.Node
  override opaque type URI <: Node & rURI = cz.Uri

  given rops: generic.ROps[R] with
    override def mkUri(str: String): Try[RDF.URI[R]] = Try(
      cz.aFactory.mkUri(str)
    )
    override protected def nodeVal(node: RDF.Node[R]): String = node.value
    def auth(uri: RDF.URI[R]): Try[String] = 
      Try(java.net.URI.create(nodeVal(uri)).getAuthority())

end ClassRDF

@main def run =
  val test = generic.Test[ClassRDF.type]
  println(test.x)
