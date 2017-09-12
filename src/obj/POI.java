package obj;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import obj.annualTable.AnnualBase;

@SuppressWarnings("unchecked")
public class POI implements SQLIO{
	
	private Field[] getImportFields(Class<? extends Base> clazz){
		List<Field> fs=new ArrayList<Field>();
		for(Class<? extends Base> c=clazz;c!=Base.class;c=(Class<? extends Base>)c.getSuperclass()) for(Field f:c.getDeclaredFields()){
			f.setAccessible(true);
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			if(!s.needImport()) continue;
			fs.add(f);
		}
		Field[] res=new Field[fs.size()];
		return fs.toArray(res);
	}
	
	static public CellStyle getCellStyle(Workbook wb,String fontName,int fontSize,boolean bold,HorizontalAlignment h){
		CellStyle res=wb.createCellStyle();
	//	res.setFillForegroundColor((short)14);	//设置背景色
	//	res.setFillPattern(CellStyle.SOLID_FOREGROUND);	//设置背景填充方式
		res.setBorderBottom(BorderStyle.THIN);	//边框
		res.setBorderLeft(BorderStyle.THIN);	//边框
		res.setBorderRight(BorderStyle.THIN);	//边框
		res.setBorderTop(BorderStyle.THIN);		//边框
		res.setAlignment(h);	//居中  
		res.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中 
		Font font=wb.createFont();
		font.setFontName(fontName);
		font.setFontHeightInPoints((short)fontSize);//按字号设置
		font.setBold(bold);//加粗
		res.setFont(font);
		res.setWrapText(true);//设置自动换行
		return res;
	}

	@Override
	public void getModelExcel(Class<? extends Base> clazz,OutputStream out) throws IOException {
		Field[] fs=this.getImportFields(clazz);
		//创建excel工作簿
		Workbook wb=null;
		try{
			wb=new XSSFWorkbook();
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		//创建第一个sheet并命名
		Sheet st=wb.createSheet("importSheet");
		CellStyle styleTitle=POI.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER);
		CellStyle styleContent=POI.getCellStyle(wb,"宋体",10,false,HorizontalAlignment.CENTER);
		//列宽，第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2012-08-10"的宽度为2500
//		st.setColumnWidth(0,2500);
		//行高
		for(int r=0,rindex=0;r<3;r++,rindex++){
			Row row=st.createRow(rindex);
			row.setHeight((short)-1);
			boolean flag=true;
			for(int i=0;i<fs.length;i++){
				Cell cell=row.createCell(i);
				SQLField s=fs[i].getAnnotation(SQLField.class);
				if(s==null) continue;
				cell.setCellType(CellType.STRING);
				cell.setCellValue(r==0?fs[i].getName()
						:(r==1?Base.getSQLFieldName(fs[i]):""));
				cell.setCellStyle(r==0?styleTitle:styleContent);
				System.out.print(cell.getStringCellValue()+"\t");
				if(r==0&&flag && !s.description().isEmpty()) flag=false;
			}if(r==0 && flag) r++;//略过description
			System.out.println();
		}
/*	//	用作debug
		File f=new File("/Users/diandianla/Hus/work/wfy教务处工作/J教育实习/2014级2017年/北京师范大学免费师范生实习管理系统/Student模板.xlsx");
		FileOutputStream o=new FileOutputStream(f);
		wb.write(o);
		o.flush();
		o.close();//*/
        wb.write(out);
	}

	@Override
	public <T extends Base>
	List<T> readExcel(Class<T> clazz,InputStream in,List<Integer> error,int year) throws IOException, EncryptedDocumentException, InvalidFormatException, InstantiationException, IllegalAccessException{
		List<T> res=new ArrayList<T>();
		Map<Short,Field> fsMap=new HashMap<Short,Field>();
		try(Workbook wb=new XSSFWorkbook(in);){
			Sheet st=wb.getSheetAt(0);
			int title=-1;
			for(Row row:st){
				title++;
				if(title==0){
					short first=row.getFirstCellNum();
					short last=row.getLastCellNum();
					for(short i=first;i<last;i++){
						Cell c=row.getCell(i);
						if(c==null) continue;
						c.setCellType(CellType.STRING);
						String fieldName=c.getStringCellValue();
						try {
							fsMap.put(i,Base.getField(clazz,fieldName));
						} catch (NoSuchFieldException e) {
						}
					}
				}else if(title>=2){
					T t=clazz.newInstance();
					if(t!=null) for(Map.Entry<Short,Field> e:fsMap.entrySet()){
						Cell c=row.getCell(e.getKey());
						c.setCellType(CellType.STRING);
						String fieldValue=c.getStringCellValue();
						Field field=e.getValue();
						field.setAccessible(true);
						try {
							t.setFieldValueBySetter(field,fieldValue);
						} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException
								| SecurityException e1) {
							e1.printStackTrace();
							t=null;
							break;
						}
					}
					if(t instanceof AnnualBase)
						((AnnualBase)t).setYear(year);
					if(t==null || t.checkKeyNull()){
						 if(error!=null)
							 error.add(row.getRowNum());
					}else
						res.add(t);
				}
			}
		}
		return res;
	}
	
	
	
	
	
}
