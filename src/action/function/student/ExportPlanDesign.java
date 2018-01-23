package action.function.student;

import java.io.*;

import action.*;
import obj.*;
import obj.annualTable.list.List_Region_PracticeBase_Student;

/**
 * 导出实习生名单
 */
public class ExportPlanDesign extends Action{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}

	private List_Region_PracticeBase_Student list;
	
	public List_Region_PracticeBase_Student getList(){return this.list;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportPlanDesign(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBase_Student.class,SessionListKey);
	}

	@Override
	public String execute(){
		return this.jumpBackWithTips("该项目不可用!");
	}


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
	protected String downloadByIO(SpecialIO io,int year,List_Region_PracticeBase_Student list,Boolean status,OutputStream stream) throws IOException{
		return io.createPlanDesign(year,list,status,stream);
	}
	public String download(){//下载模板
		System.out.println(">> ExportPlanDesign:download >");
		if(this.list==null)
			return this.jumpBackWithTips("该项目未初始化!");
		System.out.println(">> ExportPlanDesign:download > create download file.");
		this.downloadOutputStream=new ByteArrayOutputStream();
		try{
			String fileName=this.downloadByIO((SpecialIO)Base.io(),
					this.getAnnual().getYear(),this.list,status,
					downloadOutputStream);
			this.setDownloadFileName(fileName);//设置下载文件名称
			this.downloadOutputStream.flush();
		}catch(IOException e){
			this.downloadOutputStream=null;
			return this.jumpBackWithTips("服务器开小差去了，暂时无法下载！",e);
		}
		System.out.println(">> ExportPlanDesign:download <downloadAttachment");
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
