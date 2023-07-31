package net.tenie.Sqlucky.sdk.utility;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @description 文件的分割与合并
 */
public class SplitAndMergeFile {
	
	/**
	 * 
	 * @Description:   文件的分割
	 * @param:         @param splitFile 需要分割的文件
	 * @param:         @param splitSmallFilesDir 分割后的小文件位置
	 * @param:         @param splitSmallFileSize 每个切割的小文件大小
	 * @param:         @return      
	 * @return:        List<String> 所有分割的小文件
	 * @throws
	 */
	public List<String> split(File splitFile,String splitSmallFilesDir,long splitSmallFileSize) {

		//被分割文件总大小
		long splitFileSize=splitFile.length();
		//每个小文件分割的起始位置
		long splitSmallFileBeginPos=0;
		//实际分割的小文件大小
		long splitSmallFileActualSize=splitSmallFileSize>splitFileSize?splitFileSize:splitSmallFileSize;
		//被分割的小文件个数
		int size=(int)Math.ceil(splitFileSize*1.0/splitSmallFileSize);
		//被分割的小文件路径list
		List<String> splitSmallFileList=new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			splitSmallFileList.add(splitSmallFilesDir+"/"+i+"_"+splitFile.getName());
		}
		
		//切割文件
		for (int i = 0; i < size; i++) {
			//切割起始位置
			splitSmallFileBeginPos=i*splitSmallFileSize;
			//切割到最后一个小文件
			if (i==size-1) {
				//切割的实际大小
				splitSmallFileActualSize=splitFileSize;
			}else {//否则
				//切割的实际大小
				splitSmallFileActualSize=splitSmallFileSize;
				//源文件减小
				splitFileSize-=splitSmallFileActualSize;
			}
			
			//具体的切割
			try {
				//源文件
				RandomAccessFile splitRandomAccessFile=new RandomAccessFile(splitFile, "r");
				//被分割的小文件
				RandomAccessFile splitSmallRandomAccessFile=new RandomAccessFile(splitSmallFileList.get(i), "rw");
				
				//从源文件的哪个位置读取
				splitRandomAccessFile.seek(splitSmallFileBeginPos);
				
				//分段读取
				//10字节的缓存
				byte[] cache=new byte[1024*10];
				int len=-1;
				while((len=splitRandomAccessFile.read(cache))!=-1) {
					//小文件实际分割大小>len
					if (splitSmallFileActualSize>len) {
						splitSmallRandomAccessFile.write(cache,0,len);
						splitSmallFileActualSize-=len;
					}else {//小文件实际分割大小<len，写完数据后跳出循环
						splitSmallRandomAccessFile.write(cache,0,(int)splitSmallFileActualSize);
						break;
					}
				}
				
				splitRandomAccessFile.close();
				splitSmallRandomAccessFile.close();
				
			} catch (FileNotFoundException e) {
				throw new RuntimeException("文件未找到",e);
			} catch (IOException e) {
				throw new RuntimeException("文件传输异常",e);
			}
			
		}
		
		return splitSmallFileList;
	}
	
	/**
	 * 
	 * @Description:   合并文件  
	 * @param:         @param splitSmallFileList 小文件列表
	 * @param:         @param mergeFileDir 合并的文件存储的文件夹
	 * @param:         @param mergeFileName 合并的文件新名    
	 * @return:        String 合并的文件路径
	 * @throws
	 */
	public String merge(List<String> splitSmallFileList,String mergeFileDir,String mergeFileName) {
		
		try {
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append(mergeFileDir).append("/").append(mergeFileName);
			String newMergeFile=stringBuilder.toString();
			//输出流，写文件,true表示追加写而不覆盖
			OutputStream outputStream=new BufferedOutputStream(new FileOutputStream(newMergeFile,true));
			//输入流，读文件
			Vector<InputStream> vector=new Vector<InputStream>();
			for (int i = 0; i < splitSmallFileList.size(); i++) {
				vector.add(new BufferedInputStream(new FileInputStream(splitSmallFileList.get(i))));
			}
			//SequenceInputStream，实现批量输入流的按序列读
			SequenceInputStream sequenceInputStream=new SequenceInputStream(vector.elements());
			//10字节的缓存
			byte[] cache=new byte[1024*10];
			int len=-1;
			while ((len=sequenceInputStream.read(cache))!=-1) {
				//分段写
				outputStream.write(cache,0,len);
			}
			//强制将所有缓冲的输出字节被写入磁盘，更可靠
			outputStream.flush();
			outputStream.close();
			sequenceInputStream.close();
			
			//返回新合成的文件
			return newMergeFile;
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException("文件未找到",e);
		} catch (IOException e) {
			throw new RuntimeException("文件传输异常",e);
		}
		
	}


	public static void main(String[] args) {
		
		SplitAndMergeFile splitAndMergeFile=new SplitAndMergeFile();
		
		//文件分割
		List<String> splitList = splitAndMergeFile.split(new File("mat.png"), "splitSmallFilesDir", 1024);
		System.out.println(splitList);
		
		//文件合并
		String newMergeFile = splitAndMergeFile.merge(splitList, "mergeFileDir", "matMerge.png");
		System.out.println(newMergeFile);
		

	}

}

