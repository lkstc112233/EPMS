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
import obj.staticSource.*;

public class POIWord implements SpecialWordIO{
	static private final char [] ChineseNum ={'零','壹','贰','叁','肆','伍','陆','柒','捌','玖'};
	static private final char [] ChineseUnit={'里','分','角','元','拾','佰','仟','万','拾','佰','仟','亿','拾','佰','仟'};
	static private String arabNumToChineseRMB(String moneyNum) throws IllegalArgumentException{  
	    String res="";  
	    int i=3;  
	    int len=moneyNum.length();  
	    if(len>12){  
	        throw new IllegalArgumentException("Number too large!");  
	    }  
	    if("0".equals(moneyNum))  
	        return "零元";  
	    //System.out.println(moneyNum);  
	    for(len--;len>=0;len--){  
	        res=ChineseUnit[i++]+res;  
	        int num=Integer.parseInt(moneyNum.charAt(len)+"");  
	        res=ChineseNum[num]+res;  
	    }  
	    return res.replaceAll("零[拾佰仟]", "零")  
	            .replaceAll("零+亿", "亿").replaceAll("零+万", "万")  
	            .replaceAll("零+元", "元").replaceAll("零+", "零");  
	}  
	static private String arabNumToChineseRMB(float moneyNum) throws IllegalArgumentException {
	    String money=String.format("%.3f",moneyNum);
	    if(moneyNum==0.0)
	        return "零元整";
	    String[] ss=money.split("\\.",2);
	    if(ss.length==1||ss[1].isEmpty() || ss.length==2&&Integer.parseInt(ss[1])==0)
	    	return arabNumToChineseRMB(ss[0])+"整";
	    String res="";
	    int deci=Integer.parseInt(money.split("\\.")[1].substring(0,3));
	    int i=0;
	    while(deci>0){
	        res=ChineseUnit[i++]+res;
	        res=ChineseNum[deci%10]+res;
	        deci/=10;
	    }
	    res=res.replaceAll("零[里分角]", "零");
	    if(i<3)
	        res="零"+res;
	    res=res.replaceAll("零+", "零");
	    if(res.endsWith("零"))
	        res=res.substring(0,res.length()-1);
	    return arabNumToChineseRMB(ss[0])+res;
	}
	
	//"\\w"匹配字母、数字、下划线。等价于'[A-Za-z0-9_]'
	static final public String regex="\\$\\{[A-Za-z_](\\w|\\.)*\\}";
	
	static private String getParamString(Object param) {
		if(param==null) return "";
		Float f=null;
		if(param instanceof Float) f=(Float)param;
		else if(param instanceof Double) f=((Double)param).floatValue();
		if(f!=null) {
			for(int i=0,b=1;i<10;i++,b*=10) {
				int a=Math.round(f*b);
				if(Math.abs(f*b-a)*b<1e-7) {
					String res=String.valueOf(a);
					int len=res.length();
					return res.substring(0,len-i)+(i<=0?"":("."+res.substring(len-i+1,len)));
				}
			}
		}
		return param.toString();
	}
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
	
