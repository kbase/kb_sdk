/* 
test whitespace in comments
    	  

 
*/
module whitespace {

	/**/
	typedef int bar;

	/*
*/
	typedef int foo;
	
	/* */
	typedef int baz;

	/* 
*/
	typedef int whee;

	/*	
*/
	typedef int wuz;
	
	/*a
*/
	typedef int a;

	/* a
*/
	typedef int b;
	
	/*a*/
	typedef int c;
	
	/* a*/
	typedef int d;
	
	/*	a*/
	typedef int e;

};