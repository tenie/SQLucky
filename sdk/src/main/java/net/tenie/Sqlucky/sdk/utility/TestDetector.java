package net.tenie.Sqlucky.sdk.utility;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.mozilla.universalchardet.UniversalDetector;
import org.mozilla.universalchardet.ReaderFactory;

public class TestDetector {
	public static void main2(String[] args) throws IOException {
		byte[] buf = new byte[4096];
		java.io.InputStream fis = java.nio.file.Files.newInputStream(
				java.nio.file.Paths.get("D:\\ft\\trunk\\InfoDMS_Src\\src\\Balance\\F_bsAccountPayable.pas"));

		// (1)
		UniversalDetector detector = new UniversalDetector();

		// (2)
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		// (3)
		detector.dataEnd();

		// (4)
		String encoding = detector.getDetectedCharset();
		if (encoding != null) {
			System.out.println("Detected encoding = " + encoding);
		} else {
			System.out.println("No encoding detected.");
		}

		// (5)
		detector.reset();
	}

	public static void main3(String[] args) throws java.io.IOException {
		java.io.File file = new java.io.File("D:\\ft\\trunk\\InfoDMS_Src\\src\\Balance\\F_bsAccountPayable.pas");
		String encoding = UniversalDetector.detectCharset(file);
		if (encoding != null) {
			System.out.println("Detected encoding = " + encoding);
		} else {
			System.out.println("No encoding detected.");
		}
	}

	public static void main(String[] args) throws java.io.IOException {

		java.io.Reader reader = null;
		Writer writer = new StringWriter();
		try {
			java.io.File file = new java.io.File("D:\\ft\\trunk\\InfoDMS_Src\\src\\Balance\\F_bsAccountPayable.pas");
			reader = ReaderFactory.createBufferedReader(file);
			char[] buffer = new char[1024];

			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
			String jsonString = writer.toString();
			System.out.println(jsonString);
			// Do whatever you want with the reader
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

	}
}
