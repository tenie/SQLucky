package net.tenie.fx.Action;

import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.tenie.fx.main.MainMyDB;

public class LogSystem {
	static {
		Log4jPrintStream.redirectSystemOut();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.print("abc");
			System.out.print(i);
			System.out.print((int) (Math.random() * 100));
		}
	}
}
