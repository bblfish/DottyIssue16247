package interf_based

import scala.util.Try
import scala.reflect.TypeTest

object TraitTypes {
  trait Node:
    def value: String

  class Uri(u: String) extends Node:
    def value = u

  def mkUri(u: String): Uri = new Uri(u)
}

object TraitRDF extends generic.RDF:
  import generic.*
  import interf_based.TraitTypes as tz

  override opaque type rNode <: Matchable = tz.Node
  override opaque type rURI <: rNode = tz.Uri
  override opaque type Node <: rNode = tz.Node
  override opaque type URI <: Node & rURI = tz.Uri

  given rops: generic.ROps[R] with
    override def mkUri(str: String): Try[RDF.URI[R]] = Try(
      tz.mkUri(str)
    )
    override protected def nodeVal(node: RDF.Node[R]): String = node.value
    override def auth(uri: RDF.URI[R]): Try[String] =
      Try(java.net.URI.create(nodeVal(uri)).getAuthority())

end TraitRDF

@main def run =
  val test = generic.Test[TraitRDF.type]
  println(test.x)
