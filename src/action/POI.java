package action;

import java.io.*;
import java.util.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import obj.Base;
import obj.Field;
import obj.Restraint;
import obj.SQLIO;
import obj.annualTable.Region;
import obj.annualTable.Student;
import obj.restraint.BaseRestraint;
import obj.staticObject.InnerPerson;
import obj.staticObject.PracticeBase;
import obj.staticSource.Major;

public class POI implements SQLIO, SpecialIO{
	static final public short HeightRatio=20;
	static public short getHeight(int x) {
		return (short)(x<0?-1:(x*HeightRatio));
	}
	static public void setHeight(Row row,int x) {
		if(row!=null) row.setHeight(getHeight(x));
	}
	static public CellStyle getCellStyle(Workbook wb,String fontName,int fontSize,boolean bold,HorizontalAlignment h,BorderStyle border){
		CellStyle res=wb.createCellStyle();
	//	res.setFillForegroundColor((short)14);	//设置背景色
	//	res.setFillPattern(CellStyle.SOLID_FOREGROUND);	//设置背景填充方式
		res.setBorderBottom(border);	//边框
		res.setBorderLeft(border);	//边框
		res.setBorderRight(border);	//边框
		res.setBorderTop(border);		//边框
		res.setAlignment(h);	//居中  
		res.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中 
		Font font=wb.createFont();
		font.setFontName(fontName);
		font.setFontHeightInPoints((short)fontSize);//按字号设置
		font.setBold(bold);//加粗
		res.setFont(font);
		res.setWrapText(false);//设置自动换行
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
			throw new IOException(e.getMessage());
		}
		//创建第一个sheet并命名
		Sheet st=wb.createSheet("importSheet");
		CellStyle styleTitle=POI.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER,BorderStyle.THIN);
		CellStyle styleContent=POI.getCellStyle(wb,"宋体",10,false,HorizontalAlignment.CENTER,BorderStyle.THIN);
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
					//	(r==2?f.getPs():
							""
					//	)
						);
				cell.setCellStyle(r<rmax-1 ? styleTitle : styleContent);
				System.out.print(cell.getStringCellValue()+"\t");
			}
			System.out.println();
		}
		//列宽，第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2012-08-10"的宽度为2500
