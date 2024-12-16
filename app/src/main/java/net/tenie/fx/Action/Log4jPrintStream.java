package net.tenie.fx.Action;

import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4jPrintStream extends PrintStream {

    private Logger log = LogManager.getLogger(System.out);
    private Logger log_err = LogManager.getLogger(System.err);
    private static PrintStream instance = new Log4jPrintStream(System.out);
    private static PrintStream instance_err = new Log4jPrintStream(System.err);

    private Log4jPrintStream(OutputStream out) {
        super(out);

    }

    public static void redirectSystemOut() {
        System.setOut(instance);
        System.setErr(instance_err);
    }

    @Override
    public void print(boolean b) {
        println(b);
    }

    @Override
    public void print(char c) {
        println(c);
    }

    @Override
    public void print(char[] s) {
        println(s);
    }

    @Override
    public void print(double d) {
        println(d);
    }

    @Override
    public void print(float f) {
        println(f);
    }

    @Override
    public void print(int i) {
        println(i);
    }

    @Override
    public void print(long l) {
        println(l);
    }

    @Override
    public void print(Object obj) {
        println(obj);
    }

    @Override
    public void print(String s) {
        println(s);
    }

    @Override
    public void println(boolean x) {
        log.debug(Boolean.valueOf(x));
        log_err.debug(Boolean.valueOf(x));
    }

    @Override
    public void println(char x) {
        log.debug(Character.valueOf(x));
        log_err.debug(Character.valueOf(x));
    }

    @Override
    public void println(char[] x) {
        log.debug(x == null ? null : new String(x));
        log_err.debug(x == null ? null : new String(x));
    }

    @Override
    public void println(double x) {
        log.debug(Double.valueOf(x));
        log_err.debug(Double.valueOf(x));
    }

    @Override
    public void println(float x) {
        log.debug(Float.valueOf(x));
        log_err.debug(Float.valueOf(x));
    }

    @Override
    public void println(int x) {
        log.debug(Integer.valueOf(x));
        log_err.debug(Integer.valueOf(x));
    }

    @Override
    public void println(long x) {
        log.debug(x);
        log_err.debug(x);
    }

    @Override
    public void println(Object x) {
        log.debug(x);
        log_err.debug(x);
    }

    @Override
    public void println(String x) {
        log.debug(x);
        log_err.debug(x);
    }

}