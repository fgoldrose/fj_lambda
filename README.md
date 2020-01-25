Embed lambda calculus into Featherweight Java with two methods.

Details on Featherweight Java: https://www.cis.upenn.edu/~bcpierce/papers/fj-toplas.pdf

fj lambda1.java: Take lambda calculus expressions and translate them to FJ programs that will evaluate to an equivalent expression. This approach fully takes advantage of the similarity between the method invocation rule and beta-reduction in the lambda calculus. Abstractions are represented as classes, and application is a method of each of those classes. This means that when we invoke the application method of an abstraction class, we are doing something equivalent to when we substitute a term for a variable in the lambda calculus. Additionally, the fields of a class are used to keep track of free variables in that abstraction, so we don’t have the complications of substitution using variable names. The downside to this approach is that it requires the programmer to do a lot more than just input an AST for a lambda term. A new class must be written for every abstraction within the term. This means that encoding a lambda expression that has many abstractions within abstractions requires writing many classes. It isn’t easily adapted to include a new lambda term quickly.

fj lambda2.java: A full evaluation system for lambda calculus terms in Featherweight Java. This system can evaluate any lambda calculus expression it is given, without the programmer having to define new classes. Instead, there are Abs, App, and Var classes that have methods for substitution, evaluation, and application. An Abs has a Var and Term field set by its constructor, and an App has two Term fields set by its constructor. This means you can construct any lambda calculus expression and then evaluate it with the eval method, with the classes that already exist. To use multiple variables, each is included as its own class extending the Var class. The downside to this approach is that dealing with variables becomes more complicated since a variable is a class that isn’t linked to the abstraction it is bound to, and variable names can be reused throughout the lambda expression. Uses a bound variable class to address the complexities of the substitution rule for free variables. This is explained more in the code file itself.
