# Direct Comparison to Other Tools #
None of the tools currently available have #1 above as their primary goal.
  * PHC, Raven, and Roadsend have PHP Compatibility and Performance as co-highest priorities, maintainable source code output is not at all a goal, and is in fact very bad. These compilers are great for people who are happy with the PHP "culture" and just want their code to run faster, while committing themselves even more to a long-term PHP environment.
  * Hip-Hop has Performance as its highest priority, at the cost of compatibility (e.g. many dynamic language features are not supported) and lack of usable output (the C++ output is an unmaintainable mess). Great for people who are skeptical of PHP, don't care for PHP dynamic language features, and need their code to run a lot faster.
  * Quercus is focused on PHP compatibility and long-term co-existence of PHP code alongside Java. Quercus does not produce any translated source code. On the other hand, the Quercus run-time library is a fairly complete implementation of PHP built-in functions in pure Java, and we intend to re-use the library sources in this project.