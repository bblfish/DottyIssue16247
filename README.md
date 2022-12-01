# Attempting to reporduce dotty Issue 16247 

For details on the problem see [Issue 16247: Should Java Interfaces be Matchable?](https://github.com/lampepfl/dotty/issues/16247). 

An intial attempt seemed to reproduce the problem, But as per [26 Oct, discussion on Discord](https://discord.com/channels/632150470000902164/632628489719382036/1034828756063363082) it turns out this seems to have been tied to my OS or java installation... 

After simplifying the code a lot we have 3 scala classes in the [scala](scala) directory
* [RDF.scala](scala/RDF.scala): this contains the generic trait that uses the operations `Ops` that will 
 be implemented in two ways: once with a class and once with a trait.
* [RDF_Class.scala](scala/RDF_Class.scala): this uses a class to implement the operations 
* [RDF_Trait.scala](scala/RDF_Trait.scala): this uses a trait to implement the operations 

A side by side diff (`opendiff` on mac) of `RDF_Class` and `RDF_Trait` will show clearly that
the only real changes are the move from a class to an interface.

I try to use minimal commands and limit what jdk and java compiler I am using

```zsh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
export SCALA_HOME=/usr/local/scala/scala3-3.2.1/
```

To compile the code look at the two scripts in [bin](bin/) directory.

The RDF code based on Scala Classes works

```zsh
➤  sh bin/compileScalaPure
Success(https://bblfish.net/#i)
folded=<https://bblfish.net/#i>
matched should be uriisURI class_based.ClassTypes$AFactory$$anon$2@358ee631with authority Success(bblfish.net)
```

The code relying on Scala traits does not compile either. Here we use scala-cli as we do
in the github actions that are run whenever a commit is sent to this repository. See [workflows reports](https://github.com/bblfish/DottyIssue16247/actions/workflows/test-scala.yml).

```scala
➤  scala-cli --scala 3.2.1 scala/RDF_Trait.scala scala/RDF.scala                                                                                                                                              
Compiling project (Scala 3.2.1, JVM)
[error] ./scala/RDF_Trait.scala:25:9: object creation impossible, since def auth(uri: generic.RDF.URI[R]): util.Try[String] in trait ROps in package generic is not defined
[error] (Note that
[error]  parameter generic.RDF.URI[R] in def auth(uri: generic.RDF.URI[R]): util.Try[String] in trait ROps in package generic does not match
[error]  parameter interf_based.TraitTypes.Uri & (interf_based.TraitTypes.Node & (
[error]   interf_based.TraitTypes.Node
[error]  & Matchable)) & (interf_based.TraitTypes.Uri & (interf_based.TraitTypes.Node &
[error]   Matchable
[error] )) in override def auth
[error]   (uri: interf_based.TraitTypes.Uri & (interf_based.TraitTypes.Node & (
[error]     interf_based.TraitTypes.Node
[error]    & Matchable)) & (interf_based.TraitTypes.Uri & (interf_based.TraitTypes.Node
[error]     &
[error]    Matchable))): util.Try[String] in object rops in object TraitRDF
[error]  )
[error]   given rops: generic.ROps[R] with
[error]         ^
[error] ./scala/RDF_Trait.scala:30:18: method auth has a different signature than the overridden declaration
[error]     override def auth(uri: RDF.URI[R]): Try[String] =
[error]                  ^
Error compiling project (Scala 3.2.1, JVM)
```

(see also the `compileScalaTrait` script in the `bin` dir)

## Notes

The problem was found in [commit e26983bec0d1a509cab97ac44f78f45935f4a980](https://github.com/bblfish/DottyIssue16247/commit/e26983bec0d1a509cab97ac44f78f45935f4a980) after adding a simple method `auth` to the `ROps` trait.

```scala
 protected def auth(uri: RDF.URI[R]): Try[String]
```

The next commit after that was to clean up the code into three sections:
 - generic RDF -- all the code that does not know about implementations
 - class-based -- the implementation that works using scala classes
 - interface-based -- the implementation works using interfaces (Java ones in particular)

using a side by side diff tool such as opendiff on macos, will help show
that the difference between RDF_Class and RDF_Interface is essentially 
just that RDF_Class needs to supply its own classes and factory. 

```zsh
cd scala
opendiff RDF_Class.scala  RDF_Interface.scala
```
