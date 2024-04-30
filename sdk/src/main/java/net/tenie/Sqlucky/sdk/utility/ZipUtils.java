package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	public static void zipFile(String sourceFile, String savePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(savePath);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		File fileToZip = new File(sourceFile);
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
		zipOut.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}

		zipOut.close();
		fis.close();
		fos.close();
	}

	public static void zipMultipleFiles(List<String> srcFiles, String savePath) throws IOException {
		final FileOutputStream fos = new FileOutputStream(savePath);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		for (String srcFile : srcFiles) {
			File fileToZip = new File(srcFile);
			FileInputStream fis = new FileInputStream(fileToZip);
			ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
			zipOut.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
			fis.close();
		}

		zipOut.close();
		fos.close();
	}

	public static void ZipDirectory(String sourceFile, String savePath) throws IOException {

		FileOutputStream fos = new FileOutputStream(savePath);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		File fileToZip = new File(sourceFile);
		zipFile(fileToZip, fileToZip.getName(), zipOut);
		zipOut.close();
		fos.close();
	}
	/**
	 * 
	 * @param fileToZip
	 * @param fileName
	 * @param zipOut
	 * @throws IOException
	 */
	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

	/**
	 * Append New Files to Zip File
	 * 把文件添加到 zip中
	 * @param appFile
	 * @param zipPath
	 * @throws IOException
	 */
	public static void zipfileAppendFile(String appFile, String zipPath) throws IOException {
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");

		Path path = Paths.get(zipPath);
		URI uri = URI.create("jar:" + path.toUri());

		try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
			File tempFile = new File(appFile.trim());
			String tmpfileName = tempFile.getName();

			Path nf = fs.getPath(tmpfileName);
			Files.write(nf, Files.readAllBytes(Paths.get(appFile)), StandardOpenOption.CREATE);
		}
	}

	/**
	 * 解压
	 * 
	 * @param fileZip
	 * @param desDirPath
	 * @throws IOException
	 */
	public static void UnzipFile(String fileZip, String desDirPath) throws IOException {
		File destDir = new File(desDirPath);

		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			File newFile = newFile(destDir, zipEntry);
			if (zipEntry.isDirectory()) {
				if (!newFile.isDirectory() && !newFile.mkdirs()) {
					throw new IOException("Failed to create directory " + newFile);
				}
			} else {
				// fix for Windows-created archives
				File parent = newFile.getParentFile();
				if (!parent.isDirectory() && !parent.mkdirs()) {
					throw new IOException("Failed to create directory " + parent);
				}

				// write file content
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			zipEntry = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
	}

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}
}
