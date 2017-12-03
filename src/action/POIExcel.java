package action;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import obj.*;
import obj.annualTable.*;
import obj.restraint.BaseRestraint;
import obj.staticObject.*;
import obj.staticSource.*;

public class POIExcel implements SQLIO, SpecialExcelIO{

	static final public short HeightRatio=20;
	static final public short WidthRatio=512;
	static public void setHeight(Row row,int x) {
		if(row!=null) row.setHeight((short)(x<0?-1:(x*HeightRatio)));
	}
	static public void setWidth(Sheet st,int column,int width) {
		st.setColumnWidth(column,width*WidthRatio);
	}
	static public CellStyle getCellStyle(Workbook wb,String fontName,int fontSize,boolean bold,HorizontalAlignment h,BorderStyle border,boolean wrapText,Short bg){
		CellStyle res=wb.createCellStyle();
		if(bg!=null){
			res.setFillForegroundColor(bg);	//设置背景色
			res.setFillPattern(CellStyle.SOLID_FOREGROUND);	//设置背景填充方式
		}
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
		res.setWrapText(wrapText);//设置自动换行
		return res;
	}
	
	static public void debug(Workbook wb,String name) throws IOException{
	/*	File f=new File("C:\\Users\\12132\\Desktop\\"+name+".xlsx");
		FileOutputStream o=new FileOutputStream(f);
		wb.write(o);
		o.flush();
		o.close();//*/
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
	public String getModelExcel(Class<? extends Base> clazz,
			Collection<Field> displayFields,
			OutputStream out) throws IOException {
		String name=Base.getSQLTableName(clazz)+"模板";
		try(Workbook wb=new XSSFWorkbook();){
			//创建第一个sheet并命名
			Sheet st=wb.createSheet("importSheet");
			CellStyle styleTitle=POIExcel.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER,BorderStyle.THIN,false,null);
			CellStyle styleContent=POIExcel.getCellStyle(wb,"宋体",10,false,HorizontalAlignment.CENTER,BorderStyle.THIN,false,null);
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
			for(int i=0;i<displayFields.size();i++) st.autoSizeColumn(i,true);
			debug(wb,name);
	        wb.write(out);			
		}
        return name+".xlsx";
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
	public String createStudentList(int year,PracticeBase pb, String majorName, OutputStream out) throws IOException {
		if(pb==null) throw new IOException("大区为空!");
		if(majorName==null || majorName.isEmpty()) majorName=null;
		Region region;
		try{
			region=Base.list(Region.class,new Restraint(Field.getFields(Region.class,"year","practiceBase"),new Object[] {year,pb.getName()}))
					.get(0);
		}catch(Exception e) {
			throw new IOException("未找到实习基地所在大区!("+e.getMessage()+")");
		}
		Map<Major,Pair<Set<InnerPerson>,List<Student>>> list=new TreeMap<Major,Pair<Set<InnerPerson>,List<Student>>>();
		Restraint restraint=majorName==null?new Restraint(Field.getField(Student.class,"year"),year)
				:new Restraint(Field.getFields(Student.class,"year","major"),new Object[]{year,majorName});
		try{
			for(Student stu:Base.list(Student.class,restraint)) try{
				Major m=new Major(stu.getMajor());
				InnerPerson t=new InnerPerson(stu.getTeacherId());
				if(!list.containsKey(m))
					list.put(m,new Pair<Set<InnerPerson>,List<Student>>(new HashSet<InnerPerson>(),new ArrayList<Student>()));
				Pair<Set<InnerPerson>,List<Student>> p=list.get(m);
				p.getKey().add(t);
				p.getValue().add(stu);
			}catch(IllegalArgumentException | SQLException e) {
				System.err.println(stu.getName()+"没有指导老师！("+stu.toString()+")");
			}
		}catch(IllegalArgumentException | SQLException | InstantiationException e) {
			System.err.println("读取实习生列表失败！");
		}
		String name=String.format("%d年[%s]%s免费师范生教育实习学生名单",
				year,pb.getName(),
				majorName==null?"":("("+majorName+")")
						);
		try(Workbook wb=new XSSFWorkbook();){
			Sheet st=wb.createSheet("学生名单");
			CellStyle styleBigTitle=POIExcel.getCellStyle(wb,"宋体",18,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
			CellStyle styleSmallTitle=POIExcel.getCellStyle(wb,"宋体",12,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
			CellStyle styleTitle=POIExcel.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
			CellStyle styleContent=POIExcel.getCellStyle(wb,"宋体",10,false,HorizontalAlignment.CENTER,BorderStyle.HAIR,false,null);
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
			for(Map.Entry<Major,Pair<Set<InnerPerson>,List<Student>>> entry:list.entrySet()) {
				if(entry.getValue().getKey().isEmpty()) continue;
				/*空白行*/row=st.createRow(r);
				setHeight(row,10);
				st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
				r++;
				/*专业名称*/row=st.createRow(r);
				setHeight(row,20);
				cell=row.createCell(0);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(String.format("%s  %s专业",
						entry.getKey().getSchool(),entry.getKey().getName()));
				cell.setCellStyle(styleTitle);
				for(int i=1;i<column;i++) row.createCell(i).setCellStyle(styleTitle);
				st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
				r++;
				for(InnerPerson t:entry.getValue().getKey()) {
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
				for(Student s:entry.getValue().getValue()){
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
			debug(wb,name);
	        wb.write(out);
		}
		return name+".xlsx";//下载文件名称
	}
	
	
	@Override
	public String createPlanDesign(int year, ListOfPracticeBaseAndStudents list, OutputStream out)
			throws IOException {
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
		List<Major> tmp=new ArrayList<Major>();
		for(int i=0;i<majors.size();i++) {
			Major m=null;
			for(ListOfPracticeBaseAndStudents.RegionPair rp:list.getList()) {
				for(ListOfPracticeBaseAndStudents.RegionPair.PracticeBasePair pair:rp.getList()) {
					for(Student stu:pair.getStudents()) {
						if(majors.get(i).getName().equals(stu.getMajor())) {
							m=majors.get(i);
						}if(m!=null) break;
					}if(m!=null) break;
				}if(m!=null) break;
			}if(m!=null) tmp.add(m);
		}
		for(Major m:tmp)
			majors.remove(m);
		int[] numbers=new int[majors.size()];
		int[] hxNumber=new int[majors.size()];
		int allNumber=0;
		int hxAllNumber=0;
		for(int i=0;i<majors.size();i++) {
			numbers[i]=0;
			for(ListOfPracticeBaseAndStudents.RegionPair rp:list.getList()) {
				for(ListOfPracticeBaseAndStudents.RegionPair.PracticeBasePair pair:rp.getList()) {
					if(pair.getPracticeBase().getHx())
						hxNumber[i]+=pair.getStudents().size();
					for(Student stu:pair.getStudents()) {
						if(majors.get(i).getName().equals(stu.getMajor())) {
							numbers[i]++;
						}
					}
				}
			}
			allNumber+=numbers[i];
			hxAllNumber+=hxNumber[i];
		}
		String name=String.format("%d年免费师范生教育实习布局规划",
				year);
		try(Workbook wb=new XSSFWorkbook();){
			Sheet st=wb.createSheet("布局规划");
			//列：大区、序号、实习基地名称、总人数、各学科人数、指导教师列表
			//行：大标题、列标签、求和列、内容
			CellStyle styleBigTitle=POIExcel.getCellStyle(wb,"宋体",18,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
		//	CellStyle styleSmallTitle=POIExcel.getCellStyle(wb,"宋体",12,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false);
			CellStyle styleBigSum=POIExcel.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER,BorderStyle.HAIR,true,IndexedColors.CORAL.getIndex());
			CellStyle styleSmallSum=POIExcel.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER,BorderStyle.HAIR,true,IndexedColors.ROSE.getIndex());
			CellStyle styleTitle=POIExcel.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER,BorderStyle.HAIR,true,null);
			CellStyle styleContent=POIExcel.getCellStyle(wb,"宋体",10,false,HorizontalAlignment.CENTER,BorderStyle.HAIR,true,null);
			//设置列数
			final int column=4+majors.size()+1;
			Row row;
			Cell cell;
			int r=0;
			/*第一行标题*/row=st.createRow(r);
			setHeight(row,40);
			cell=row.createCell(0);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(name);
			cell.setCellStyle(styleBigTitle);
			for(int i=1;i<column;i++) row.createCell(i).setCellStyle(styleBigTitle);
			st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
			r++;
			/*第二行列标签*/row=st.createRow(r);
			setHeight(row,30);
			for(int i=0;i<column;i++) {
				cell=row.createCell(i);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(i==0?"实习大区":i==1?"序号":
					i==2?"实习基地名称":i==3?"接收人数":
						i<column-1?majors.get(i-3).getSubject():
							"指导老师");
				setWidth(st,i,i==0?4:i==1?2:
					i==2?26:i==3?5:
						i<column-1?4:46);
				cell.setCellStyle(styleTitle);
			}
			r++;
			/*第三行统计人数*/row=st.createRow(r);
			setHeight(row,30);
			for(int i=0;i<column;i++) {
				cell=row.createCell(i);
				cell.setCellType(CellType.STRING);
				cell.setCellStyle(styleTitle);
				if(i<3) {
					if(i==2) st.addMergedRegion(new CellRangeAddress(r,r,0,2));
					if(i>0) continue;
				}
				if(i==column-1) {
					st.addMergedRegion(new CellRangeAddress(r-1,r,i,i));
					continue;
				}
				cell.setCellValue(i<3?("本科生学生人数"+allNumber+"人"):
					i==3?String.valueOf(allNumber):
						String.valueOf(numbers[i-3]));
			}
			r++;
			/*第四行开始每个专业实习生列表*/
			String gProvince=null;
			boolean gHx=false;
			int gCnt=0;
			int gNumber[]=new int[majors.size()];
			for(ListOfPracticeBaseAndStudents.RegionPair rp:list.getList()) {
				PracticeBase pb=rp.getList().get(0).getPracticeBase();
				int statu=0;
				if(gProvince==null) statu=1;
				else if(gHx ^ pb.getHx()) statu=2;
				else if(gHx && pb.getHx() && !gProvince.equals(pb.getProvince())) statu=3;
				else if(!gHx && !pb.getHx() && !gProvince.equals(pb.getProvince())) statu=4;
				if(statu>0) {
					if(statu==2 || statu==3 || statu==4) {
						//(不)回乡实习大区内部变化大区，进行大区汇总
						//不回乡变为回乡实习大区，进行大区汇总
						row=st.createRow(r);
						setHeight(row,-1);
						int num=0;
						for(int i=0;i<gNumber.length;i++)
							num+=gNumber[i];
						for(int i=0;i<column;i++) {
							cell=row.createCell(i);
							cell.setCellType(CellType.STRING);
							cell.setCellStyle(styleSmallSum);
							if(i<3) {
								if(i==2) st.addMergedRegion(new CellRangeAddress(r,r,0,2));
								if(i>0) continue;
							}
							cell.setCellValue(i<3?(gProvince+"小计"):
										i==3?String.valueOf(num):
											i<column-1?String.valueOf(gNumber[i-3]):
												"");
						}
						r++;
					}else if(statu==2){
						//不回乡变为回乡实习大区，进行不回乡汇总
						row=st.createRow(r);
						setHeight(row,-1);
						for(int i=0;i<column;i++) {
							cell=row.createCell(i);
							cell.setCellType(CellType.STRING);
							cell.setCellStyle(styleBigSum);
							if(i<3) {
								if(i==2) st.addMergedRegion(new CellRangeAddress(r,r,0,2));
								if(i>0) continue;
							}
							cell.setCellValue(i<3?(gHx?"error":"北京及周边地区教育实习总计"):
										i==3?String.valueOf(hxAllNumber):
											i<column-1?String.valueOf(hxNumber[i-3]):
												"");
						}
						r++;
					}
					//change the province
					gHx=pb.getHx();
					gProvince=pb.getProvince();
					gCnt=0;
					for(int i=0;i<gNumber.length;i++) gNumber[i]=0;
				}
				int rStart=r;
				for(ListOfPracticeBaseAndStudents.RegionPair.PracticeBasePair pair:rp.getList()) {
					row=st.createRow(r);
					setHeight(row,-1);
					int num[]=new int[majors.size()];
					for(int i=0;i<num.length;i++) {
						num[i]=0;
						for(Student stu:pair.getStudents()) if(majors.get(i).getName().equals(stu.getMajor()))
							num[i]++;
					}
					StringBuilder teachers=new StringBuilder();
					boolean first=true;
					for(Major m:majors) {
						Set<String> ts=new HashSet<String>();
						for(Student stu:pair.getStudents()) if(m.getName().equals(stu.getMajor())) {
							try {
								ts.add(new InnerPerson(stu.getTeacherId()).getName());
							}catch(IllegalArgumentException|SQLException e) {
								System.err.println(stu.getName()+"没有指导老师！("+stu.toString()+")");
							}
						}
						if(ts.isEmpty()) continue;
						if(first) first=false;
						else teachers.append(' ');
						teachers.append(m.getSubject());
						teachers.append('(');
						boolean first2=true;
						for(String t:ts) {
							if(first2) first2=false;
							else teachers.append(',');
							teachers.append(t);
						}
						teachers.append(')');
					}
					for(int i=0;i<column;i++) {
						cell=row.createCell(i);
						cell.setCellType(CellType.STRING);
						cell.setCellStyle(styleContent);
						if(i==0 && r>rStart) continue;
						cell.setCellValue(i==0?rp.getRegion().getName():
							i==1?String.valueOf(gCnt):
								i==2?pair.getPracticeBase().getName():
									i==3?String.valueOf(pair.getStudents().size()):
										i<column-1?String.valueOf(num[i-3]):
											teachers.toString());
					}
					gCnt++;
					r++;
				}
				if(r-1>rStart)
					st.addMergedRegion(new CellRangeAddress(rStart,r-1,0,0));
			}
			//回乡变为不回乡实习大区，进行回乡汇总
			row=st.createRow(r);
			setHeight(row,-1);
			for(int i=0;i<column;i++) {
				cell=row.createCell(i);
				cell.setCellType(CellType.STRING);
				cell.setCellStyle(styleBigSum);
				if(i<3) {
					if(i==2) st.addMergedRegion(new CellRangeAddress(r,r,0,2));
					if(i>0) continue;
				}
				cell.setCellValue(i<3?"回生源地教育实习总计":
							i==3?String.valueOf(allNumber-hxAllNumber):
								i<column-1?String.valueOf(numbers[i-3]-hxNumber[i-3]):
									"");
			}
			r++;
			//进行总汇总
			row=st.createRow(r);
			setHeight(row,-1);
			for(int i=0;i<column;i++) {
				cell=row.createCell(i);
				cell.setCellType(CellType.STRING);
				cell.setCellStyle(styleTitle);
				if(i<3) {
					if(i==2) st.addMergedRegion(new CellRangeAddress(r,r,0,2));
					if(i>0) continue;
				}
				cell.setCellValue(i<3?"总人数合计":
							i==3?String.valueOf(allNumber):
								i<column-1?String.valueOf(numbers[i-3]):
									"");
			}
			r++;
			//列宽，第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2012-08-10"的宽度为2500
	//		st.setColumnWidth(0,2500);
			debug(wb,name);
	        wb.write(out);
		}
		return name+".xlsx";//下载文件名称
	}
	
	
	@Override
	public String createTeacherList(int year, OutputStream out) throws IOException {
		String name=String.format("%d年免费师范生教育实习指导教师名单",
				year);
		Map<Integer,List<InnerPerson>> teachers=new TreeMap<Integer,List<InnerPerson>>();
		try{
			for(Student stu:Base.list(Student.class,new Restraint(Field.getField(Student.class,"year"),year))) try{
				InnerPerson t=new InnerPerson(stu.getTeacherId());
				School s=new School(t.getSchool());
				if(!teachers.containsKey(s.getOrderId()))
					teachers.put(s.getOrderId(),new ArrayList<InnerPerson>());
				teachers.get(s.getOrderId()).add(t);
			}catch(IllegalArgumentException | SQLException e) {
				System.err.println(stu.getName()+"没有指导老师！("+stu.toString()+")");
			}
		}catch(IllegalArgumentException | SQLException | InstantiationException e) {
			System.err.println("读取实习生列表失败！");
		}
		try{
			for(Supervise sup:Base.list(Supervise.class,new Restraint(Field.getField(Supervise.class,"year"),year))) try{
				InnerPerson t=new InnerPerson(sup.getSupervisorId());
				School s=new School(t.getSchool());
				if(!teachers.containsKey(s.getOrderId()))
					teachers.put(s.getOrderId(),new ArrayList<InnerPerson>());
				teachers.get(s.getOrderId()).add(t);
			}catch(IllegalArgumentException | SQLException e) {
				System.err.println(sup.getPracticeBase()+"的"+Supervise.getTypeNameList()[sup.getSuperviseType()]+
						"没有督导老师！("+sup.toString()+")");
			}
		}catch(IllegalArgumentException | SQLException | InstantiationException e) {
			System.err.println("读取督导老师列表失败！");
		}
		try(Workbook wb=new XSSFWorkbook();){
			Sheet st=wb.createSheet("指导教师");
			CellStyle styleBigTitle=POIExcel.getCellStyle(wb,"宋体",18,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
		//	CellStyle styleSmallTitle=POIExcel.getCellStyle(wb,"宋体",12,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
			CellStyle styleTitle=POIExcel.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
			CellStyle styleContent=POIExcel.getCellStyle(wb,"宋体",10,false,HorizontalAlignment.CENTER,BorderStyle.HAIR,false,null);
			//设置列数
			final Field[] fs=Field.getFields(InnerPerson.class,"school","name","id","mobile","email");
			final int column=fs.length+1;
			Row row;
			Cell cell;
			int r=0;
			/*第一行标题*/row=st.createRow(r);
			setHeight(row,40);
			cell=row.createCell(0);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(year+"年免费师范生教育实习指导教师名单");
			cell.setCellStyle(styleBigTitle);
			for(int i=1;i<column;i++) row.createCell(i).setCellStyle(styleBigTitle);
			st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
			r++;
			/*第二行列标签*/row=st.createRow(r);
			setHeight(row,20);
			for(int i=0;i<column;i++) {
				cell=row.createCell(i);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(i==0?"序号":
					fs[i-1].getDescription());
				cell.setCellStyle(styleTitle);
			}
			r++;
			/*第三行开始每个院系指导教师列表*/
			int cnt=0;
			for(Map.Entry<Integer,List<InnerPerson>> entry:teachers.entrySet()) {
				boolean first=true;
				for(InnerPerson t:entry.getValue()) {
					/*学生内容*/row=st.createRow(r);
					row.setHeight((short)-1);
					for(int i=0;i<column;i++) {
						cell=row.createCell(i);
						cell.setCellType(CellType.STRING);
						cell.setCellStyle(styleContent);
						if(first) first=false;
						else if(i==1) continue;
						cell.setCellValue(i==0?String.valueOf(cnt++):
							Field.o2s(fs[i-1].get(t),""));
					}
					r++;
				}
				st.addMergedRegion(new CellRangeAddress(r-entry.getValue().size(),r-1,1,1));
			}
			//列宽，第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2012-08-10"的宽度为2500
	//		st.setColumnWidth(0,2500);
			for(int i=0;i<column;i++) st.autoSizeColumn(i,true);
			debug(wb,name);
	        wb.write(out);
		}
		return name+".xlsx";//下载文件名称
	}
	
	
	@Override
	public String createSuperviseList(int year, OutputStream stream) throws IOException {
		String name=String.format("%d年免费师范生教育实习督导任务表",
				year);
		Map<Integer,List<InnerPerson>> teachers=new TreeMap<Integer,List<InnerPerson>>();
		try{
			for(Student stu:Base.list(Student.class,new Restraint(Field.getField(Student.class,"year"),year))) try{
				InnerPerson t=new InnerPerson(stu.getTeacherId());
				School s=new School(t.getSchool());
				if(!teachers.containsKey(s.getOrderId()))
					teachers.put(s.getOrderId(),new ArrayList<InnerPerson>());
				teachers.get(s.getOrderId()).add(t);
			}catch(IllegalArgumentException | SQLException e) {
				System.err.println(stu.getName()+"没有指导老师！("+stu.toString()+")");
			}
		}catch(IllegalArgumentException | SQLException | InstantiationException e) {
			System.err.println("读取实习生列表失败！");
		}
		try{
			for(Supervise sup:Base.list(Supervise.class,new Restraint(Field.getField(Supervise.class,"year"),year))) try{
				InnerPerson t=new InnerPerson(sup.getSupervisorId());
				School s=new School(t.getSchool());
				if(!teachers.containsKey(s.getOrderId()))
					teachers.put(s.getOrderId(),new ArrayList<InnerPerson>());
				teachers.get(s.getOrderId()).add(t);
			}catch(IllegalArgumentException | SQLException e) {
				System.err.println(sup.getPracticeBase()+"的"+Supervise.getTypeNameList()[sup.getSuperviseType()]+
						"没有督导老师！("+sup.toString()+")");
			}
		}catch(IllegalArgumentException | SQLException | InstantiationException e) {
			System.err.println("读取督导老师列表失败！");
		}
		try(Workbook wb=new XSSFWorkbook();){
			Sheet st=wb.createSheet("指导教师");
			CellStyle styleBigTitle=POIExcel.getCellStyle(wb,"宋体",18,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
		//	CellStyle styleSmallTitle=POIExcel.getCellStyle(wb,"宋体",12,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
			CellStyle styleTitle=POIExcel.getCellStyle(wb,"宋体",10,true,HorizontalAlignment.CENTER,BorderStyle.NONE,false,null);
			CellStyle styleContent=POIExcel.getCellStyle(wb,"宋体",10,false,HorizontalAlignment.CENTER,BorderStyle.HAIR,false,null);
			//设置列数
			final Field[] fs=Field.getFields(InnerPerson.class,"school","name","id","mobile","email");
			final int column=fs.length+1;
			Row row;
			Cell cell;
			int r=0;
			/*第一行标题*/row=st.createRow(r);
			setHeight(row,40);
			cell=row.createCell(0);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(year+"年免费师范生教育实习指导教师名单");
			cell.setCellStyle(styleBigTitle);
			for(int i=1;i<column;i++) row.createCell(i).setCellStyle(styleBigTitle);
			st.addMergedRegion(new CellRangeAddress(r,r,0,column-1));
			r++;
			/*第二行列标签*/row=st.createRow(r);
			setHeight(row,20);
			for(int i=0;i<column;i++) {
				cell=row.createCell(i);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(i==0?"序号":
					fs[i-1].getDescription());
				cell.setCellStyle(styleTitle);
			}
			r++;
			/*第三行开始每个院系指导教师列表*/
			int cnt=0;
			for(Map.Entry<Integer,List<InnerPerson>> entry:teachers.entrySet()) {
				boolean first=true;
				for(InnerPerson t:entry.getValue()) {
					/*学生内容*/row=st.createRow(r);
					row.setHeight((short)-1);
					for(int i=0;i<column;i++) {
						cell=row.createCell(i);
						cell.setCellType(CellType.STRING);
						cell.setCellStyle(styleContent);
						if(first) first=false;
						else if(i==1) continue;
						cell.setCellValue(i==0?String.valueOf(cnt++):
							Field.o2s(fs[i-1].get(t),""));
					}
					r++;
				}
				st.addMergedRegion(new CellRangeAddress(r-entry.getValue().size(),r-1,1,1));
			}
			//列宽，第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2012-08-10"的宽度为2500
	//		st.setColumnWidth(0,2500);
			for(int i=0;i<column;i++) st.autoSizeColumn(i,true);
			debug(wb,name);
	        wb.write(out);
		}
		return name+".xlsx";//下载文件名称
	}


}
