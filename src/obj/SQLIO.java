package obj;

import java.io.*;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import obj.restraint.BaseRestraint;

public interface SQLIO{

	/**
	 * 获取模板文件 
	 * @return OutputStream
	 */
	public void getModelExcel(Class<? extends Base> clazz,
			Collection<Field> displayFields,
			OutputStream out) throws IOException ;
	
	
	/**
	 * 在输入流中按照标签读取excel表格内容，形成T的列表
	 * T extends Base
	 * @param clazz
	 * @param in
	 * @param error
	 * @param year
	 * @return List<T>
	 * @throws IOException
	 * @throws EncryptedDocumentException
	 * @throws InvalidFormatException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T extends Base>
	List<T> readExcel(Class<T> clazz, InputStream in,List<Integer> error,BaseRestraint restraint)
			throws IOException, EncryptedDocumentException, InvalidFormatException, InstantiationException, IllegalAccessException;
	
	
	
	
	
	
}
