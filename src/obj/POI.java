package obj;

import java.io.*;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import obj.restraint.BaseRestraint;

public class POI implements SQLIO{
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
	
	
	private int getTitleRowCounts(Class<? extends Base> clazz){
		int rmax=3;//略过Ps行
	/*	for(Field f:Field.getFields(clazz)) if(!f.autoInit()){
			if(f.getPs()!=null && !f.getPs().isEmpty()){
				rmax++;break;//不略过Ps行
			}
		}//*/
		return rmax;
	}

	@Override
	public void getModelExcel(Class<? extends Base> clazz,
			Collection<Field> displayFields,
			OutputStream out) throws IOException {
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
		for(int r=0,rmax=this.getTitleRowCounts(clazz);r<rmax;r++){
			Row row=st.createRow(r);
			row.setHeight((short)-1);
			int i=-1;
			for(Field f:displayFields) if(!f.autoInit()){
				Cell cell=row.createCell(++i);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(r==0?f.getName():
					r==1?(f.getDescription()+(f.notNull()?"[必须]":"")):
						(r==2?f.getPs():
							"")
						);
				cell.setCellStyle(r<rmax-1 ? styleTitle : styleContent);
				System.out.print(cell.getStringCellValue()+"\t");
			}
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
	List<T> readExcel(Class<T> clazz,InputStream in,List<Integer> error,BaseRestraint restraint) throws IOException, EncryptedDocumentException, InvalidFormatException, InstantiationException, IllegalAccessException{
		List<T> res=new ArrayList<T>();
		Map<Short,Field> fsMap=new HashMap<Short,Field>();
		try(Workbook wb=new XSSFWorkbook(in);){
			Sheet st=wb.getSheetAt(0);
			int title=0-this.getTitleRowCounts(clazz);
			for(Row row:st){
				title++;
				if(title<0){//对应Title
					short first=row.getFirstCellNum();
					short last=row.getLastCellNum();
					for(short i=first;i<last;i++){
						Cell c=row.getCell(i);
						if(c==null) continue;
						c.setCellType(CellType.STRING);
						String fieldName=c.getStringCellValue();
						Field field=Field.getField(clazz,fieldName);
						if(field!=null)
							fsMap.put(i,field);
					}
				}else{//对应Content
					T t=clazz.newInstance();
					if(t!=null) for(Map.Entry<Short,Field> entry:fsMap.entrySet()){
						Cell c=row.getCell(entry.getKey());
						c.setCellType(CellType.STRING);
						String fieldValue=c.getStringCellValue();
						Field field=entry.getValue();
						try {
							field.setBySetter(t,fieldValue);
						} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
							e.printStackTrace();
							t=null;
							break;
						}
					}
					if(t==null || t.checkNotNullField()
							|| restraint!=null&& !restraint.checkBase(t,false)){
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
