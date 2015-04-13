module basicsrv:Basic {

    typedef int integer_number;

    typedef float double_number;

    /* Simple structure */
    typedef structure {
        integer_number prop1;
        double_number prop2;
        string prop3;
    } simple_struct;

    typedef structure {
        list<simple_struct> large_prop1;
        mapping<string, simple_struct> large_prop2;
        list<mapping<string, list<simple_struct>>> large_prop3;
        mapping<string, list<mapping<string, simple_struct>>> large_prop4;
    } complex_struct;

    funcdef one_simple_param(int val) returns (int);
    
    funcdef nothing() returns ();

    funcdef one_complex_param(complex_struct val2) returns (complex_struct);

    funcdef many_simple_params(int val1, float val2, string val3, tuple<list<mapping<string,int>>,mapping<string,list<float>>> val4) returns (int, float, string, tuple<list<mapping<string,int>>,mapping<string,list<float>>>);

    funcdef many_complex_params(simple_struct simple_val, complex_struct complex_val) returns (simple_struct, complex_struct);
    
    funcdef with_auth(int val1) returns (int) authentication required;
};
