package action.function.teacher;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.ListOfRegionAndPracticeBaseAndInnerPerson.RegionPair.PracticeBasePair;
import obj.staticObject.InnerPerson;
import obj.staticSource.School;

/**
 * 导出督导任务书
 */
public class ExportSupervisorAllMandate extends Action{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private ListOfRegionAndPracticeBaseAndInnerPerson regionAndPracticeBaseAndInnerPerson;
	
	public ListOfRegionAndPracticeBaseAndInnerPerson getRegionAndPracticeBaseAndInnerPerson(){return this.regionAndPracticeBaseAndInnerPerson;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportSupervisorAllMandate(){
		super();
		this.regionAndPracticeBaseAndInnerPerson=Manager.loadSession(ListOfRegionAndPracticeBaseAndInnerPerson.class, SessionListKey);
	}

	@Override
	public String execute(){
		return this.jumpBackWithTips("该项目不可用!");
	}
	
	

	private String supervisorId;
		public void setSupervisorId(String a) {this.supervisorId=Field.s2S(a);}
		public String getSuperviserId() {return this.supervisorId;}
		

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
	protected String downloadByIO(SpecialIO io,int year,InnerPerson supervisor,PracticeBasePair pair,int superviseIndex,OutputStream stream) throws IOException{
		return io.createSupervisorMandate(year,supervisor,pair,superviseIndex,stream);
	}
	public String download(){//下载模板
		System.out.println(">> ExportSupervisorAllMandate:download > supervisorId="+this.supervisorId);
		if(this.regionAndPracticeBaseAndInnerPerson==null)
			return this.jumpBackWithTips("该项目未初始化!");
		InnerPerson supervisor;
		School school;
		try {
			supervisor=new InnerPerson(supervisorId);
			school=new School(supervisor.getSchool());
		}catch(IllegalArgumentException | SQLException e) {
			return this.jumpBackWithTips("督导老师工号("+supervisorId+")不正确",e);
		}
		//设置下载文件名称
		String fileName=String.format("%d年[%s%s]免费师范生教育实习督导任务书.zip",
				this.getAnnual().getYear(),
				school.getSubName(),
				supervisor.getName());
		this.setDownloadFileName(fileName);
		//准备文件内容
		Map<String,OutputStream> files=new HashMap<String,OutputStream>();
		for(ListOfRegionAndPracticeBaseAndInnerPerson.RegionPair rp:this.regionAndPracticeBaseAndInnerPerson.getList()) {
			for(ListOfRegionAndPracticeBaseAndInnerPerson.RegionPair.PracticeBasePair pair:rp.getList()) {
				int superviseIndex=0;
				for(InnerPerson inner:pair.getSupervisor()) {
					if(inner.getId().equals(supervisor.getId())) {
						System.out.println(">> ExportAllStudentList:download > create download file. practiceBaseName="+pair.getPracticeBase().getName()+","+Supervise.getTypeNameList()[superviseIndex]);
						OutputStream out=new ByteArrayOutputStream();
						try{
							String name=this.downloadByIO((SpecialIO)Base.io(),
									this.getAnnual().getYear(),inner,pair,superviseIndex,out);
							files.put(name,out);
						}catch(IOException e){
							downloadOutputStream=null;
							return this.jumpBackWithTips("创建文件失败，暂时无法下载！",e);
						}
					}
					superviseIndex++;
				}
			}
		}
		try{
			this.downloadOutputStream=IOHelper.ZIP(files);
			this.downloadOutputStream.flush();
		} catch (IOException e) {
			this.downloadOutputStream=null;
			return this.jumpBackWithTips("压缩文件失败，暂时无法下载！",e);
		}
		System.out.println(">> ExportSupervisorAllMandate:download <downloadAttachment");
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
