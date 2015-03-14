Below is a description of the some of the more interesting transformations that are performed in the conversion from PHP to Scala.
# PHP Built-In Functions #

PHP has a standard library with thousands of built-in functions and extensions. Luckily for us, an Open Source Java implementation of those functions has existed for some time - as a feature known as Quercus buried inside the Caucho Resin application server.

php-to-scala is currently bundled with version 4.0.6 of the Quercus/Resin Jars. At build time, a script (`mklib.sh`) scans the Quercus source to produce a more transparent façade to the Quercus libraries: Every PHP function is automatically in scope for subclasses of `php.script` which is defined in `php.scala`. This is all then packaged into `php-scala.jar`.

The functions are made available directly by name and with the same parameter signatures in the converted Scala code as in the original PHP script. In other words, the converted Scala code really looks identical to the PHP code, in regards to the use of built-in functions.


# `for` loop #

Scala does not have a traditional `for` loop.

The solution is to rewrite the `for` into a `while` loop: `for(E;F;G){H}` becomes `E; while(F) {H; G}`.

In a future release this will be re-implemented using higher-order function as `for_loop(E,F,G){H}`.

# Ternary Operator #

Scala lacks a traditional ternary operator - `?:`.

The solution, which preserves the overall "look and feel" of a ternary operator, is the `?|` function, defined in `php.scala`, which takes a `Boolean` on the left and a function on the right. The expression `E ? F : G` becomes `E ?| {if(_) F else G}`

Although a simple `if(E) F else G` might seem like a simpler alternative, that would have required a much more complex transformation algorithm.

# Duck Typing #
In PHP, it is possible to reference functions and members on an object of an unknown type. For instance, consider this PHP code:
```
function f($obj) {
  $obj->doSomething(123);
}
```
In this case, the type of `$obj` is unknown. The `doSomething()` method is only resolved at run-time.

The Scala solution is actually rather simple. We define a dynamic resolution operator, `~&`, which uses Java reflection to resolve the method. So the Scala code becomes:
```
def f(obj: ref) {
  obj~&'doSomething->(123);
}
```
It looks slightly uglier than the PHP, but not too bad really. The developer can always clean-up the code later once the actual type is known:
```
def f(obj: SomeClass) {
  obj.doSomething(123)
}
```
The `~&` works for class functions. For class variables, the dynamic resolution operator is `~>`. We have to use `~>` instead of the more natural `->` here only because of operator precedence.

# Scala Constructors #

Scala is notorious for its restrictions on what constructors may be defined on a class.

The work-around for the converted PHP code is rather trivial: we just don't generate Scala constructors! Instead, every Scala class is given an empty constructor, and the PHP constructor is translated to a `__construct` method (that is literally the name of the method).

So, this PHP code:
```
class Point {
  function Point($x,$y) { ... }
}
$p = new Point(1,2);
```
is translated to this Scala code:
```
class Point {
  def __construct(x: ref, y: ref) { ... }
}
p = new Point __construct(1,2);
```
It looks a bit strange at first but is still quite bearable. The developer can always come back at a later time (after the PHP code has been thrown away for good) and change the `__construct` to an actual constructor. One major advantage of a static language like Scala is, that the developer can rely on the Scala compiler (or a good IDE) to point out all the references that need to be updated. The developer does not have to run the application to discover where all the errors are.

# String Concatenation #
The `.` and `.=` are mapped to `+&` and `+&=` respectively. Suggestions for a better choice of symbol would be welcome.

# Number Arithmetic Semantics #
PHP is notorious for its "flexible" numeric type which automatically expands from integer to double as needed. Scala on the other hand is stuck to more traditional Ints, Longs, and Doubles.

The solution here is to let Quercus handle the number semantics. It's all done within the `NumberValue` hierarchy which includes subclasses `LongValue` and `DoubleValue`. The performance does not seem to suffer from this. Scala's implicit conversions and operator overloading makes it all transparent to the developer: an expression like `x = y + z` looks the same regardless of whether the variables involved are Ints, Doubles, or Quercus `NumberValue`s.

The Scala developer can later substitute `Int` and `Double` where appropriate, such as to make the code clearer, or slightly faster.

# Inline HTML #
These are handled as Scala triple-quoted strings. Not a lot of testing done yet with this.

# eval() #
Quercus has its own PHP parser and interpreter (in Java), which includes full support for `eval`. This just hasn't been integrated or tested with php-to-scala yet, but it should work with minimal effort.