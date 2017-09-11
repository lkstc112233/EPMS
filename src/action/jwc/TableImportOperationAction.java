package action.jwc;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.opensymphony.xwork2.ActionContext;

import obj.Base;
import obj.SQLCollection;

/**
 * TableImportOperationAction
 */
public class TableImportOperationAction extends action.login.AnnualAction{
	private static final long serialVersionUID = 5998268336475528662L;
	
	public TableImportOperationAction() throws SQLException, NoSuchFieldException, SecurityException{
		super();
	}

	@Override
	public String execute(){
		if(!executive)
			return display();
		return display();
	}
	
	@Override
	public String display(){
		System.out.println(">> TableImportOperationAction:display > year="+this.getYear());
		
		
		System.out.println(">> TableImportOperationAction:display <NONE");
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
		System.out.println(">> TableImportOperationAction:upload > year="+this.getYear());
		System.out.println(">> TableImportOperationAction:upload > uploadFileContentType="+this.getUploadFileContentType());
		System.out.println(">> TableImportOperationAction:upload > uploadFileFileName="+this.getUploadFileFileName());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		if(this.getUploadFile()==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"上传了空文件！");
			System.out.println(">> TableImportOperationAction:upload > 上传了空文件！");
			System.out.println(">> TableImportOperationAction:upload <NONE");
			return NONE;
		}
		List<? extends Base> content=null;
		List<Integer> errorIndex=new ArrayList<Integer>();
		try(FileInputStream in=new FileInputStream(this.getUploadFile());){
			content=SQLCollection.io.readExcel(clazz,in,errorIndex,this.getYear());
		}
		catch(IOException e){
			e.printStackTrace();
			System.out.println(">> TableImportOperationAction:upload > 文件错误！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"文件错误！");
			System.out.println(">> TableImportOperationAction:upload <NONE");
			return NONE;
		}
		catch (EncryptedDocumentException e) {
			e.printStackTrace();
			System.out.println(">> TableImportOperationAction:upload > 解码错误！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"解码错误！");
			System.out.println(">> TableImportOperationAction:upload <NONE");
			return NONE;
		}
		catch (InvalidFormatException e) {
			e.printStackTrace();
			System.out.println(">> TableImportOperationAction:upload > 格式错误！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"格式错误！");
			System.out.println(">> TableImportOperationAction:upload <NONE");
			return NONE;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.out.println(">> TableImportOperationAction:upload > 初始化实例错误！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"初始化实例错误！");
			System.out.println(">> TableImportOperationAction:upload <NONE");
			return NONE;
		}
		if(content==null){
			System.out.println(">> TableImportOperationAction:upload > 文件读取失败！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"文件读取失败！");
			System.out.println(">> TableImportOperationAction:upload <NONE");
			return NONE;
		}
		if(content.isEmpty()){
			System.out.println(">> TableImportOperationAction:upload > 文件为空！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"文件为空！");
			System.out.println(">> TableImportOperationAction:upload <NONE");
			return NONE;
		}
		for(int i=0;i<content.size();i++){
			try{
				content.get(i).create();
			}catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
				e.printStackTrace();
				errorIndex.add(i);
			}
		}
		if(!errorIndex.isEmpty()){
			StringBuilder error=new StringBuilder("上传失败序号：");
			for(int i:errorIndex){
				error.append(i);
				error.append(';');
			}
			System.out.println(">> TableImportOperationAction:upload > "+error);
			session.put(token.ActionInterceptor.ErrorTipsName,
					error);
			System.out.println(">> TableImportOperationAction:upload <SUCCESS");
			return SUCCESS;
		}
		System.out.println(">> TableImportOperationAction:upload > 上传成功！");
		session.put(token.ActionInterceptor.ErrorTipsName,
				"上传成功！");
		System.out.println(">> TableImportOperationAction:upload <SUCCESS");
		return SUCCESS;
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
		System.out.println(">> TableImportOperationAction:download > year="+this.getYear());
		System.out.println(">> TableImportOperationAction:download > tableName="+this.getTableName());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		this.setDownloadFileName(Base.getSQLTableName(clazz)+"模板.xlsx");//设置下载文件名称
		System.out.println(">> TableImportOperationAction:download > tableName="+this.getTableName());
		System.out.println(">> TableImportOperationAction:download > downloadFielName="+this.getDownloadFileName());
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
		System.out.println(">> TableImportOperationAction:download <downloadAttachment");
		return "downloadAttachment";
	}
	public InputStream getDownloadAttachment(){//实际上获取的输出流，使用getter获取的downloadAttachment
		if(downloadOutputStream==null) return null;
		System.out.println(">> TableImportOperationAction:downloadAttachment > ");
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
