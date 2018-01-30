package action.function.moneyMajor;

import java.io.*;
import java.util.*;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.staticObject.PracticeBase;

public class ExportAllMoneyMajor extends Action{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private ListOfPracticeBaseAndMoney practiceBaseAndStudents;
	
	public ListOfPracticeBaseAndMoney getPracticeBaseAndStudents(){return this.practiceBaseAndStudents;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportAllMoneyMajor(){
		super();
		this.practiceBaseAndStudents=Manager.loadSession(ListOfPracticeBaseAndMoney.class,SessionListKey);
	}

	@Override
	public String execute(){
		return this.returnWithTips(NONE,"该项目不可用!");
	}
	
	
	
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
	protected String downloadByIO(SpecialIO io,int year,ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair, OutputStream stream) throws IOException{
		return io.createPracticeBaseMoney(year,pair,stream);
	}
	public String download(){//下载模板
		if(this.practiceBaseAndStudents==null)
			return this.returnWithTips(NONE,"该项目未初始化!");
		//设置下载文件名称
		String fileName=String.format("%d年免费师范生教育实习实习基地经费明细表及回执单.zip",
				this.getAnnual().getYear());
		this.setDownloadFileName(fileName);
		//准备文件内容
		final Boolean status=false;
		Map<String,OutputStream> files=new HashMap<String,OutputStream>();
		for(ListOfPracticeBaseAndMoney.RegionPair rp:this.practiceBaseAndStudents.getList()) {
			for(ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair:rp.getList()) {
				PracticeBase pb=pair.getPracticeBase();
				if(status!=null && (status^pb.getStatus()))
					continue;
				System.out.println(">> ExportAllMoneyPB:download > create download file. practiceBaseName="+pb.getName());
				OutputStream out=new ByteArrayOutputStream();
				try{
					String name=this.downloadByIO((SpecialIO)Base.io(),
							this.getAnnual().getYear(),pair,out);
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
		System.out.println(">> ExportAllMoneyPB:download <downloadAttachment");
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
