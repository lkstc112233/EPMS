package action.function.teacher;

import java.io.*;

import action.*;
import obj.*;
import obj.annualTable.list.List_Region_PracticeBaseRegionLeaderSuperviseSupervisors;
import obj.annualTable.list.PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors;
import obj.staticObject.InnerPerson;

/**
 * 导出督导任务书
 */
public class ExportSupervisorMandate extends Action{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private List_Region_PracticeBaseRegionLeaderSuperviseSupervisors list;
	
	public List_Region_PracticeBaseRegionLeaderSuperviseSupervisors getlist(){return this.list;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportSupervisorMandate(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBaseRegionLeaderSuperviseSupervisors.class, SessionListKey);
	}

	@Override
	public String execute(){
		return this.returnWithTips(NONE,"该项目不可用!");
	}
	
	

	private String practiceBaseName;
		public void setPracticeBaseName(String a) {this.practiceBaseName=Field.s2S(a);}
		public String getPracticeBaseName() {return this.practiceBaseName;}
	private int superviseIndex;
		public void setSuperviseIndex(String a) {this.superviseIndex=Field.s2i(a,-1);}
		public int getSuperviseIndex() {return this.superviseIndex;}
		

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
	protected String downloadByIO(SpecialIO io,int year,InnerPerson supervisor,PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors pblss,int superviseIndex,OutputStream stream) throws IOException{
		return io.createSupervisorMandate(year,supervisor,pblss,superviseIndex,stream);
	}
	public String download(){//下载模板
		System.out.println(">> ExportSupervisorMandate:download > practiceBaseName="+this.practiceBaseName+",superviseIndex="+this.superviseIndex);
		if(this.list==null)
			return this.returnWithTips(NONE,"该项目未初始化!");
		PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors
			pair=this.list.getByPracticeBaseName(practiceBaseName);
		if(pair==null)
			return this.returnWithTips(NONE,"实习基地名称不正确!");
		if(superviseIndex<0||superviseIndex>=pair.getSupervisors().length)
			return this.returnWithTips(NONE,"督导序号不正确");
		InnerPerson supervisor=pair.getSupervisors()[superviseIndex];
		System.out.println(">> ExportSupervisorMandate:download > create download file.");
		downloadOutputStream=new ByteArrayOutputStream();
		try{
			String fileName=this.downloadByIO((SpecialIO)Base.io(),
					this.getAnnual().getYear(),supervisor,pair,superviseIndex,downloadOutputStream);
			this.setDownloadFileName(fileName);//设置下载文件名称
			this.downloadOutputStream.flush();
		}catch(IOException e){
			downloadOutputStream=null;
			return this.returnWithTips(NONE,"服务器开小差去了，暂时无法下载！",e);
		}
		System.out.println(">> ExportSupervisorMandate:download <downloadAttachment");
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
