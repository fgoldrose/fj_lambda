// All code is valid Featherweight Java except for: 
// --- toString methods (just to provide nice output, no impact on evaluation)
// --- FJLambda2 class (to start the program and print out examples)

// Term is a superclass for everything which allows typing to work and
// provides some default methods.
class Term extends Object {
	Term(){ super(); }
	//these app, subs, eval, and boundvar methods are never used since 
	// each class extending Term has its method for each that overrides.
	Term app(Term x){ return new Var(); } 
	Term subs(Var y, Term t){ return t; }
	Term eval() { return this; }
	Term boundvar(Var b){return this;}
	// if* methods test if a term is * and returns f, or else it returns e.
	// Default values for if* methods will return e.
	// Within the class for each var, the if* method will be overridden
	// to return f instead.
	Term ifx(Term f, Term e){ return e;	}
	Term ify(Term f, Term e){ return e; }
	Term ift(Term f, Term e){ return e; }
	Term iff(Term f, Term e){ return e; }

	public String toString(){
		return "TERM";
	}
}

// Vars are never used directly. It is a superclass for all the 
// variable classes (X, Y, T, F) defined below. 
class Var extends Term {
	Var(){ super(); }
	// Applying a Var to anything is stuck. To stop evaluation when the term is 
	// stuck we use the Stuck class.
	Term app(Term y){ return new Stuck(new App(this, y)); }
	// Returns the term t only if the Var matches z.
	Term subs(Var z, Term t){return z.ifeq(this, t, this);}
	// Each subclass of Var has its own ifeq method, so this is only for typing.
	Term ifeq(Var c, Term o1, Term o2){ return o2; }
	// boundvar binds the var if it equals b. 
	Var boundvar(Var b){ return (Var) b.ifeq(this, new Bound(this), this); }
	// unbinding an unbound var does nothing.
	Var ub() { return this; }

	public String toString(){
		return "VAR";
	} 
}

// Bound Vars are used to make sure that Vars bound to abstractions are
// distinguished from free vars with the same name.
// We will only bind Vars when there is a risk of this confusion happening,
// so when an abstraction is substituted into. 
class Bound extends Var {
	Var x;
	Bound(Var x){ super(); this.x=x;}
	// This checks that the var c is bound, and if so, checks that the
	// unbound c is equal to the unbound Bound Var (this.x)
	Term ifeq(Var c, Term o1, Term o2){
		return c.ifeq(c.ub(), o2, c.ub().ifeq(this.x, o1, o2));
	}
	// unbound, accesses the Var that is bound.
	Var ub() { return this.x; }

	// single quote added to variable name to distinguish easily
	// from free variable with the same name. 
	public String toString(){
		return this.x.toString() + "'";
	}
}

class App extends Term {
	Term t1;
	Term t2;
	App(Term t1, Term t2){super(); this.t1=t1; this.t2=t2; }
	Term app(Term tx){
		// Nested applications
		return new App(new App(this.t1, this.t2), tx);
	}
	Term subs(Var y, Term ty){
		// Push the subs through to both terms.
		return new App(this.t1.subs(y, ty), this.t2.subs(y, ty));
	}
	Term eval(){
		// The final eval call is in case there are nested applications.
		return this.t1.eval().app(this.t2.eval()).eval();
	}
	Term boundvar(Var b){
		// push the boundvar through to both terms
		return new App(this.t1.boundvar(b), this.t2.boundvar(b)); 
	}

	public String toString(){
		return "[" + this.t1.toString() + " " + this.t2.toString() + "]" ;
	}
}

class Stuck extends Term {
	// Stuck is needed to make sure that evaluation stops when it should instead
	// of getting caught in an infinite loop (if the end result is an application
	// it will keep trying to evaluate).
	Term t1;
	Stuck(Term t1){super(); this.t1=t1;}
	Term app(Term tx){
		// push through application.
		return new Stuck(new App(this.t1, tx));
	}
	Term subs(Var y, Term ty){
		// push through subs.
		return new Stuck(this.t1.subs(y, ty));
	}
	Term eval(){
		// Does not evaluate.
		return this;
	}
	Term boundvar(Var b){
		// push through boundvar.
		return new Stuck(this.t1.boundvar(b));
	}

