package action.function.teacher;

import java.io.*;

import com.opensymphony.xwork2.ActionSupport;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.staticObject.PracticeBase;

/**
 * 导出督导任务书
 */
public class ExportPracticeBaseInfomation extends ActionSupport{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private ListOfRegionAndPracticeBaseAndInnerPerson regionAndPracticeBaseAndInnerPerson;
	
	public ListOfRegionAndPracticeBaseAndInnerPerson getRegionAndPracticeBaseAndInnerPerson(){return this.regionAndPracticeBaseAndInnerPerson;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportPracticeBaseInfomation(){
		super();
		this.regionAndPracticeBaseAndInnerPerson=Manager.loadSession(ListOfRegionAndPracticeBaseAndInnerPerson.class, SessionListKey);
	}

	private String jumpURL=Export.ActionName;
		public String getJumpURL() {return this.jumpURL;}
		public void setJumpURL(String a) {this.jumpURL=a;}

	@Override
	public String execute(){
		return Manager.tips("该项目不可用!","jump");
	}
	
	

	private String practiceBaseName;
		public void setPracticeBaseName(String a) {this.practiceBaseName=Field.s2S(a);}
		public String getPracticeBaseName() {return this.practiceBaseName;}
		

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
	private ByteArrayOutputStream downloadOutputStream=null;
	protected String downloadByIO(SpecialIO io,int year,PracticeBase pb,OutputStream stream) throws IOException{
		return io.createPracticeBaseInfomation(year,pb,stream);
	}
	public String download(){//下载模板
		System.out.println(">> ExportPracticeBaseConsultationLetter:download > practiceBaseName="+this.practiceBaseName);
		if(this.regionAndPracticeBaseAndInnerPerson==null)
			return Manager.tips("该项目未初始化!","jump");
		ListOfRegionAndPracticeBaseAndInnerPerson.RegionPair.PracticeBasePair
			pair=this.regionAndPracticeBaseAndInnerPerson.get(practiceBaseName);
		if(pair==null)
			return Manager.tips("实习基地名称不正确!","jump");
		System.out.println(">> ExportPracticeBaseConsultationLetter:download > create download file.");
		downloadOutputStream=new ByteArrayOutputStream();
		try{
			String fileName=this.downloadByIO((SpecialIO)Base.io(),
					this.getAnnual().getYear(),pair.getPracticeBase(),downloadOutputStream);
			this.setDownloadFileName(fileName);//设置下载文件名称
			this.downloadOutputStream.flush();
		}catch(IOException e){
			downloadOutputStream=null;
			return Manager.tips("服务器开小差去了，暂时无法下载！",e,"jump");
		}
		System.out.println(">> ExportPracticeBaseConsultationLetter:download <downloadAttachment");
		return "downloadAttachment";
	}
	public InputStream getDownloadAttachment(){//实际上获取的输出流，使用getter获取的downloadAttachment
		ByteArrayInputStream in=IOHelper.ByteArrayOutStream2InputStream(this.downloadOutputStream);
		try {
			this.downloadOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	/*	try {
			ServletActionContext.getResponse().setHeader("Content-Disposition","attachment;downloadFileName="+java.net.URLEncoder.encode(this.downloadFileName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}//*/
		return in;
	}
	


}