	static private void copyRun(XWPFRun target,XWPFRun source) {
		if(target==null || source==null) return;
		target.setBold(source.isBold());
		target.setCapitalized(source.isCapitalized());
		target.setColor(source.getColor());
		target.setDoubleStrikethrough(source.isDoubleStrikeThrough());
		target.setEmbossed(source.isEmbossed());
		target.setFontFamily(source.getFontFamily());
		if(source.getFontSize()>0)
			target.setFontSize(source.getFontSize());
		target.setImprinted(source.isImprinted());
		target.setItalic(source.isItalic());
		target.setKerning(source.getKerning());
		target.setShadow(source.isShadowed());
		target.setSmallCaps(source.isSmallCaps());
		target.setStrikeThrough(source.isStrikeThrough());
		target.setUnderline(source.getUnderline());
	}
	@SuppressWarnings("unused")
	static private void ReplaceInParagraph(XWPFParagraph para,Object param) throws IOException {
		String str=para.getText();
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(str);
		while(matcher.find()) {
			List<XWPFRun> runs=para.getRuns();
			String oldStr=matcher.group();
			String s=oldStr.substring(2,oldStr.length()-1);
			String ss[]=s.split("\\.");
			Object o=param;
			for(String a:ss) if(o!=null)
				o=getFromParam(o,a,s);
			String newStr=getParamString(o);
			//oldStr->newStr
			TextSegement seg=para.searchText(oldStr,new PositionInParagraph());
			if(seg!=null) {
				if(seg.getBeginRun()==seg.getEndRun()) {
					XWPFRun firstRun=runs.get(seg.getBeginRun());
					String runText=firstRun.getText(firstRun.getTextPosition());
					String replaced = runText.replace(oldStr,newStr);
					/*	firstRun.setText(replaced,0);*/
					firstRun.setText("",0);
					String[] replacedSplit=replaced.split("\\n");
					for(int i=0;i<replacedSplit.length;i++) {
						XWPFRun run=para.insertNewRun(seg.getBeginRun()+i);
						copyRun(run,firstRun);
						run.setText(replacedSplit[i],0);
						if(i<replacedSplit.length-1)
							run.addBreak();
					}
				}else{//存在多个Run标签
					StringBuilder sb=new StringBuilder();
					for(int i=seg.getBeginRun();i<=seg.getEndRun();i++) {
						XWPFRun run=runs.get(i);
						sb.append(run.getText((run.getTextPosition())));
					}
					String connectedRuns=sb.toString();
					String replaced=connectedRuns.replace(oldStr,newStr);
					XWPFRun firstRun=runs.get(seg.getBeginRun());
					/*	firstRun.setText(replaced,0);
						for(int i=seg.getBeginRun()+1;i<=seg.getEndRun();i++) {
							//删除后边的run标签
						//	para.removeRun(i);
							runs.get(i).setText("",0);
						}*/
					firstRun=null;
					for(int i=seg.getBeginRun();i<=seg.getEndRun();i++) {
						if(runs.get(i).getFontSize()>0) {
							firstRun=runs.get(i);
							break;
						}
					}
					if(1>2 && firstRun==null) {
						String error="Cannot get the font size!(";
						for(int i=seg.getBeginRun()+1;i<=seg.getEndRun();i++) {
							XWPFRun run=runs.get(i);
							error+="["+run.getText(0)+"("+run.getFontFamily()+","+run.getFontName()+","+run.getFontSize()+")]";
							run.setText("",0);
						}
						System.err.println(error);
						runs.get(seg.getBeginRun()).setText(replaced);
					}else {
						firstRun=runs.get(seg.getBeginRun());
						for(int i=seg.getBeginRun();i<=seg.getEndRun();i++)
							runs.get(i).setText("",0);
						String[] replacedSplit=replaced.split("\\n");
						for(int i=0;i<replacedSplit.length;i++) {
							XWPFRun run=para.insertNewRun(seg.getEndRun()+i);
							copyRun(run,firstRun);
							run.setText(replacedSplit[i]);
							if(i<replacedSplit.length-1)
								run.addBreak();
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
		try {
			param.put("grade",String.valueOf(year-3));
			param.put("year",String.valueOf(year));
			param.put("nowYear",String.valueOf(Manager.getNowTimeYear()));
			param.put("nowMonth",String.valueOf(Manager.getNowTimeMonth()));
			param.put("nowDay",String.valueOf(Manager.getNowTimeDay()));
			param.put("practiceBaseName",pb.getName());
			try{
				Time a=new Time(year,ACCESS.jysx);
				param.put("startMonth",a.getTime1()==null?"null":String.valueOf(Manager.getTimeMonth(a.getTime1())));
				param.put("startDay",a.getTime1()==null?"null":String.valueOf(Manager.getTimeDay(a.getTime1())));
				param.put("endMonth",a.getTime2()==null?"null":String.valueOf(Manager.getTimeMonth(a.getTime2())));
				param.put("endDay",a.getTime2()==null?"null":String.valueOf(Manager.getTimeDay(a.getTime2())));
			}catch(IllegalArgumentException | SQLException e) {
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
		}catch(Exception e) {
			throw new IOException("参数获取失败!("+e.getMessage()+")");
		}
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

	@Override
	public String createSupervisorMandate(int year,
			InnerPerson supervisor,
			ListOfRegionAndPracticeBaseAndInnerPerson.RegionPair.PracticeBasePair pair,
			int superviseIndex,
			OutputStream out) throws IOException
	{
		School school;
		try{school=new School(supervisor.getSchool());
		}catch (IllegalArgumentException | SQLException e){
			throw new IOException(e.getMessage());
		}
		//start
		String name=String.format("%d年[%s%s%s至%s]免费师范生教育实习督导任务书",
				year,school.getSubName(),
				supervisor.getName(),
				Supervise.getTypeNameList()[superviseIndex],pair.getPracticeBase().getName());
		String modelFileName="免费师范生教育实习督导任务书.docx";
		Map<String,Object> param=new HashMap<String,Object>();
		try {
			param.put("grade",String.valueOf(year-3));
			param.put("year",String.valueOf(year));
			param.put("nowYear",String.valueOf(Manager.getNowTimeYear()));
			param.put("nowMonth",String.valueOf(Manager.getNowTimeMonth()));
			param.put("nowDay",String.valueOf(Manager.getNowTimeDay()));
			param.put("supervisor",supervisor);
			param.put("regionName",pair.getRegion().getName());
			param.put("regionLeader",pair.getLeader());
			param.put("practiceBase",pair.getPracticeBase());
			param.put("superviseTypeName",Supervise.getTypeNameList()[superviseIndex]);
			param.put("superviseTask",POI.loadTextFile("superviseTask_"+String.valueOf(
					superviseIndex)+".txt"));
			try{
				Time a=new Time(year,ACCESS.supervise[superviseIndex]);
				param.put("supervise"+"StartMonth",a.getTime1()==null?"null":String.valueOf(Manager.getTimeMonth(a.getTime1())));
				param.put("supervise"+"StartDay",a.getTime1()==null?"null":String.valueOf(Manager.getTimeDay(a.getTime1())));
				param.put("supervise"+"EndMonth",a.getTime2()==null?"null":String.valueOf(Manager.getTimeMonth(a.getTime2())));
				param.put("supervise"+"EndDay",a.getTime2()==null?"null":String.valueOf(Manager.getTimeDay(a.getTime2())));
			}catch(IllegalArgumentException|SQLException e) {
				throw new IOException(e.getMessage());
			}
		}catch(Exception e) {
			throw new IOException("参数获取失败!("+e.getMessage()+")");
		}
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

	@Override
	public String createPracticeBaseInfomation(int year,
			PracticeBase pb,
			OutputStream out) throws IOException {
		//start
		String name=String.format("%d年免费师范生教育实习基地信息(%s)",
				year,
				pb.getName());
		String modelFileName="免费师范生教育实习基地信息.docx";
		Map<String,Object> param=new HashMap<String,Object>();
		try {
			param.put("grade",String.valueOf(year-3));
			param.put("year",String.valueOf(year));
		//	param.put("nowYear",String.valueOf(Manager.getNowTimeYear()));
		//	param.put("nowMonth",String.valueOf(Manager.getNowTimeMonth()));
		//	param.put("nowDay",String.valueOf(Manager.getNowTimeDay()));
			param.put("practiceBase",pb);
			try {
				int index=0;
				for(OuterOffice office:Base.list(OuterOffice.class)) {
					if(office.isAvailable()) {
						OuterPerson outer=null;
						for(OuterPerson o:Base.list(OuterPerson.class,new Restraint(
								Field.getFields(OuterPerson.class,"practiceBase","office"),
								new Object[] {pb.getName(),office.getName()}
								))) {
							outer=o;break;
						}
						if(outer==null) {
							outer=new OuterPerson();
							outer.setOffice(office.getName());
						}
						param.put(String.format("outer_%d",++index),outer);
					}
				}
			} catch (IllegalArgumentException | InstantiationException | SQLException e) {
				throw new IOException(e.getMessage());
			}
		}catch(Exception e) {
			throw new IOException("参数获取失败!("+e.getMessage()+")");
		}
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

	@Override
	public String createPracticeBaseMoney(int year,
			ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair,
			OutputStream out) throws IOException {
		//start
		String name=String.format("%d年免费师范生教育实习基地经费明细单(%s)",
				year,
				pair.getPracticeBase().getName());
		String modelFileName="免费师范生教育实习基地经费明细单.docx";
		Map<String,Object> param=new HashMap<String,Object>();
		try {
			param.put("grade",String.valueOf(year-3));
			param.put("year",String.valueOf(year));
			param.put("nowYear",String.valueOf(Manager.getNowTimeYear()));
			param.put("nowMonth",String.valueOf(Manager.getNowTimeMonth()));
			param.put("nowDay",String.valueOf(Manager.getNowTimeDay()));
			param.put("practiceBaseName",pair.getPracticeBase().getName());
			param.put("sum",pair.getSum());
			param.put("studentNum",pair.getNumberOfStudent());
			param.put("syyNum",pair.getNumberOfStudentSYY());
			param.put("sumSmall",String.format("%.2f",pair.getSum().getSum()));
			param.put("sumChinese",arabNumToChineseRMB(pair.getSum().getSum()));
			MoneyPB base[];
			try {
				base = MoneyPB.getMoneyPBBase();
			} catch (IllegalArgumentException | InstantiationException | SQLException e) {
				throw new IOException("读取教育实习经费标准失败!("+e+")");
			}
			param.put("base",base[pair.getPracticeBase().getProvince().contains("北京")?0:1]);
			param.put("startMonth",Manager.getTimeMonth(pair.getRegion().getEnterPracticeBaseTime()));
			param.put("startDay",Manager.getTimeDay(pair.getRegion().getEnterPracticeBaseTime()));
			try{
				Time a=new Time(year,ACCESS.jysx);
				param.put("endMonth",Manager.getTimeMonth(a.getTime2()));
				param.put("endDay",Manager.getTimeDay(a.getTime2()));
			}catch(IllegalArgumentException|SQLException e) {
				throw new IOException(e.getMessage());
			}
		}catch(Exception e) {
			throw new IOException("参数获取失败!("+e.getMessage()+")");
		}
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