//		st.setColumnWidth(0,2500);
		for(int i=0;i<displayFields.size();i++) st.autoSizeColumn(i,true);
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
	List<T> readExcel(Class<T> clazz,Collection<Field> displayFields,InputStream in,List<String> error,BaseRestraint restraint) throws IOException, EncryptedDocumentException, InvalidFormatException, InstantiationException, IllegalAccessException{
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
						if(field!=null && displayFields.contains(field))
							fsMap.put(i,field);
					}
				}else{//对应Content
					T t=clazz.newInstance();
					if(t!=null) for(Map.Entry<Short,Field> entry:fsMap.entrySet()){
						Cell c=null;
						try{c=row.getCell(entry.getKey());
							c.setCellType(CellType.STRING);
						}catch(Exception e) {
							c=null;
						}
						String fieldValue=c==null?null:c.getStringCellValue();
						Field field=entry.getValue();
						try {
							field.setBySetter(t,fieldValue);
						} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
							e.printStackTrace();
							t=null;
							break;
						}
					}
					String msg="读取成功!";
					if(t==null || !t.checkNotNullField()) {
						msg="字段不全!";
						t=null;
					}else if(restraint!=null&& !restraint.checkBase(t,false)){
						msg="字段有误!";
						t=null;
					}
					if(error!=null)
						error.add(msg);
					res.add(t);
				}
			}
		}
		return res;
	}


	
	@Override
	public String createStudentList(int year,PracticeBase pb, List<Student> students, String majorName, OutputStream out) throws IOException {
		if(pb==null) throw new IOException("大区为空!");
		Region region;
		try{
			region=Base.list(Region.class,new Restraint(Field.getFields(Region.class,"year","practiceBase"),new Object[] {year,pb.getName()}))
					.get(0);
		}catch(Exception e) {
			throw new IOException("未找到实习基地所在大区!("+e.getMessage()+")");
		}
		String name=String.format("%d年[%s]%s免费师范生教育实习学生名单",
				year,pb.getName(),
				majorName==null?"":("("+majorName+")")
						);
		//创建excel工作簿
		Workbook wb=null;
		try{
			wb=new XSSFWorkbook();
		}catch(Exception e){
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		//创建第一个sheet并命名
		Sheet st=wb.createSheet("importSheet");
		CellStyle styleBigTitle=POI.getCellStyle(wb,"宋体",18,true,HorizontalAlignment.CENTER,BorderStyle.NONE);
		CellStyle styleSmallTitle=POI.getCellStyle(wb,"宋体",12,true,HorizontalAlignment.CENTER,BorderStyle.NONE);
		CellStyle styleTitle=POI.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER,BorderStyle.NONE);
		CellStyle styleContent=POI.getCellStyle(wb,"宋体",10,false,HorizontalAlignment.CENTER,BorderStyle.HAIR);
		//设置列数
		final Field[] fs=Field.getFields(Student.class,"name","zzmm","nation","mobile","email","recommend");
		final int column=fs.length+2;
		
		Row row;
		Cell cell;
		int r=0;
		/*第一行标题*/row=st.createRow(r);
		setHeight(row,40);
		cell=row.createCell(0);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(year+"年免费师范生教育实习学生名单");
		cell.setCellStyle(styleBigTitle);
		for(int i=1;i<column;i++) row.createCell(i).setCellStyle(styleBigTitle);
		st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
		r++;
		/*第二行实习基地名称*/row=st.createRow(r);
		setHeight(row,30);
		cell=row.createCell(0);
		cell.setCellType(CellType.STRING);
		cell.setCellValue("实习基地："+pb.getName());
		cell.setCellStyle(styleSmallTitle);
		for(int i=1;i<column;i++) row.createCell(i).setCellStyle(styleSmallTitle);
		st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
		r++;
		/*第三行开始每个专业实习生列表*/
		List<Major> majors=new ArrayList<Major>();
		try {
			majors.addAll(Base.list(Major.class));
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			throw new IOException(e.getMessage());
		}
		Collections.sort(majors,new Comparator<Major>() {
			public int compare(Major a,Major b) {
				int cmp=a.getSchool().compareTo(b.getSchool());
				return cmp==0?(a.getName().compareTo(b.getName())):cmp;
			}
		});
		for(Major major:majors) {
			Set<InnerPerson> teacher=new HashSet<InnerPerson>();
			for(Student s:students) if(s.getMajor().equals(major.getName())) try{
				teacher.add(new InnerPerson(s.getTeacherId()));
			}catch(IllegalArgumentException | SQLException e) {
				System.err.println(s.getName()+"没有指导老师！("+s.toString()+")");
			}
			if(teacher.isEmpty()) continue;
			/*空白行*/row=st.createRow(r);
			setHeight(row,10);
			st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
			r++;
			/*专业名称*/row=st.createRow(r);
			setHeight(row,20);
			cell=row.createCell(0);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(String.format("%s  %s专业",
					major.getSchool(),major.getName()));
			cell.setCellStyle(styleTitle);
			for(int i=1;i<column;i++) row.createCell(i).setCellStyle(styleTitle);
			st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
			r++;
			for(InnerPerson t:teacher) {
				/*指导教师*/row=st.createRow(r);
				row.setHeight((short)-1);
				cell=row.createCell(0);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(String.format("指导老师：%s  %s  %s",
						t.getName(),t.getMobile(),t.getEmail()));
				cell.setCellStyle(styleContent);
				for(int i=1;i<column;i++) row.createCell(i).setCellStyle(styleContent);
				st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
				r++;
			}
			/*学生抬头*/row=st.createRow(r);
			row.setHeight((short)-1);
			for(int i=0;i<column;i++) {
				cell=row.createCell(i);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(i==0?"实习基地大组长":i==2?"性别":
					(fs[i-1-(i>2?1:0)].getDescription()));
				cell.setCellStyle(styleContent);
			}
			r++;
			for(Student s:students) if(s.getMajor().equals(major.getName())) {
				/*学生内容*/row=st.createRow(r);
				row.setHeight((short)-1);
				boolean groupLeader=
						s.getId()!=null && s.getId().equals(region.getStudentGroupLeaderId());
				for(int i=0;i<column;i++) {
					cell=row.createCell(i);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(i==0?(groupLeader?"✔":""):i==2?s.getSex():
						Field.o2s(fs[i-1-(i>2?1:0)].get(s),"","✔",""));
					cell.setCellStyle(styleContent);
				}
				r++;
			}
		}
		//列宽，第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2012-08-10"的宽度为2500
//		st.setColumnWidth(0,2500);
		for(int i=0;i<column;i++) st.autoSizeColumn(i,true);
        wb.write(out);
		return name+".xlsx";//下载文件名称
	}


	@Override
	public void createPracticeBaseFile(PracticeBase pb, List<Student> students, OutputStream stream)
			throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
