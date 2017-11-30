package action.function;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import action.SpecialIO;
import obj.Base;
import obj.Field;
import obj.annualTable.*;
import obj.staticObject.PracticeBase;

/**
 * 导出实习生名单
 */
public class ExportPracticeBaseConsultationLetter extends ActionSupport{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private ListOfPracticeBaseAndStudents practiceBaseAndStudents;
	
	public ListOfPracticeBaseAndStudents getPracticeBaseAndStudents(){return this.practiceBaseAndStudents;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportPracticeBaseConsultationLetter(){
		super();
		this.practiceBaseAndStudents=Manager.loadSession(ListOfPracticeBaseAndStudents.class,SessionListKey);
	}

	private String jumpURL="function_Export_display.action";
		public String getJumpURL() {return this.jumpURL;}
		public void setJumpURL(String a) {this.jumpURL=a;}

	@Override
	public String execute(){
		return Manager.tips("该项目不可用!","jump");
	}
	
	
	
	private String practiceBaseName;
		public void setPracticeBaseName(String a) {this.practiceBaseName=Field.s2S(a);}
		public String getPracticeBaseName() {return this.practiceBaseName;}
	private String majorName;
		public void setMajorName(String a){this.majorName=Field.s2S(a);}
		public String getMajorName(){return majorName;}
	

	/*
	 * 下载模板
	 */
	private String downloadFileName;
		public void setDownloadFileName(String a){
			this.downloadFileName=a;
			try{this.downloadFileName=new String(a.getBytes("gb2312"), "iso8859-1");
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
				this.downloadFileName=a;
			}//*/
		}
		public String getDownloadFileName(){return this.downloadFileName;}
	private OutputStream downloadOutputStream=null;
	protected String downloadByIO(SpecialIO io,int year,PracticeBase pb,List<Student> students,String majorName,OutputStream stream) throws IOException{
		return io.createPracticeBaseConsultationLetter(year,pb,students,majorName,stream);
	}
	public String download(){//下载模板
		System.out.println(">> TableOperationAction:download > practiceBaseName="+this.practiceBaseName);
		if(this.practiceBaseAndStudents==null)
			return Manager.tips("该项目未初始化!","jump");
		ListOfPracticeBaseAndStudents.RegionPair.PracticeBasePair pair=
				this.practiceBaseAndStudents.get(this.practiceBaseName);
		if(pair==null)
			return Manager.tips("实习基地名称有误!","jump");
		List<Student> students=new ArrayList<Student>();
		if(this.majorName==null||this.majorName.isEmpty()) {
			students.addAll(pair.getStudents());
			this.majorName=null;
		}else for(Student stu:pair.getStudents())
			if(this.majorName.equals(stu.getMajor()))
				students.add(stu);
		System.out.println(">> TableOperationAction:download > create download file.");
		downloadOutputStream=new ByteArrayOutputStream();
		try{
			String fileName=this.downloadByIO((SpecialIO)Base.io(),
					this.getAnnual().getYear(),pair.getPracticeBase(),students,this.majorName,downloadOutputStream);
			this.setDownloadFileName(fileName);//设置下载文件名称
			this.downloadOutputStream.flush();
		}catch(IOException e){
			downloadOutputStream=null;
			return Manager.tips("服务器开小差去了，暂时无法下载！",e,"jump");
		}
		System.out.println(">> TableOperationAction:download <downloadAttachment");
		return "downloadAttachment";
	}
	public InputStream getDownloadAttachment(){//实际上获取的输出流，使用getter获取的downloadAttachment
		if(downloadOutputStream==null) return null;
		System.out.println(">> TableOperationAction:downloadAttachment > ");
		byte[] data=((ByteArrayOutputStream)downloadOutputStream).toByteArray();
		try {
			this.downloadOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.downloadOutputStream=null;
		ByteArrayInputStream in=new ByteArrayInputStream(data);
	/*	try {
			ServletActionContext.getResponse().setHeader("Content-Disposition","attachment;downloadFileName="+java.net.URLEncoder.encode(this.downloadFileName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}//*/
		return in;
	}
	


}
