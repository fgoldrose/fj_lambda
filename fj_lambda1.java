// All code is valid Featherweight Java except for: 
// --- toString methods (just to provide nice output, no impact on evaluation)
// --- FJLambda class (to start the program and print out examples)

// Term is a superclass for the other terms that allows
// typing to work. We assume that all Terms have an app method,
// so the typing system requires us to define one here.
// Actually, this method will NEVER be called since every
// subclass of Term has its own app method which overrides it.
class Term extends Object {
	Term(){ super(); }
	Term app(Term x){ return this; }

	public String toString(){
		return "TERM";
	}
}

// A FreeVar applied to something will be stuck, so we need a class
// to be able to represent this possible result of a lambda expression.
class StuckApp extends Term {
	Term x;
	Term y;
	StuckApp(Term x, Term y){ super(); this.x=x; this.y=y; }
	Term app(Term z){
		return new StuckApp(new StuckApp(this.x, this.y), z);
	}
	public String toString(){
		return "(" + x.toString() + " " + y.toString() + ")";
	}
}

// Note that we only need a class for free variables, since
// all variables within abstractions (whether bound or free) 
// are stored in fields of that abstraction class.
// Classes are things that can be returned, so the only time 
// a variable class is necessary at all is when that variable
// is free in the entire lambda expression and may be the result
// of the evaluation.
// If we needed multiple free variables we could make other classes
// that extended FreeVar.
class FreeVar extends Term {
	FreeVar(){ 
		super(); 
	}
	Term app(Term y){ return new StuckApp(new FreeVar(), y); }

	public String toString(){
		return "v";
	}
}

// Returns the term it was applied to.
class Id extends Term {
	Id(){ super(); }
	Term app(Term x) {
		return x;
	}
	public String toString(){
		return "λx.x";
	}
}

// Returns the term it was given in the constructor,
// no matter what term it is applied to.
class Constant extends Term {
	Term y;
	Constant(Term y){ super(); this.y=y; }
	Term app(Term x){
		return this.y;
	}

	public String toString(){
		return "λx.y";
	}
}

class Tru extends Term {
	Tru(){ super(); }
	Term app(Term x){
		return new Constant(x);
	}
	public String toString(){
		return "λt.λf.t";
	}
}

class Fls extends Term {
	Fls(){ super(); }
	Term app(Term x){
		return new Id();
	}
	public String toString(){
		return "λt.λf.f";
	}
}

// Necessary to define each abstraction within test.
class Test_aux2 extends Term {
	Term x;
	Term y;
	Test_aux2(Term x, Term y) { super(); this.x=x; this.y=y; }
	Term app(Term z){
		return x.app(y).app(z);
	}
	public String toString(){
		return "λz.(x y z)";
	}
}

class Test_aux1 extends Term {
	Term x;
	Test_aux1(Term x) { super(); this.x=x; }
	Term app(Term y){
		return new Test_aux2(x, y);
	}
	public String toString(){
		return "λy.λz.(x y z)";
	}
}

class Test extends Term {
	Test() { super(); }
	Term app(Term x){
		return new Test_aux1(x);
	}
	public String toString(){
		return "λx.λy.λz.(x y z)";
	}
}

class FJLambda1 {
	public static void main (String[] args) {
		Term run1 = new Test().app(new Fls()).app(new Id()).app(new Constant(new Fls()).app(new Tru()));
		Term run2 = new Test().app(new Tru()).app(new Tru()).app(new Fls());
		Term run3 = new Test().app(new Fls()).app(new FreeVar()).app(new Fls().app(new FreeVar()));
		System.out.println(run1);
		System.out.println(run2);
		System.out.println(run3);
	}
}