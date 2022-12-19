# Code for Dotty issue 16408: Problem with Traits not being Matchable

For details on the problem see [16408: Problem with Traits not being Matchable](https://github.com/lampepfl/dotty/issues/16408).

## History

An intial attempt seemed to reproduce the problem. but as per [26 Oct, discussion on Discord](https://discord.com/channels/632150470000902164/632628489719382036/1034828756063363082) it turns out this seems to have been tied to my OS or java installation...

I opened [Issue 16247: Should Java Interfaces be Matchable?](https://github.com/lampepfl/dotty/issues/16247) but the error could not be reproduced,
and seemed to only appear on my machine.

So instead of sending a Tar file, I opened a github repository, so as to make sure the code could be run as a gitub action
on various VMS

## Test the code

Currently to run the code make a clean install of java and scala.

I try to use minimal commands and limit what jdk and java compiler I am using

```zsh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
export SCALA_HOME=/usr/local/scala/scala3-3.2.1/
```

The Trait based version presented here does not compile. By changing the trait 
to a class everything compiles and the test runs.

```zsh
> sh bin/clean
> sh bin/compileScala
-- Error: scala/RDF.scala:44:8 -------------------------------------------------
44 |  given rops: ROps[R] with
   |        ^
   |object creation impossible, since def auth(uri: RDF.URI[R]): util.Try[String] in trait ROps is not defined
   |(Note that
   | parameter RDF.URI[R] in def auth(uri: RDF.URI[R]): util.Try[String] in trait ROps does not match
   | parameter TraitTypes.Uri & Matchable in override def auth(uri: TraitTypes.Uri & Matchable): util.Try[String] in object rops in object TraitRDF
   | )
-- [E038] Declaration Error: scala/RDF.scala:46:17 -----------------------------
46 |    override def auth(uri: RDF.URI[R]): Try[String] =
   |                 ^
   |   method auth has a different signature than the overridden declaration
   |----------------------------------------------------------------------------
   | Explanation (enabled by `-explain`)
   |- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   | There must be a non-final field or method with the name auth and the
   | same parameter list in a super class of object rops to override it.
   |
   |   override def auth(uri: TraitTypes.Uri & Matchable): util.Try[String]
   |
   | The super classes of object rops contain the following members
   | named auth:
   |   def auth(uri: RDF.URI[TraitRDF.R]): util.Try[String]
    ----------------------------------------------------------------------------
2 errors found
```

