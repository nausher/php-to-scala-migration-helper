# News #
The source code which previously had only been available in the Downloads area, now has been uploaded to the SVN repository here on Google Code. There are also a couple of partial forks (converter source code only, no libraries) at https://github.com/atramos/php2scala

# Technical Overview #
PHP-to-Scala Migration Helper ("php2scala" for short) converts PHP code to clean, maintainable Scala source code. To cut to the chase, see ConversionExamples, or the feature-by-feature DesignDocument. It is technically a PHP Compiler, but it differs radically from other existing compilers such as Hip-Hop, PHC, Raven, Roadsend, Phalanger, and Quercus, in a few key points (see: KeyDifferences for details), most importantly the primary, usable output of the tool is nice, maintainable Source Code, not executable or Java bytecode.

# Business Overview #
PHP-to-Scala Migration Helper is a tool that enables individuals and organizations who've either inherited or outgrown a significant PHP codebase, to migrate to a Java-centric environment completely free of any PHP source code, PHP tools, PHP servers, and PHP developers. In the case of IT organizations, migration enables a consolidation of infrastructure and production operations around the J2EE platform - eliminating the need to continue supporting Apache servers, for instance.

# Why Scala? #
One might ask: why not target another JVM language such as Clojure, Groovy, Jruby, Jython, or Java itself as the output of the migration tool? The answer has to do with translation complexity. From all the common JVM languages, only Scala has enough syntactic expressiveness and flexibility (such as implicit conversions, operator overloading, triple-quoted strings, and the Uniform Access Principle), to enable a mostly lexical conversion from PHP that preserves readability of the code. The power of Scala is what ultimately enabled the proof-of-concept for this tool to be developed in one weekend afternoon and is also what enables the generated code to remain clutter-free and close to the original, improving readability which is the project's main goal.

# Does this tool really work? #
Yes, in short, it does. See the ReleaseNotes for details of what's been done so far, what's working, and what's planned.
