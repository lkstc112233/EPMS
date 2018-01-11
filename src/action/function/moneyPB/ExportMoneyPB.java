package action.function.moneyPB;

import java.io.*;

import com.opensymphony.xwork2.ActionSupport;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.staticObject.PracticeBase;

public class ExportMoneyPB extends ActionSupport{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private ListOfPracticeBaseAndMoney practiceBaseAndStudents;
	
	public ListOfPracticeBaseAndMoney getPracticeBaseAndStudents(){return this.practiceBaseAndStudents;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportMoneyPB(){
		super();
		this.practiceBaseAndStudents=Manager.loadSession(ListOfPracticeBaseAndMoney.class,SessionListKey);
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
//TODO	return io.createPracticeBaseConsultationLetter(year,pb,majorName,stream);
		return null;
	}
	public String download(){//下载模板
		System.out.println(">> ExportMoneyPB:download > practiceBaseName="+this.practiceBaseName);
		if(this.practiceBaseAndStudents==null)
			return Manager.tips("该项目未初始化!","jump");
		ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair=
				this.practiceBaseAndStudents.get(this.practiceBaseName);
		if(pair==null)
			return Manager.tips("实习基地名称有误!","jump");
		final Boolean status=false;
		if(status!=null && (status^pair.getPracticeBase().getStatus()))
			return Manager.tips("实习基地是非正式实习基地，不能提供经费明细！","jump");
		System.out.println(">> ExportMoneyPB:download > create download file.");
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
		System.out.println(">> ExportMoneyPB:download <downloadAttachment");
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
