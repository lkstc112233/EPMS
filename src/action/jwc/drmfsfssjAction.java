package action.jwc;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import obj.Base;
import obj.SQLCollection;

/**
 * 导入免费师范生数据
 */
public class drmfsfssjAction extends action.login.AnnualAction{
	private static final long serialVersionUID = 5998268336475528662L;
	
	public drmfsfssjAction() throws SQLException, NoSuchFieldException, SecurityException{
		super();
	}
	
	
	

	@Override
	public String execute(){
		if(!executive)
			return display();
		System.out.println(">> 导入免费师范生数据Action:execute > year="+this.getYear());
	//	Map<String, Object> session=ActionContext.getContext().getSession();
		if(Base.getClassForName(tableName)==null)
			return display();
		if(uploadFile==null) return download();
		else return upload();
	}
	
	@Override
	public String display(){
		System.out.println(">> 导入免费师范生数据Action:display > year="+this.getYear());
		
		
		System.out.println(">> 导入免费师范生数据Action:display <NONE");
		return NONE;
	}

	/**
	 * 文件操作
	 */
	private String tableName;	public void setTableName(String a){this.tableName=a;}public String getTableName(){return this.tableName;}
	/*
	 * 上传文件
	 */
	private File uploadFile=null;			public File getUploadFile(){return uploadFile;}public void setUploadFile(File uploadFile){this.uploadFile=uploadFile;}
	private String uploadFileContentType;	public String getUploadFileContentType(){return this.uploadFileContentType;}public void setUploadFileContentType(String a){this.uploadFileContentType=a;}
	private String uploadFileFileName;	public String getUploadFileFileName(){return this.uploadFileFileName;}public void setUploadFileFileName(String a){this.uploadFileFileName=a;}
	public String upload(){//上传文件
		return NONE;
	}

	/*
	 * 下载模板
	 */
	private String downloadFileName;
		public void setDownloadFileName(String a){
			try{this.downloadFileName=new String(a.getBytes("gb2312"), "iso8859-1");
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
				this.downloadFileName=a;
			}
		}
		public String getDownloadFileName(){return this.downloadFileName;}
	private OutputStream downloadOutputStream=null;
	public String download(){//下载模板
		System.out.println(">> 导入免费师范生数据Action:download > year="+this.getYear());
		System.out.println(">> 导入免费师范生数据Action:download > tableName="+this.getTableName());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		this.setDownloadFileName(Base.getSQLTableName(clazz)+"模板.xlsx");//设置下载文件名称
		System.out.println(">> 导入免费师范生数据Action:download > tableName="+this.getTableName());
		System.out.println(">> 导入免费师范生数据Action:download > downloadFielName="+this.getDownloadFileName());
		downloadOutputStream=new ByteArrayOutputStream();
		try{
			SQLCollection.io.getModelExcel(clazz,downloadOutputStream);
			this.downloadOutputStream.flush();
		}catch(IOException e){
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开小差去了，暂时无法下载！");
			downloadOutputStream=null;
			return NONE;
		}
		System.out.println(">> 导入免费师范生数据Action:download <downloadAttachment");
		return "downloadAttachment";
	}
	public InputStream getDownloadAttachment(){
		if(downloadOutputStream==null) return null;
		System.out.println(">> 导入免费师范生数据Action:downloadAttachment > ");
		byte[] data=((ByteArrayOutputStream)downloadOutputStream).toByteArray();
		try {
			this.downloadOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.downloadOutputStream=null;
		ByteArrayInputStream in=new ByteArrayInputStream(data);
		return in;
	}
	
	
	
}
