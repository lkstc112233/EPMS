package action;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.*;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.*;

import org.apache.poi.xwpf.usermodel.*;

import obj.*;
import obj.annualTable.*;
import obj.staticObject.*;
import obj.staticSource.ACCESS;
import obj.staticSource.Major;

public class POIWord implements SpecialWordIO{
	
	//"\\w"匹配字母、数字、下划线。等价于'[A-Za-z0-9_]'
	static final public String regex="\\$\\{[A-Za-z_](\\w|\\.)*\\}";
	
	static private Object getFromParam(Object o,String field,String allStr) throws IOException {
		String getterMethodName="get"+field.substring(0,1).toUpperCase()+field.substring(1);
		try {
			Method m=o.getClass().getMethod(getterMethodName);
			return m.invoke(o);
		} catch (NoSuchMethodException | SecurityException | NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			try {
				Method m=o.getClass().getMethod("get",Object.class);
				return m.invoke(o,field);
			} catch (NoSuchMethodException | SecurityException | NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
				String error=String.format(
						"[POIWord] Cannot get Value from '%s', when get %s from %s by Method(%s).(error: %s)\n"
					+	"[POIWord]     and by Method(get).(error: %s)",
						allStr,field,String.valueOf(o),getterMethodName,
						e.getMessage(),e2.getMessage());
				System.err.println(error);
				throw new IOException(error);
			}
		}
	}
	
