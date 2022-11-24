package class_based

import scala.util.Try
import scala.reflect.TypeTest

// If instead of using interfaces we use abstract classes then it compiles
// correctly
object ClassTypes {
  abstract class Node:
    def value: String

  abstract class Uri extends Node
  abstract class BNode extends Node
  abstract class Lit extends Node

  trait IFactory:
    def mkBNode(): BNode
    def mkUri(u: String): Uri
    def mkLit(u: String): Lit

  def getFactory: IFactory = AFactory

  private object AFactory extends IFactory:
    var bnode: Int = 0
    def mkBNode(): BNode =
      bnode = bnode + 1
      new BNode { def value = bnode.toString }
    def mkUri(u: String): Uri =
      new Uri { def value = u }
    def mkLit(u: String): Lit =
      new Lit { def value = u }
}

object ClassRDF extends generic.RDF:
  import class_based.ClassTypes as cz
  import generic.*

  override opaque type rNode <: Matchable = cz.Node
  override opaque type rURI <: rNode = cz.Uri
  override opaque type Node <: rNode = cz.Node
  override opaque type URI <: Node & rURI = cz.Uri

  given rops: generic.ROps[R] with
    override def mkUri(str: String): Try[RDF.URI[R]] = Try(
      cz.getFactory.mkUri(str)
    )
    override protected def nodeVal(node: RDF.Node[R]): String = node.value
    override protected def auth(uri: RDF.URI[R]): Try[String] = 
      Try(java.net.URI.create(nodeVal(uri)).getAuthority())
end ClassRDF

@main def run =
  import generic.Test
  val test = Test[ClassRDF.type]
  println(test.x)
