# Attempting to reporduce dotty Issue 16247 

For details on the problem see [Issue 16247: Should Java Interfaces be Matchable?](https://github.com/lampepfl/dotty/issues/16247). 

An intial attempt seemed to reproduce the problem. but as per [26 Oct, discussion on Discord](https://discord.com/channels/632150470000902164/632628489719382036/1034828756063363082) it turns out this seems to have been tied to my OS or java installation...

Currently to run the code make a clean install of java and scala.

I try to use minimal commands and limit what jdk and java compiler I am using

```zsh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
export PATH=/bin:/usr/bin:/sbin:/usr/local/scala/scala3-3.2.1-RC4/bin:/usr/local/scala/sbt-1.7.2/bin
```

To compile the code look at the two scripts in [bin](bin/) directory.

The RDF code based on Scala Classes works

```zsh
➤  sh bin/compileScalaPure
Success(https://bblfish.net/#i)
folded=<https://bblfish.net/#i>
matched should be uriisURI class_based.ClassTypes$AFactory$$anon$2@358ee631with authority Success(bblfish.net)
```

The code relying on Java interfaces does not (whether it is Java or just the pure interfaces 
that is the problem is not yet settled)

```zsh
➤  sh bin/compileJava
-- Error: scala/RDF_Interface.scala:18:8 ---------------------------------------
18 |  given rops: ROps[R] with
   |        ^
   |object creation impossible, since protected def auth(uri: generic.RDF.URI[R]): util.Try[String] in trait ROps in package generic is not defined
   |(Note that
   | parameter generic.RDF.URI[R] in protected def auth(uri: generic.RDF.URI[R]): util.Try[String] in trait ROps in package generic does not match
   | parameter testorg.Uri & (testorg.TstNode & (testorg.TstNode & Matchable)) & (testorg.Uri &
   |
   |(testorg.TstNode & Matchable)) in protected override def auth
   |  (uri: testorg.Uri & (testorg.TstNode & (testorg.TstNode & Matchable)) & (
   |    testorg.Uri
   |   & (testorg.TstNode & Matchable))): util.Try[String] in object rops in object IRDF
   | )
-- [E038] Declaration Error: scala/RDF_Interface.scala:25:27 -------------------
25 |    override protected def auth(uri: RDF.URI[R]): Try[String] =
   |                           ^
   |   method auth has a different signature than the overridden declaration
   |----------------------------------------------------------------------------
   | Explanation (enabled by `-explain`)
   |- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   | There must be a non-final field or method with the name auth and the
   | same parameter list in a super class of object rops to override it.
   |
   |   protected override def auth
   |   (uri: testorg.Uri & (testorg.TstNode & (testorg.TstNode & Matchable)) & (
   |     testorg.Uri
   |    & (testorg.TstNode & Matchable))): util.Try[String]
   |
   | The super classes of object rops contain the following members
   | named auth:
   |   protected def auth(uri: generic.RDF.URI[interf_based.IRDF.R]): util.Try[String]
    ----------------------------------------------------------------------------
2 errors found
```

## Notes

