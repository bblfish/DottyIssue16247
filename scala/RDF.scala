package generic

import scala.util.Try
import RDF.URI

trait RDF:
  rdf =>

  type R = rdf.type
  type rNode <: Matchable
  type rURI <: rNode
  type Node <: Matchable
  type URI <: Node & rURI

  given rops: ROps[R]
end RDF

object RDF:

  type rNode[R <: RDF] = R match
    case GetRelativeNode[n] => n & Matchable

  type rURI[R <: RDF] = R match
    case GetRelativeURI[u] => u & rNode[R]

  type Node[R <: RDF] = R match
    case GetNode[n] => n & rNode[R]

  type URI[R <: RDF] <: Node[R] = R match
    case GetURI[u] => u & Node[R] & rURI[R]

  private type GetRelativeNode[N] = RDF { type rNode = N }
  private type GetNode[N] = RDF { type Node = N }
  private type GetRelativeURI[U] = RDF { type rURI = U }
  private type GetURI[U] = RDF { type URI = U }

end RDF

trait ROps[R <: RDF]:
  def mkUri(str: String): Try[RDF.URI[R]]
  def auth(uri: RDF.URI[R]): Try[String]

class Test[R <: RDF](using rops: ROps[R]):
  import rops.given
  val uriT: Try[RDF.URI[R]] = rops.mkUri("https://bblfish.net/#i")
  val x: String = "uri authority=" + uriT.map(u => rops.auth(u))


object TraitTypes:
  trait Node:
    def value: String

  trait Uri extends Node

  def mkUri(u: String): Uri =
    new Uri { def value = u }


object TraitRDF extends generic.RDF:
  import TraitTypes as tz

  override opaque type rNode <: Matchable = tz.Node
  override opaque type rURI <: rNode = tz.Uri
  override opaque type Node <: rNode = tz.Node
  override opaque type URI <: Node & rURI = tz.Uri

  given rops: generic.ROps[R] with
    override def mkUri(str: String): Try[RDF.URI[R]] = Try(
      tz.mkUri(str)
    )
    override def auth(uri: RDF.URI[R]): Try[String] =
      Try(java.net.URI.create(uri.value).getAuthority())

end TraitRDF

@main def run =
  val test = generic.Test[TraitRDF.type]
  println(test.x)
