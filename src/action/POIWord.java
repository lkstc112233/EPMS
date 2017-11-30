package action;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.*;

import org.apache.poi.xwpf.usermodel.*;

import obj.Base;
import obj.Field;
import obj.Restraint;
import obj.annualTable.*;
import obj.staticObject.*;
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
			List<Student> students,
			String majorName,
			OutputStream out) throws IOException
	{
		String name=String.format("%d年[%s]%s免费师范生教育实习商洽函",
				year,pb.getName(),
				majorName==null?"":("("+majorName+")"));
		String modelFileName="免费师范生教育实习商洽函.docx";
		Map<String,String> param=new HashMap<String,String>();
		param.put("grade",String.valueOf(year-3));
		param.put("year",String.valueOf(year));
		param.put("month",String.valueOf(Manager.getNowTimeMonth()));
		param.put("day",String.valueOf(Manager.getNowTimeDay()));
		try{
			Time a=Base.list(Time.class,new Restraint(Field.getField(Time.class,"project"),Restraint.Type.Equal,Manager.jysx)).get(0);
			param.put("endMonth",String.valueOf(Manager.getTimeMonth(a.getTime2())));
			param.put("endDay",String.valueOf(Manager.getTimeDay(a.getTime2())));
			param.put("practiceBaseName",pb.getName());
		}catch(IllegalArgumentException|InstantiationException|SQLException e) {
			throw new IOException(e.getMessage());
		}
		StringBuilder sb=new StringBuilder();
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
		int cnt=0;
		for(Major major:majors) {
			int num=0;
			for(Student s:students) if(s.getMajor().equals(major.getName()))
				num++;
			if(num<=0) continue;
			if(sb.length()>0) sb.append("、");
			sb.append(major.getSubject()+num+"人");
			cnt+=num;
		}
		param.put("studentCountList",sb.toString());
		param.put("studentCount",String.valueOf(cnt));
		sb=new StringBuilder();
		try{for(InnerPerson inner:Manager.getManagerInnerPersons()) {
			sb.append(inner.getName());
			sb.append("  电话：");
			sb.append(inner.getPhone());
			sb.append("  手机：");
			sb.append(inner.getMobile());
			sb.append("  邮箱：");
			sb.append(inner.getEmail());
			sb.append("\r\n");
		}}catch(IllegalArgumentException|InstantiationException|SQLException e) {
			throw new IOException(e.getMessage());
		}
		param.put("jwcManager",sb.toString());
		try(InputStream in=new FileInputStream(POI.path+modelFileName);
				XWPFDocument doc=this.getModel(in,param);){
			debug(doc,name);
			doc.write(out);
		}
		return name+".docx";
	}

}
