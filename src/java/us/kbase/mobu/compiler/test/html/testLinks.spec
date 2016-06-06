/* 
test internal file links - from type name to typedef and deprecation links
external file links are tested in the imports test
note that functions cannot link outside the spec for now and the module name
is ignored in the deprecation
*/
module links {

	funcdef newest() returns();
	/* @deprecated foo.newest */
	funcdef newer() returns();
	/* @deprecated links.newer */ 
	funcdef new() returns();
	/* @deprecated new */
	funcdef old() returns();
	
	typedef string newest;
	/* @deprecated links.newest */
	typedef string new;
	/* @deprecated new */
	typedef string old;
	
	typedef string foo;
	typedef foo bar;
	typedef bar baz;

};