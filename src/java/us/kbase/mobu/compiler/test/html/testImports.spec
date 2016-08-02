#include <import3.spec>
#include <import1.spec>
#include <import4.spec>
#include <import2.spec>

/* 
Test importing modules:
1) imports should be sorted
2) imports that are unused don't show up in the list in html, even if they're
   used in an imported spec
3) imports that are unused should still get a html file
4) deprecation across specs
*/
module Imports {

	typedef Import1.type1 foo;
	typedef import2.type1 bar;
	/* @deprecated Import1.type1 */
	typedef Import3.type1 baz;
};