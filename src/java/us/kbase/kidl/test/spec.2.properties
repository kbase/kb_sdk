/*
ERDB Service API specification

This service wraps the ERDB software and allows querying the CDS via the ERDB
using typecompiler generated clients rather than direct Perl imports of the ERDB
code.

The exposed functions behave, generally, identically to the ERDB functions documented
L<here|http://pubseed.theseed.org/sapling/server.cgi?pod=ERDB#Query_Methods>.
It is expected that users of this service already understand how to query the CDS via
the ERDB.

*/

module ERDB_Service
{
	typedef string objectNames;
	typedef string filterClause;
	typedef string parameter;
	typedef list<parameter> parameters;
	typedef string fields;
	typedef int count;
	typedef string fieldValue;
	typedef list<fieldValue> fieldValues;
	typedef list<fieldValues> rowlist;
    
    /*
    Wrapper for the GetAll function documented L<here|http://pubseed.theseed.org/sapling/server.cgi?pod=ERDB#GetAll>.
    Note that the objectNames and fields arguments must be strings; array references are not allowed.
    */
    funcdef GetAll(objectNames, filterClause, parameters, fields, count) returns(rowlist);
    
    typedef string SQLstring;
    
    /*
    WARNING: this is a function of last resort. Try to do what you need to do with the CDMI client or the
    GetAll function first.
    Runs a standard SQL query via the ERDB DB hook. Be sure not to code inputs into the SQL string - put them
    in the parameter list and use ? placeholders in the SQL. Otherwise you risk SQL injection. If you don't
    understand this paragraph, do not use this function.
    Note that most likely, the account for this server only has select privileges and cannot modify the
    database.
    */
    funcdef runSQL(SQLstring, parameters) returns(rowlist);
    
};
