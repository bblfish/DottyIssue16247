package interf_based

import scala.util.Try
import scala.reflect.TypeTest
import generic.*

// here we use interfaces written in Java 
// todo, put back example using traits only to see if Java is the problem
object JavaInterfaceRDF extends RDF:
  lazy val factory = testorg.impl.SimpleNodeFactory.getInstance()
  override opaque type rNode <: Matchable = testorg.TstNode
  override opaque type rURI <: rNode = testorg.Uri
  override opaque type Node <: rNode = testorg.TstNode
  override opaque type URI <: Node & rURI = testorg.Uri

  given rops: ROps[R] with
    override def mkUri(str: String): Try[RDF.URI[R]] = Try(
      factory.mkUri(str)
    )
    override protected def nodeVal(node: RDF.Node[R]): String = node.value()
    override protected def auth(uri: RDF.URI[R]): Try[String] = 
      Try(java.net.URI.create(nodeVal(uri)).getAuthority())

 
end JavaInterfaceRDF

@main def run =
  val test = Test[JavaInterfaceRDF.type]
  println(test.x)

