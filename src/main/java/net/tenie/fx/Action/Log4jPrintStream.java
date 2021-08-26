package net.tenie.fx.Action;

import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public 
class Log4jPrintStream extends PrintStream {   
	
	private Logger log =  LogManager.getLogger(System.out);
	private Logger log_err =  LogManager.getLogger(System.err);
    private static PrintStream instance = new Log4jPrintStream(System.out);  
    private static PrintStream instance_err = new Log4jPrintStream(System.err);  
  
    private Log4jPrintStream(OutputStream out) {  
        super(out);  
        
    }  
  
    public static void redirectSystemOut() {  
        System.setOut(instance);  
        System.setErr(instance_err);
    }  
  
    public void print(boolean b) {  
        println(b);  
    }  
  
    public void print(char c) {  
        println(c);  
    }  
  
    public void print(char[] s) {  
        println(s);  
    }  
  
    public void print(double d) {  
        println(d);  
    }  
  
    public void print(float f) {  
        println(f);  
    }  
  
    public void print(int i) {  
        println(i);  
    }  
  
    public void print(long l) {  
        println(l);  
    }  
  
    public void print(Object obj) {  
        println(obj);  
    }  
  
    public void print(String s) {  
        println(s);  
    }  
  
    public void println(boolean x) {  
        log.debug(Boolean.valueOf(x));  
        log_err.debug(Boolean.valueOf(x));  
    }  
  
    public void println(char x) {  
        log.debug(Character.valueOf(x));  
        log_err.debug(Character.valueOf(x));  
    }  
  
    public void println(char[] x) {  
        log.debug(x == null ? null : new String(x));  
        log_err.debug(x == null ? null : new String(x));  
    }  
  
    public void println(double x) {  
        log.debug(Double.valueOf(x));  
        log_err.debug(Double.valueOf(x));  
    }  
  
    public void println(float x) {  
        log.debug(Float.valueOf(x));  
        log_err.debug(Float.valueOf(x));  
    }  
  
    public void println(int x) {  
        log.debug(Integer.valueOf(x));  
        log_err.debug(Integer.valueOf(x));  
    }  
  
    public void println(long x) {  
        log.debug(x);  
        log_err.debug(x);  
    }  
  
    public void println(Object x) {  
        log.debug(x);  
        log_err.debug(x);  
    }  
  
    public void println(String x) {  
        log.debug(x);  
        log_err.debug(x);  
    }  
  
}  