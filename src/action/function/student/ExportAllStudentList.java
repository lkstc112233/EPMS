package action.function.student;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.list.Leaf;
import obj.annualTable.list.List_Region_PracticeBaseRegion_Student;
import obj.annualTable.list.Node;
import obj.annualTable.list.PracticeBaseWithRegion;
import obj.staticObject.PracticeBase;
import obj.staticSource.Major;

/**
 * 导出实习生名单
 */
public class ExportAllStudentList extends Action{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}

	private List_Region_PracticeBaseRegion_Student list;
	
	public List_Region_PracticeBaseRegion_Student getList(){return this.list;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportAllStudentList(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBaseRegion_Student.class,SessionListKey);
	}

	@Override
	public String execute(){
		return this.returnWithTips(NONE,"该项目不可用!");
	}
	
	
	
	private String majorName;
		public void setMajorName(String a){this.majorName=Field.s2S(a);}
		public String getMajorName(){return majorName;}
	private Boolean status=false;
		public void setStatus(String a){this.status=Field.s2B(a);}
		public Boolean getStatus(){return status;}
	

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
	protected String downloadByIO(SpecialIO io,int year,PracticeBase pb,Major major,OutputStream stream) throws IOException{
		return io.createStudentList(year,pb,major,stream);
	}
	public String download(){//下载模板
		if(this.list==null)
			return this.returnWithTips(NONE,"该项目未初始化!");
		//设置下载文件名称
		Major major=null;
		if(majorName!=null) try {
			major=new Major(majorName);
		} catch (IllegalArgumentException | SQLException e1) {
			return this.returnWithTips(NONE,"专业名称有误!");
		}
		String fileName=String.format("%d年%s免费师范生教育实习学生名单%s.zip",
				this.getAnnual().getYear(),
				(major==null?"":("("+major.getDescription()+")")),
				status==null?"(含特殊基地)":status?"(特殊基地)":"");
		this.setDownloadFileName(fileName);
		//准备文件内容
		Map<String,OutputStream> files=new HashMap<String,OutputStream>();
		for(Node<Region,Leaf<PracticeBaseWithRegion,Student>> rp:this.list.getList()) {
			for(Leaf<PracticeBaseWithRegion,Student> pair:rp.getList()) {
				PracticeBase pb=pair.getT().getPracticeBase();
				if(status!=null && (status^pb.getStatus()))
					continue;
				System.out.println(">> ExportAllStudentList:download > create download file. practiceBaseName="+pb.getName());
				OutputStream out=new ByteArrayOutputStream();
				try{
					String name=this.downloadByIO((SpecialIO)Base.io(),
							this.getAnnual().getYear(),pair.getT().getPracticeBase(),major,
							out);
					files.put(name,out);
				}catch(IOException e){
					downloadOutputStream=null;
					return this.returnWithTips(NONE,"创建文件失败，暂时无法下载！",e);
				}
			}
		}
		try{
			this.downloadOutputStream=IOHelper.ZIP(files);
			this.downloadOutputStream.flush();
		} catch (IOException e) {
			this.downloadOutputStream=null;
			return this.returnWithTips(NONE,"压缩文件失败，暂时无法下载！",e);
		}
		System.out.println(">> ExportAllStudentList:download <downloadAttachment");
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
