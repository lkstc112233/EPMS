package action;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import obj.*;

public abstract class TableOperationAction extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	static public final String SessionSearchKey="TableOperationAction_Search";
	static public final String SessionChooseKey="TableOperationAction_Choose";
	
	
	protected Search search=null;//查询信息
	private Integer[] choose;//操作项
	private Base operateBase;//当为update|delete时，此处存放旧数据
	
	public Search getSearch(){return this.search;}
	public Integer[] getChoose(){return this.choose;}
	public Base getOperateBase(){return this.operateBase;}


	public TableOperationAction(){
		super();
		this.setup();
	}
	
	protected void setup(){
		if(this.search==null)
			this.search=Manager.loadSession(Search.class,SessionSearchKey);
		else Manager.saveSession(SessionSearchKey,this.search);
		if(this.search==null) return;
		//===
		if(this.choose==null)
			this.choose=Manager.loadSession(Integer[].class,SessionChooseKey);
		else Manager.saveSession(SessionChooseKey,this.choose);
		if(this.choose!=null && this.choose.length>=2 && this.choose[0]!=null && this.choose[1]!=null
				&& (0<=this.choose[0] && this.choose[0]<this.search.getParam().size())){
			JoinParam.Part part=this.search.getParam().getList().get(this.choose[0]);
			try{
				this.operateBase=part.getClazz().newInstance();
			}catch(InstantiationException | IllegalArgumentException | IllegalAccessException e){
			}
			if(this.choose[1]==null || (0<=this.choose[1] && this.choose[1]<this.search.getResult().size())){
				//update|delete 备份旧数据
				this.search.getResult().get(this.choose[1])[this.choose[0]].copyTo(this.operateBase);
			}else this.choose[1]=-1;
			Manager.saveSession(SessionChooseKey,this.choose);
		}else{
			this.operateBase=null;
			this.choose=new Integer[]{-1,-1};
			Manager.removeSession(SessionChooseKey);
		}
	}
	
	protected abstract Search createSearch() throws Exception;

	public String display(){
		if(this.search==null) try{
			this.search=this.createSearch();
		}catch(Exception e){
			return Manager.tips("搜索结果实例初始化失败！",e,NONE);
		}
		this.setup();
		return NONE;
	}
	
	
	@Override
	public String execute(){//执行查询
		if(this.search==null)
			return Manager.tips("搜索结果实例初始化失败！",display());
		System.out.println(">> TableOperationAction:execute > start");
		try {
			this.search.execute();
		} catch (IllegalAccessException | InstantiationException e) {
			return Manager.tips("搜索结果生成失败！"
					,e,NONE);
		} catch (SQLException e) {
			Manager.tips("搜索操作失败，可能是数据库开小差去了！",
					e,NONE);
		}
		Manager.removeSession(SessionChooseKey);
		this.choose=null;
		System.out.println(">> TableOperationAction:execute > resultSet count="+this.search.getResult().size());
		return display();
	}
	
	

	/**
	 * 更新选中条（根据choose值）
	 */
	public String update(){
		if(this.search==null)
			return Manager.tips("搜索结果实例初始化失败！",display());
		System.out.println(">> TableOperationAction:update > start");
		if(this.operateBase==null)
			return Manager.tips("操作选择错误！",
					NONE);
		Base newBase=null;
		try{
			newBase=this.search.getResult().get(this.choose[1])[this.choose[0]];
		}catch(Exception e){
		}
		if(newBase==null)
			return Manager.tips("选择条目错误！",
					NONE);
		if(!this.search.getBaseRestraint().checkBase(newBase,/*setIfFalse*/false))
			return Manager.tips("不能修改为其他[年份/部院系]条目！",
					NONE);
		try{
			this.operateBase.update(newBase);
		}catch (IllegalArgumentException e) {
			return Manager.tips("修改时参数错误！",
					e,NONE);
		} catch (SQLException e) {
			return Manager.tips("修改时遇到数据库错误！",
					e,NONE);
		}
		return Manager.tips("修改成功！",
				execute());
	}
	/**
	 * 删除选中条（根据choose值）
	 */
	public String delete(){
		if(this.search==null)
			return Manager.tips("搜索结果实例初始化失败！",display());
		System.out.println(">> TableOperationAction:delete > start");
		this.operateBase=null;
		try{
			this.operateBase=this.search.getResult().get(this.choose[1])[this.choose[0]];
		}catch(Exception e){
		}
		if(this.operateBase==null)
			return Manager.tips("操作选择错误！",
					NONE);
		if(!this.search.getBaseRestraint().checkBase(this.operateBase,/*setIfFalse*/false))
			return Manager.tips("不能删除其他[年份/部院系]条目！",
					NONE);
		try{
			this.operateBase.delete();
		}catch (IllegalArgumentException e) {
			return Manager.tips("删除时参数错误！",
					e,NONE);
		} catch (SQLException e) {
			return Manager.tips("删除时遇到数据库错误！",
					e,NONE);
		}
		return Manager.tips("删除成功！",
				execute());
	}
	/**
	 * 新建条（新建createBase）
	 */
	public String create(){
		if(this.search==null)
			return Manager.tips("搜索结果实例初始化失败！",display());
		System.out.println(">> TableOperationAction:update > start");
		if(this.operateBase==null)
			return Manager.tips("操作选择错误！",
					NONE);
		if(!this.search.getBaseRestraint().checkBase(this.operateBase,/*setIfFalse*/false))
			return Manager.tips("不能新建其他[年份/部院系]条目！",
					NONE);
		try{
			this.operateBase.create();
		} catch (IllegalArgumentException e) {
			return Manager.tips("创建时参数错误！",
					e,NONE);
		} catch (IllegalAccessException e) {
			return Manager.tips("创建时参数权限错误！",
					e,NONE);
		} catch (SQLException e) {
			return Manager.tips("创建时遇到数据库错误！",
					e,NONE);
		}
		return Manager.tips("创建成功！",
				execute());
	}
	
	
	
	
	
	
	//============================================
	private String fileTableName;
		public String getFileTableName(){return this.fileTableName;}
		public void setFileTableName(String tableName){this.fileTableName=tableName==null||tableName.isEmpty()?null:tableName;}
	/*
	 * 上传文件
	 */
	private File uploadFile=null;			public File getUploadFile(){return uploadFile;}public void setUploadFile(File uploadFile){this.uploadFile=uploadFile;}
	private String uploadFileContentType;	public String getUploadFileContentType(){return this.uploadFileContentType;}public void setUploadFileContentType(String a){this.uploadFileContentType=a;}
	private String uploadFileFileName;	public String getUploadFileFileName(){return this.uploadFileFileName;}public void setUploadFileFileName(String a){this.uploadFileFileName=a;}
	public String upload(){//上传文件
		System.out.println(">> TableOperationAction:upload > uploadFileContentType="+this.getUploadFileContentType());
		System.out.println(">> TableOperationAction:upload > uploadFileFileName="+this.getUploadFileFileName());
		Class<? extends Base> clazz=Base.getClassForName(this.fileTableName);
		if(clazz==null)
			return Manager.tips("选择了错误的表名称",display());
		if(this.getUploadFile()==null)
			return Manager.tips("上传了空文件！",display());
		List<? extends Base> content=null;
		List<Integer> errorIndex=new ArrayList<Integer>();
		try(FileInputStream in=new FileInputStream(this.getUploadFile());){
			content=Base.io().readExcel(clazz,in,errorIndex,this.getSearch().getBaseRestraint());
		}
		catch(IOException e){
			return Manager.tips("文件错误！",e,display());
		}
		catch (EncryptedDocumentException e) {
			return Manager.tips("解码错误！",e,display());
		}
		catch (InvalidFormatException e) {
			return Manager.tips("格式错误！",e,display());
		} catch (InstantiationException | IllegalAccessException e) {
			return Manager.tips("初始化实例错误！",e,display());
		}
		if(content==null)
			return Manager.tips("文件读取失败！",display());
		if(content.isEmpty())
			return Manager.tips("文件为空！",display());
		for(int i=0;i<content.size();i++) try{
			Base b=content.get(i);
			if(b.exist())
				b.update();
			else
				b.create();
		}catch (IllegalArgumentException | IllegalAccessException | SQLException | InstantiationException e) {
			e.printStackTrace();
			errorIndex.add(i);
		}
		if(!errorIndex.isEmpty()){
			StringBuilder error=new StringBuilder("上传失败序号：");
			for(int i:errorIndex){
				error.append(i);
				error.append(';');
			}
			return Manager.tips(error.toString(),display());
		}
		Manager.tips("上传成功！");
		return display();
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
		System.out.println(">> TableOperationAction:download > tableName="+this.fileTableName);
		Class<? extends Base> clazz=Base.getClassForName(this.fileTableName);
		if(clazz==null)
			return Manager.tips("选择了错误的表名称",display());
		this.setDownloadFileName(Base.getSQLTableName(clazz)+"模板.xlsx");//设置下载文件名称
		System.out.println(">> TableOperationAction:download > tableName="+this.fileTableName);
		System.out.println(">> TableOperationAction:download > downloadFielName="+this.getDownloadFileName());
		downloadOutputStream=new ByteArrayOutputStream();
		try{
			Base.io().getModelExcel(clazz,downloadOutputStream);
			this.downloadOutputStream.flush();
		}catch(IOException e){
			downloadOutputStream=null;
			return Manager.tips("服务器开小差去了，暂时无法下载！",e,display());
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
		try {
			ServletActionContext.getResponse().setHeader("Content-Disposition","attachment;downloadFileName="+java.net.URLEncoder.encode(this.downloadFileName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return in;
	}
	


}
