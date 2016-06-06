#include <import3.spec>
#include <import1.spec>
#include <import4.spec>
#include <import2.spec>

/* 
Test importing modules:
1) imports should be sorted
2) imports that are unused don't show up in the list in html
3) imports that are unused should still get a html file
*/
module Imports {

	typedef Import1.type1 foo;
	typedef import2.type1 bar;
	typedef Import3.type1 baz;
};