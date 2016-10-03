/* 
test default authentication
*/
module auth {

	funcdef none() returns();
	funcdef opt() returns() authentication optional;
	funcdef req() returns() authentication required;
	
	authentication required;
	
	funcdef reqnone() returns() authentication none;
	funcdef reqopt() returns() authentication optional;
	funcdef reqreq() returns();

	authentication optional;
	
	funcdef optnone() returns() authentication none;
	funcdef optopt() returns();
	funcdef optreq() returns() authentication required;
	
	authentication none;
	
	funcdef nonenone() returns();
	funcdef noneopt() returns() authentication optional;
	funcdef nonereq() returns() authentication required;
};