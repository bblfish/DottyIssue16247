package generic 

import scala.util.Try
import RDF.URI
import scala.reflect.TypeTest

// here is the structural code that which is then implemented in 
// a number of ways... 

trait RDF:
  rdf =>

  type R = rdf.type
  
  type rNode <: Matchable
  type rURI <: rNode
  type Node <: rNode
  type URI <: Node & rURI

  given rops: ROps[R]

end RDF

object RDF:
  type rNode[R <: RDF] <: Matchable = R match
    case GetRelNode[n] => n & Matchable

  type rURI[R <: RDF] <: rNode[R] = R match
    case GetRelURI[n] => n & rNode[R]

  type Node[R <: RDF] <: rNode[R] = R match
    case GetNode[n] => n & rNode[R]

  type URI[R <: RDF] <: Node[R] = R match
    case GetURI[u] => u & Node[R] & rURI[R]

  private type GetRelNode[N <: Matchable] = RDF { type rNode = N }
  private type GetNode[N] = RDF { type Node = N }
  private type GetRelURI[U] = RDF { type rURI = U }
  private type GetURI[U] = RDF { type URI = U }

end RDF

trait ROps[R <: RDF]:
  def mkUri(str: String): Try[RDF.URI[R]]

  // def mkRelURI(str: String): Try[RDF.rURI[R]]
  protected def nodeVal(node: RDF.Node[R]): String
  protected def auth(uri: RDF.URI[R]): Try[String]
   
  extension (uri: URI[R])
    def authority: Try[String] = auth(uri)
 
end ROps


class Test[R <: RDF](using rops: ROps[R]):
  import rops.given
  val uri: Try[RDF.URI[R]] = rops.mkUri("https://bblfish.net/#i")
  val x: String = "uri authority="+uri.flatMap(u => u.authority)

end Test


