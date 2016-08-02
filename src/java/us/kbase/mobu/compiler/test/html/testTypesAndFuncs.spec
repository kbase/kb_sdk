/* 
test various combinations of types alone and in functions
*/
module typesAndFuncs {

	typedef list<mapping<string, UnspecifiedObject>> bar;
	typedef tuple<int> tint;
	typedef mapping<string, list<tint>> sillymap;
	
	/*
	This doesn't compile.
	typedef structure {
		string bar;
		structure {
			int foo;
		} whee;
	} wooga;
	*/
	
	typedef structure {
		string bar;
		int foo;
		float baz;
		tuple<bar whee, int, float whoo> ping;
		mapping<string, tint> mtint;
	} thingy;
	
	funcdef f(thingy, bar myname) returns(sillymap);
	funcdef f2(list<bar>) returns(int);
	funcdef f3(UnspecifiedObject) returns(tint);
	
	/* this doesn't compile
	funcdef f4(structure {int bar;} whee) returns();
	*/
};