# Introduction #

Yes, it works! The test\_1 (computation of digits of PI - from the Computer Benchmarks Game) and test\_2 (roman numeral conversion) are now fully functional. That means, successful fully automated conversion of test\_N.php to test\_N.scala, and successful execution of test\_N.scala with the equivalent results as the original PHP code!

The primary goal of producing clean, readable Scala code has been met.  The secondary goal of high performance is on track, with some caveats (see PerformanceBenchmarks for details). The tertiary goal of PHP compatibility remains surprisingly intact: using reflection to dynamically resolve objects makes it possible to support "duck-typing", untyped references, and essentially all other dynamic features of PHP.

## Features planned for future releases ##
  * Pretty-print (auto-indent) the Scala output
  * Performance improvements
  * Type-inference based on function type-hints
  * Type-inference based on global static analysis
  * Complete integration with Quercus - still missing I/O, ob\_handler, eval(), etc
  * Clean-up the "$" types with well thought-out behaviors; currently it's a bit ad-hoc
  * Still need an overall strategy and details for dealing with "large" applications (multiple PHP scripts, such as in a web app).
  * Port the build/test scripts (mklib.sh, runtests.sh) to Scala so they can run on Windows

## New Features in v0.6 (2010-04-28) ##
  * This was a performance-focused release. The roman number generator (test\_2) is now 3.4x FASTER in the generated Scala versus the original PHP. That is based on the straight output of the source converter, without any optimizations such as replacing the dynamically-typed variables with Ints or Strings. With the latter, performance would probably another 2x faster at least.

## New Features in v0.5 (2010-04-27) ##
  * Ternary operator (?:) converts to |?
  * Fuller support for arrays, including type inference, foreach(...as...){} and constructor syntax
  * New testcase (test\_2, a roman numeral generator) - executes successfully!
  * Parser handling for assignment in while loop condition
  * Specific patches for str\_replace, isset.
  * String concatenation operators +& and +=& (like PHP's . and .= )
  * New and improved execution speed! (see PerformanceBenchmarks)

## New Features in v0.4 (2010-04-22) ##
  * Success! This is the first release where the PI Digit Generator (test\_1) works!
  * **This qualifies as an "Alpha" release! It is sufficiently functional now to encourage volunteers to try it out and contribute their own improvements :)**
  * Finished up the basic integration with Quercus. A total of 1,054 (over one thousand) standard PHP functions are now integrated and available for use. What remains now are the functions that could not be handled by the automatic build script due to unusual signatures, duplicate names, etc and had to be manually excluded (see mklib.sh).
  * Finished up the basic Scala-friendly "php-like" types ($int, $str, $obj, $val, and $null) which provide implicit conversions and other syntactic niceties that keep the generated Scala code free of clutter.
  * Introduced "idiomatic" dynamic resolution operators ~> for variables and ~& for functions (note that "~>" had to be chosen over "->" due to Scala's operator precedence rules)
  * First benchmark: test\_1.php 200 runs in 0.7 seconds; test\_1.scala in 16 seconds. Yes it is surprisingly slow - and the cause for slowness is even more surprising! See PerformanceBenchmarks for details.
  * Use static binding ("." instead of "~>") for references to _this_. That's more aesthetically pleasing but actually provided no speed gain - reflection is pretty fast!
  * String variable interpolation syntax is working too now

## New Features in v0.3 (2010-04-18) ##
  * This was a rather buggy interim release. Nothing works.

## New Features in v0.2 (2010-04-16) ##
  * Begun integration with the Quercus run-time (full PHP library implementation in Java)
  * Right now everything compiles fine but only test\_0.php (Hello World) runs successfully
  * Successful compilation of test\_1.scala!!! It still doesn't run, as the Quercus integration is unfinished
  * The conversion of constructors now is nice and clean, although not "idiomatic Scala"
  * Renamed php.value to '$', to identify generic PHP variables.
  * Primitive type-inference (everything is a '$', but at least it is declared)
  * Implemented implicit conversions in php.scala
  * Improved type inference - variables assigned to an integer are declared var:Int

## Features implemented in v0.1 (2010-04-12) ##
  * Use the built-in PHP tokenizer for correctness (should handle complex scripts)
  * Recursive "look-ahead" parser avoids an AST, this helps preserve code structure.
  * Breaking up of for(;;) loops into scala while() loops is working.
  * Use correct syntax for function declaration (including no '=' for non-returning)
  * Removal of dollar signs from all variables
  * Change of array index square brackets to parens.
  * Change of object operator -> to '.'
  * Inline HTML for now just invokes an "output" function with triple-quoted string.
  * Correct handling of command-syntax (e.g. print) by adding parenthesis.
  * File scope becomes an object scope; this should help a later "aliasing" scheme.