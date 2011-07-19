/**
 * jpi doesn't indicate an exception, but the advice related with it yes. 
 * Through an static analysis is it possible say that the program is safe. Is it possible?
 */

import java.io.*;

jpi void JP();
jpi void JP2();
jpi void JP3() throws Exception;
jpi void JP4() throws Exception;
jpi void JP5() throws IOException;

aspect A{

    void around JP(){ //ok
        proceed();
    }
    
    void around JP2() throws IOException{ // error: IOException not allowed for JP2
    	proceed();
    }

    void around JP3() throws IOException{
    	proceed(); //error, can't raise Exception
    }
    
    void around JP4() throws IOException{
    	try{
    		proceed();
    	}
    	catch(Exception e){
    		throw new IOException(); //ok
    	}
    }

    void around JP5() throws Exception{ //error, Exception not allowed for JP5
    	proceed(); //ok
    }
    
    void around JP6() throws Exception{ //error, Exception not allowed for JP5
    	throw new Exception(); //error, base code doesn't handle Exception.
    }
    
}
