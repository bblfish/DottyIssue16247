package rdfscala

trait TstNode:
   def value(): String
trait URI extends TstNode

trait ScalaMkNodes:
   def mkUri(uriStr: String): URI

object SimpleScalaNodeFactory extends ScalaMkNodes:
   def mkUri(uriStr: String): URI = new URI {
      def value(): String = uriStr
   }