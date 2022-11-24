package interf_based

import scala.util.Try
import scala.reflect.TypeTest
import generic.*

// here we use interfaces written in Java 
// todo, put back example using traits only to see if Java is the problem
object TraitBasedRDF extends RDF:
  lazy val factory: rdfscala.ScalaMkNodes = rdfscala.SimpleScalaNodeFactory
  override opaque type rNode <: Matchable = rdfscala.TstNode
  override opaque type rURI <: rNode = rdfscala.URI
  override opaque type Node <: rNode = rdfscala.TstNode
  override opaque type URI <: Node & rURI = rdfscala.URI

  given rops: ROps[R] with
    override def mkUri(str: String): Try[RDF.URI[R]] = Try(
      factory.mkUri(str)
    )
    override protected def nodeVal(node: RDF.Node[R]): String = node.value()
    override protected def auth(uri: RDF.URI[R]): Try[String] = 
      Try(java.net.URI.create(nodeVal(uri)).getAuthority())
 
end TraitBasedRDF

@main def run =
  val test = Test[TraitBasedRDF.type]
  println(test.x)
