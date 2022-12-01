package interf_based

import scala.util.Try
import scala.reflect.TypeTest

object TraitTypes {
  trait Node:
    def value: String

  trait Uri extends Node

  trait Factory:
    def mkUri(u: String): Uri

  object aFactory extends Factory:
    def mkUri(u: String): Uri =
      new Uri { def value = u }
}

object TraitBasedRDF extends generic.RDF:
  import generic.*
  import interf_based.TraitTypes as tz

  override opaque type rNode <: Matchable = tz.Node
  override opaque type rURI <: rNode = tz.Uri
  override opaque type Node <: rNode = tz.Node
  override opaque type URI <: Node & rURI = tz.Uri

  given rops: generic.ROps[R] with
    override def mkUri(str: String): Try[RDF.URI[R]] = Try(
      tz.aFactory.mkUri(str)
    )
    override protected def nodeVal(node: RDF.Node[R]): String = node.value
    override def auth(uri: RDF.URI[R]): Try[String] =
      Try(java.net.URI.create(nodeVal(uri)).getAuthority())

end TraitBasedRDF

@main def run =
  val test = generic.Test[TraitBasedRDF.type]
  println(test.x)
