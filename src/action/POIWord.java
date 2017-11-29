package action;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.*;

import org.apache.poi.xwpf.usermodel.*;

import obj.annualTable.*;
import obj.staticObject.*;

public class POIWord implements SpecialWordIO{
	
	//"\\w"匹配字母、数字、下划线。等价于'[A-Za-z0-9_]'
	static final public String regex="\\$\\{[A-Za-z_](\\w|\\.)*\\}";

	static private void ReplaceInParagraph(XWPFParagraph para,Object param) {
		for(XWPFRun run:para.getRuns()) {
			String oldStr=run.getText(run.getTextPosition());
			String newStr=oldStr;
			Pattern pattern=Pattern.compile(regex);
			Matcher matcher=pattern.matcher(oldStr);
			while(matcher.find()) {
				String oldFound=matcher.group();
				String s=oldFound.substring(2,oldFound.length()-1);
				String ss[]=s.split("\\.");
				Object o=param;
				for(String a:ss) {
					String getterMethodName="get"+a.substring(0,1).toUpperCase()+a.substring(1);
					try {
						Method m=o.getClass().getMethod(getterMethodName);
						o=m.invoke(o);
					} catch (NoSuchMethodException | SecurityException | NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						try {
							Method m=o.getClass().getMethod("get",Object.class);
							o=m.invoke(o);
						} catch (NoSuchMethodException | SecurityException | NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
							System.err.println(String.format(
									"[POIWord] Cannot get Value from '%s', when get %s from %s by Method(%s).(error: %s)",
									s,a,String.valueOf(o),getterMethodName,
									e.getMessage()));
							System.err.println(String.format(
									"[POIWord]     and by Method(get).(error: %s)",
									e2.getMessage()));
							o=null;
							break;
						}
					}
				}
				String newFound=o==null?"":o.toString();
				//oldFound->newFound
				newStr=newStr.replaceFirst(oldFound,newFound);
				System.out.println(String.format(
						"[POIWord] '%s' -> '%s'.",
						oldFound,newFound));
			}
			run.setText(newStr);
		}
		/*
		String str=para.getText();
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(str);
		List<XWPFRun> runs=para.getRuns();
		while(matcher.find()) {
			String tmp=matcher.group();
			String s=tmp.substring(2,tmp.length()-1);
			String ss[]=s.split("\\.");
			Object o=param;
			for(String a:ss) {
				String b=a.substring(0,1).toUpperCase()+a.substring(1);
				try {
					Method m=o.getClass().getMethod("get"+b);
					o=m.invoke(o);
				} catch (NoSuchMethodException | SecurityException | NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					System.err.println(e.getMessage());
					o=null;
					break;
				}
			}
			if(o!=null) {
				String os=o==null?"":o.toString();
				//tmp->os
				TextSegement seg=para.searchText(tmp,new PositionInParagraph());
				if(seg!=null) {
					if(seg.getBeginRun()==seg.getEndRun()) {
						XWPFRun run=runs.get(seg.getBeginRun());
						String runText=run.getText(run.getTextPosition());
						String replaced = runText.replace(tmp,os);
						run.setText(replaced,0);
					}else{//存在多个Run标签
						StringBuilder sb=new StringBuilder();
						for(int i=seg.getBeginRun();i<=seg.getEndRun();i++) {
							XWPFRun run=runs.get(i);
							sb.append(run.getText((run.getTextPosition())));
						}
						String connectedRuns=sb.toString();
						String replaced=connectedRuns.replace(tmp,os);
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
		*/
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
	public String createPracticeBaseFile(int year,PracticeBase pb, List<Student> students, OutputStream out)
			throws IOException {
		String name=String.format("%d年[%s]免费师范生教育实习商洽函",year,pb.getName());
		String modelFileName="免费师范生教育实习商洽函.docx";
		Map<String,String> param=new HashMap<String,String>();
		try(InputStream in=new FileInputStream(modelFileName);
				XWPFDocument doc=this.getModel(in,param);){
			debug(doc,name);
			doc.write(out);
		}
		return name+".docx";
	}

}
