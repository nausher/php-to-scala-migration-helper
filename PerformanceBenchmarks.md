# Benchmark #2: Roman Number Generator #

  * Parameters used: 5000 100 (5,000 repetitions of 100 roman numerals)
  * PHP time: 55 seconds
  * Scala time: 16 seconds!! - 3.4x faster than original PHP script.

Scala time distribution:
```
     [java]      Compiled + native   Method                        
     [java]  16.5%    48  +    72    test_2_php$$anonfun$dec2roman$1$2.apply
     [java]   9.1%    26  +    40    test_2_php$.dec2roman$1
     [java]   6.4%    13  +    34    php.string.value
     [java]   6.3%     8  +    38    test_2_php$$anonfun$dec2roman$1$1.apply
     [java]   6.0%    19  +    25    com.caucho.quercus.env.ArrayValueImpl.createEntry
     [java]   5.3%    12  +    27    php.script$phpArrowAssoc.$minus$greater
     [java]   4.5%    19  +    14    php.array$$anonfun$map$1.apply
     [java]   4.1%     0  +    30    com.caucho.quercus.env.StringBuilderValue.toString
     [java]   3.2%     0  +    23    java.lang.StringBuffer.toString
```

The profiler output (above) indicates much of the time is spent in "wasteful" string conversions.

That means it's fairly easy to extract even more performance from the converted code, by changing variable declarations to Int, Double, and String (in place of the dynamic "ref" abstract class type used by default for all variables) where appropriate.

# Benchmark #1 (slow): PI Digit Generator #

## Initial Results ##

The first test was probably biased by not accounting for JVM start-up time.

The first benchmark test performed on 4/22/2010 produced a surprising result:
  * test\_1.php runs in 0.7 seconds
  * test\_1.scala in 16 seconds

Both were run using a "200" parameter for the number of PI Digits.

These results were surprising. Then I ran the test with -Xprof, and the result of that was even MORE surprising!

## Surprise: The Scala code is actually fast! The Java libraries are slow. ##

Java's Big Integer library is mind-bogglingly slow. By pure coincidence, the first arbitrarily selected performance test (PI Digits) is very bigmath-intensive. Apparently I was not the first to [discover this](http://stackoverflow.com/questions/611732/what-to-do-with-java-bigdecimal-performance).

These are the results from -Xprof (with -server and 300 digits):
  * 63.8% of the run time is spent in java.math.MutableBigInteger.divide
  * 24.6% is spent on other Big Math library functions, for a total of 88.4% of the run time
  * The JIT does not compile java.math.BigInteger.toString until it's been invoked 171 times
  * Reflection is not a significant source of overhead (less than 0.1% of time)
  * Only about 3% of time is spent on "wasteful" conversions among strings and php types
  * Due to the relative short run, another 3% or so is spent on class-loading.
  * That leaves about 5% of time to "other stuff".

The bottomline: This benchmark test should be ignored, unless you're one of the few people who actually need Big Integer in your application. This is actually a prime example of a [general problem with benchmarks](http://shootout.alioth.debian.org/flawed-benchmarks.php).