	public String toString(){
		// not distinguished in text output
		return this.t1.toString();
	}
}

class Abs extends Term {
	Var x;
	Term t;
	Abs(Var x, Term t){super(); this.x=x; this.t=t; }
	Term app(Term y){
		// Substitute the var x with the term y in Term t.
		return this.t.subs(this.x, y);
	}
	Term subs(Var y, Term t2){
		//
		return y.ifeq(this.x, this, new Abs(new Bound(this.x), this.t.boundvar(this.x).subs(y, t2)));
	}
	Term boundvar(Var b){
		// only bind if b is not equal to x. (since we only need to bind b if it's free in the abstraction)
		// If not, push throughthe boundvar to t. 
		return b.ifeq(this.x, this, new Abs(this.x, this.t.boundvar(b)));
	}

	public String toString(){
		return "(λ" + this.x.toString() + "." + this.t.toString() + ")";
	}
}


// Different variable names
class X extends Var {
	X() {super(); }
	
	Term ifeq(Var c, Term o1, Term o2){
		return c.ifx(o1, o2); // ifeq method is based on the ifx method. 
	}
	Term ifx(Term f, Term e){ // overrides the ifx method to return f instead of e.
		return f;
	}

	public String toString(){
		return "x";
	}

}
class Y extends Var {
	Y() {super(); }
	Term ifeq(Var c, Term o1, Term o2){
		return c.ify(o1, o2);
	}
	Term ify(Term f, Term e){
		return f;
	}

	public String toString(){
		return "y";
	}
}
class T extends Var {
	T() {super(); }
	Term ifeq(Var c, Term o1, Term o2){
		return c.ift(o1, o2);
	}
	Term ift(Term f, Term e){
		return f;
	}

	public String toString(){
		return "t";
	}
}
class F extends Var {
	F() {super(); }
	Term ifeq(Var c, Term o1, Term o2){
		return c.iff(o1, o2);
	}
	Term iff(Term f, Term e){
		return f;
	}

	public String toString(){
		return "f";
	}
}



class FJLambda2 {
	public static void main (String[] args) {

		Term tru = new Abs(new T(), new Abs(new F(), new T()));
		Term fls = new Abs(new T(), new Abs(new F(), new F()));
		Term test = new Abs(new X(), new Abs(new Y(), new Abs(new T(), new App(new App(new X(), new Y()), new T()))));
		Term and = new Abs(new X(), new Abs(new Y(), new App(new App(new X(), new Y()), fls)));

		Term apptest = new App(new App(new App(test, fls), fls), tru);
		Term apptest2 = new App(new App(new App(test, new App(new App(and, tru), tru)), new F()), tru);
		System.out.println(apptest + " = " + apptest.eval());
		System.out.println(apptest2 + " = " + apptest2.eval());

		Term pair = new Abs(new F(), new Abs(new X(), new Abs(new Y(), new App(new App(new Y(), new F()), new X()))));
		Term fst = new Abs(new T(), new App(new T(), tru));
		Term snd = new Abs(new T(), new App(new T(), fls));
		Term apppair = new App(new App(pair, tru), new F());
		Term appfst = new App(fst, new App(new App(pair, tru), new F()));
		Term appsnd = new App(snd, new App(new App(pair, tru), new F()));
		System.out.println(apppair + " = " + apppair.eval());
		System.out.println(appfst + " = " + appfst.eval());
		System.out.println(appsnd + " = " + appsnd.eval());

		Term c0 = new Abs(new X(), new Abs(new Y(), new Y()));
		Term c1 = new Abs(new X(), new Abs(new Y(), new App(new X(), new Y())));
		Term c2 = new Abs(new X(), new Abs(new Y(), new App(new X(), new App(new X(), new Y()))));
		Term iszero = new Abs(new T(), new App(new App(new T(), new Abs(new X(), fls)), tru));
		Term appz1 = new App(iszero, c0);
		Term appz2 = new App(iszero, c2);
		System.out.println(appz1 + " = " + appz1.eval());
		System.out.println(appz2 + " = " + appz2.eval());

		Term omega_aux = new Abs(new X(), new App(new X(), new X()));
		Term omega = new App(omega_aux, omega_aux);
		//System.out.println(omega.eval()); 
		// ^ commented out because overflows stack (as it should!)

	}
}
