package obj;

import java.io.*;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public interface SQLIO{
	
	/**
	 * 获取模板文件 
	 * @return OutputStream
	 */
	public void getModelExcel(Class<? extends Base> clazz,OutputStream out) throws IOException ;
	
	/**
	 * 在输入流中按照标签读取excel表格内容，形成String[]的列表
	 * @param in
	 * @param labels
	 * @return List<String[]>
	 * @throws IOException
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 */
	public List<String[]> readExcel(InputStream in,String[] labels)
			throws IOException, EncryptedDocumentException, InvalidFormatException;
	
	
	
	
	
	
}