	static private void ReplaceInParagraph(XWPFParagraph para,Object param) throws IOException {
	/*	for(XWPFRun run:para.getRuns()) {
			String oldStr=run.getText(run.getTextPosition());
			String newStr=oldStr;
			Pattern pattern=Pattern.compile(regex);
			Matcher matcher=pattern.matcher(oldStr);
			while(matcher.find()) {
				String oldFound=matcher.group();
				String s=oldFound.substring(2,oldFound.length()-1);
				String ss[]=s.split("\\.");
				Object o=param;
				for(String a:ss)
					o=getFromParam(o,a,s);
				String newFound=o==null?"":o.toString();
				//oldFound->newFound
				newStr=newStr.replaceFirst(oldFound,newFound);
				System.out.println(String.format(
						"[POIWord] '%s' -> '%s'.",
						oldFound,newFound));
			}
		//	run.setText(newStr);
			run.setText(newStr,run.getTextPosition());
		}//*/
		
		String str=para.getText();
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(str);
		List<XWPFRun> runs=para.getRuns();
		while(matcher.find()) {
			String oldStr=matcher.group();
			String s=oldStr.substring(2,oldStr.length()-1);
			String ss[]=s.split("\\.");
			Object o=param;
			for(String a:ss)
				o=getFromParam(o,a,s);
			if(o!=null) {
				String newStr=o==null?"":o.toString();
				//oldStr->newStr
				TextSegement seg=para.searchText(oldStr,new PositionInParagraph());
				if(seg!=null) {
					if(seg.getBeginRun()==seg.getEndRun()) {
						XWPFRun run=runs.get(seg.getBeginRun());
						String runText=run.getText(run.getTextPosition());
						String replaced = runText.replace(oldStr,newStr);
						run.setText(replaced,0);
					}else{//存在多个Run标签
						StringBuilder sb=new StringBuilder();
						for(int i=seg.getBeginRun();i<=seg.getEndRun();i++) {
							XWPFRun run=runs.get(i);
							sb.append(run.getText((run.getTextPosition())));
						}
						String connectedRuns=sb.toString();
						String replaced=connectedRuns.replace(oldStr,newStr);
						XWPFRun firstRun=runs.get(seg.getBeginRun());
						firstRun.setText(replaced,0);
						for(int i=seg.getBeginRun()+1;i<=seg.getEndRun();i++) {
							//删除后边的run标签
						//	para.removeRun(i);
							runs.get(i).setText("",0);
						}
					}
				}
			}
		}
		//*/
	}
	public XWPFDocument getModel(InputStream in,Object param)throws IOException{
		XWPFDocument inDoc=new XWPFDocument(in);
		Iterator<XWPFParagraph> iterParagraph=inDoc.getParagraphsIterator();
		while(iterParagraph.hasNext()){
			XWPFParagraph para=iterParagraph.next();
			ReplaceInParagraph(para,param);
		}
		Iterator<XWPFTable> iterTable=inDoc.getTablesIterator();
		while(iterTable.hasNext()){
			for(XWPFTableRow row:iterTable.next().getRows()) {
				for(XWPFTableCell cell:row.getTableCells()) {
					for(XWPFParagraph para:cell.getParagraphs()) {
						ReplaceInParagraph(para,param);
					}
				}
			}
		}
		return inDoc;
	}
	static public void debug(XWPFDocument doc,String name) throws IOException{
	/*	File f=new File("C:\\Users\\12132\\Desktop\\"+name+".docx");
		FileOutputStream o=new FileOutputStream(f);
		doc.write(o);
		o.flush();
		o.close();//*/
	}

	
	@Override
	public String createPracticeBaseConsultationLetter(
			int year,
			PracticeBase pb,
			String majorName,
			OutputStream out) throws IOException
	{
		Map<Major,List<Student>> list=new TreeMap<Major,List<Student>>();
		Restraint restraint = majorName==null ?
				new Restraint(Field.getFields(Student.class,"year","practiceBase"),new Object[] {year,pb.getName()})
				:new Restraint(Field.getFields(Student.class,"year","practiceBase","major"),new Object[]{year,pb.getName(),majorName});
		try{
			for(Student stu:Base.list(Student.class,restraint)) try{
				Major m=new Major(stu.getMajor());
				if(!list.containsKey(m))
					list.put(m,new ArrayList<Student>());
				list.get(m).add(stu);
			}catch(IllegalArgumentException | SQLException e) {
				System.err.println(stu.getName()+"没有指导老师！("+stu.toString()+")");
			}
		}catch(IllegalArgumentException | SQLException | InstantiationException e) {
			System.err.println("读取实习生列表失败！");
		}
		//start
		String name=String.format("%d年[%s]%s免费师范生教育实习商洽函",
				year,pb.getName(),
				majorName==null?"":("("+majorName+")"));
		String modelFileName="免费师范生教育实习商洽函.docx";
		Map<String,String> param=new HashMap<String,String>();
		param.put("grade",String.valueOf(year-3));
		param.put("year",String.valueOf(year));
		param.put("nowYear",String.valueOf(Manager.getNowTimeYear()));
		param.put("nowMonth",String.valueOf(Manager.getNowTimeMonth()));
		param.put("nowDay",String.valueOf(Manager.getNowTimeDay()));
		param.put("practiceBaseName",pb.getName());
		try{
			Time a=new Time(year,ACCESS.jysx);
			param.put("startMonth",String.valueOf(Manager.getTimeMonth(a.getTime1())));
			param.put("startDay",String.valueOf(Manager.getTimeDay(a.getTime1())));
			param.put("endMonth",String.valueOf(Manager.getTimeMonth(a.getTime2())));
			param.put("endDay",String.valueOf(Manager.getTimeDay(a.getTime2())));
		}catch(IllegalArgumentException|InstantiationException|SQLException e) {
			throw new IOException(e.getMessage());
		}
		StringBuilder sb=new StringBuilder();
		int cnt=0;
		for(Map.Entry<Major,List<Student>> entry:list.entrySet()){
			if(entry.getValue().isEmpty()) continue;
			if(sb.length()>0) sb.append("、");
			sb.append(entry.getKey().getSubject()+entry.getValue().size()+"人");
			cnt+=entry.getValue().size();
		}
		param.put("studentCountList",sb.toString());
		param.put("studentCount",String.valueOf(cnt));
		sb=new StringBuilder();
		try{for(InnerPerson inner:Manager.getManagerInnerPersons()) {
			sb.append(inner.getName());
			sb.append("  电话：");sb.append(inner.getPhone());
			sb.append("  手机：");sb.append(inner.getMobile());
			sb.append("  邮箱：");sb.append(inner.getEmail());
			sb.append("\r\n");
		}}catch(IllegalArgumentException|InstantiationException|SQLException e) {
			throw new IOException(e.getMessage());
		}
		param.put("jwcManager",sb.toString());
		try(FileInputStream in=new FileInputStream(POI.path+modelFileName);){
			FileChannel channel=in.getChannel();
			FileLock lock=null;
			for(int i=0;i<POI.LockMaxTry;i++) try{
				lock=channel.tryLock(0L,Long.MAX_VALUE,true);
				if(lock!=null) break;
				try{Thread.sleep(POI.LockTryWait);//共享锁
				}catch(InterruptedException e){}
		//	}catch(OverlappingFileLockException e) {
			}catch(Exception e) {
				e.printStackTrace();
				break;
			}
			if(lock==null)
				throw new IOException("模板文件被占用，无法读取!");
			XWPFDocument doc;
			try{
				doc=this.getModel(in,param);
			}catch(IOException e){
				throw e;
			}finally{
				if(lock.isValid())
					lock.release();
			}
			debug(doc,name);
			doc.write(out);
			doc.close();
		}
		return name+".docx";
	}

}
