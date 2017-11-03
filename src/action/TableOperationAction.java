package action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import obj.*;

public abstract class TableOperationAction extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private String tableName;
		public String getTableName(){return this.tableName;}
		//public void setTableName(String tableName){this.setupTableName(tableName);}
	static public final String SessionSearchKey="TableOperationAction_Search";
	
	
	private Search<? extends Base> search=null;//查询信息
	private int choose=-1;//操作项
	private Base chooseBase;
	private Base createNewBase;
	
	public Search<? extends Base> getSearch(){return this.search;}
	public int getChoose(){return this.choose;}
	public void setChoose(int x){
		this.choose=x;
		if(this.choose<0)
			this.chooseBase.clear();
		else
			try{this.chooseBase=this.search.getResult().get(this.getChoose());
			}catch(IllegalArgumentException | IndexOutOfBoundsException e){}
	}
	public Base getChooseBase(){return this.chooseBase;}
	public Base getCreateNewBase(){return this.createNewBase;}


	@SuppressWarnings({ "unchecked" })
	public TableOperationAction(){
		super();
		this.search=Manager.loadSession(Search.class,SessionSearchKey);
		if(this.search!=null)
			this.setupTableName(this.search.getClassInfo().getTableName());
	}
	public TableOperationAction(String tableName){
		this();
		this.setupTableName(tableName);
		System.out.println(">> TableOperationAction:constructor > tableName="+this.getTableName());
	}
	
	protected void setupTableName(String tableName){
		this.tableName=tableName;
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		try {
			this.createNewBase=clazz.newInstance();
			if(this.search!=null && this.search.getRestraint()!=null)
				this.search.getRestraint().fitBase(this.createNewBase);
			this.chooseBase=clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		if(this.search!=null && !this.tableName.equals(this.search.getClassInfo().getTableName())){
			Manager.removeSession(SessionSearchKey);
			this.search=null;
		}
	}
	
	protected abstract void setupSearchRestraint()throws NoSuchFieldException,IllegalArgumentException,IllegalAccessException,SQLException;
	/*	this.search.setRestraint(new Search2.jwyRestraint(this.search.getClassInfo(),
			new School(Manager.getUser().getSchool()),
			this.getYear()));
	 */
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String display(){
		System.out.println(">> TableOperationAction:display > tableName="+this.getTableName());
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		try {
			if(this.search==null){
				this.search=new Search(clazz);
				this.setupSearchRestraint();
			}
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | SQLException e) {
			return Manager.tips("搜索结果实例初始化失败！",
					e,NONE);
		}
		if(this.search!=null && this.search.getRestraint()!=null && this.createNewBase!=null)
			this.search.getRestraint().fitBase(this.createNewBase);
		if(this.search!=null)
			Manager.saveSession(SessionSearchKey,this.search);
		System.out.println(">> TableOperationAction:display <NONE");
		return NONE;
	}
	
	
	@Override
	public String execute(){//执行查询
		System.out.println(">> TableOperationAction:execute > tableName="+this.getTableName());
		if(this.search==null){
			System.out.println("搜索结果实例初始化失败！");
			return display();
		}
		//===
		try {
			this.search.execute();
		} catch (IllegalAccessException | InstantiationException e) {
			return Manager.tips("搜索结果实例初始化失败！"
					,e,NONE);
		} catch (SQLException e) {
			Manager.tips("数据库开小差去了！",
					e,NONE);
		}
		this.setChoose(-1);//will clear the chooseBase
		System.out.println(">> TableOperationAction:execute > resultSet count="+this.search.getResult().size());
		return display();
	}
	
	

	/**
	 * 更新选中条（根据choose值）
	 */
	public String update(){
		System.out.println(">> TableOperationAction:update > tableName="+this.getTableName());
		if(this.search==null)
			return Manager.tips("搜索结果实例初始化失败！",
					display());
		//===
		Base b=null;
		try{
			b=this.search.getResult().get(this.getChoose());
		}catch(IllegalArgumentException e){
			return Manager.tips("出错了！",
					NONE);
		}catch(IndexOutOfBoundsException e){
			return Manager.tips("条目选择错误！",
					NONE);
		}
		if(b==null || this.chooseBase==null)
			return Manager.tips("条目选择为空！",
					NONE);
		if(!this.search.getRestraint().fitBase(this.chooseBase))
			return Manager.tips("不能修改为其他[年份/部院系]条目！",
					NONE);
		//b -> update to ->this.chooseBase
		try{
			this.chooseBase.update(b);
		}catch (IllegalArgumentException e) {
			return Manager.tips("修改参数错误！",
					e,NONE);
		} catch (IllegalAccessException e) {
			return Manager.tips("修改权限错误！",
					e,NONE);
		} catch (SQLException e) {
			return Manager.tips("数据库错误！",
					e,NONE);
		}
		this.setChoose(-1);//will clear the chooseBase
		Manager.tips("修改成功！");
		return this.execute();
	}
	/**
	 * 删除选中条（根据choose值）
	 */
	public String delete(){
		System.out.println(">> TableOperationAction:delete > tableName="+this.getTableName());
		if(this.search==null)
			return Manager.tips("搜索结果实例初始化失败！",display());
		//===
		Base b=null;
		try{
			b=this.search.getResult().get(this.getChoose());
		}catch(IllegalArgumentException e){
			return Manager.tips("出错了！",
					NONE);
		}catch(IndexOutOfBoundsException e){
			return Manager.tips("条目选择错误！",
					NONE);
		}
		if(b==null || this.chooseBase==null)
			return Manager.tips("条目选择为空！",
					NONE);
		if(!this.search.getRestraint().fitBase(this.chooseBase))
			return Manager.tips("不能删除其他[年份/部院系]条目！",
					NONE);
		//delete b (in the search result)
		try {
			b.delete();
			this.search.getResult().remove(this.choose);
		}catch (IllegalArgumentException e) {
			return Manager.tips("修改参数错误！",
					e,NONE);
		} catch (IllegalAccessException e) {
			return Manager.tips("修改权限错误！",
					e,NONE);
		} catch (SQLException e) {
			return Manager.tips("数据库错误！",
					e,NONE);
		}
		System.out.println(">> TableOperationAction:delete > 删除成功");
		Manager.tips("删除成功！");
		return this.execute();
	}
	/**
	 * 新建条（新建createBase）
	 */
	public String create(){
		System.out.println(">> TableOperationAction:create > tableName="+this.getTableName());
		if(this.search==null)
			return Manager.tips("搜索结果实例初始化失败！",
					display());
		if(this.createNewBase==null)
			return Manager.tips("条目选择为空！",
					NONE);
		try{
			if(this.createNewBase.checkKeyNull())
				return Manager.tips("新建条目内容不充分，请补全！",
						NONE);
		}catch(IllegalArgumentException | IllegalAccessException e){
			return Manager.tips("服务器开小差去了，暂时无法创建！",
					e,NONE);
		}
		if(!this.search.getRestraint().fitBase(this.createNewBase))
			return Manager.tips("不能新建其他[年份/部院系]条目！",
					NONE);
		//create this.getCreateNewBase()
		try {
			this.createNewBase.create();
		}catch(IllegalArgumentException | IllegalAccessException e) {
			return Manager.tips("服务器开小差去了，暂时无法创建！",
					e,NONE);
		}catch(SQLException e){
			return Manager.tips("数据库发现问题，无法创建该条目！",
					e,NONE);
		}
		this.createNewBase.clear();
		Manager.tips("创建成功！");
		return execute();
	}
	
	
	
	
	
	
	//============================================
	
	/*
	 * 上传文件
	 */
	private File uploadFile=null;			public File getUploadFile(){return uploadFile;}public void setUploadFile(File uploadFile){this.uploadFile=uploadFile;}
	private String uploadFileContentType;	public String getUploadFileContentType(){return this.uploadFileContentType;}public void setUploadFileContentType(String a){this.uploadFileContentType=a;}
	private String uploadFileFileName;	public String getUploadFileFileName(){return this.uploadFileFileName;}public void setUploadFileFileName(String a){this.uploadFileFileName=a;}
	public String upload(){//上传文件
		System.out.println(">> TableOperationAction:upload > uploadFileContentType="+this.getUploadFileContentType());
		System.out.println(">> TableOperationAction:upload > uploadFileFileName="+this.getUploadFileFileName());
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		if(this.getUploadFile()==null)
			return Manager.tips("上传了空文件！",
					display());
		List<? extends Base> content=null;
		List<Integer> errorIndex=new ArrayList<Integer>();
		try(FileInputStream in=new FileInputStream(this.getUploadFile());){
			content=SQLCollection.io.readExcel(clazz,in,errorIndex,this.getSearch().getRestraint());
		}
		catch(IOException e){
			return Manager.tips("文件错误！",
					e,display());
		}
		catch (EncryptedDocumentException e) {
			return Manager.tips("解码错误！",
					e,display());
		}
		catch (InvalidFormatException e) {
			return Manager.tips("格式错误！",
					e,display());
		} catch (InstantiationException | IllegalAccessException e) {
			return Manager.tips("初始化实例错误！",
					e,display());
		}
		if(content==null)
			return Manager.tips("文件读取失败！",
					display());
		if(content.isEmpty())
			return Manager.tips("文件为空！",
					display());
		for(int i=0;i<content.size();i++){
			try{
				Base b=content.get(i);
				if(b.exist())
					b.update();
				else
					b.create();
			}catch (IllegalArgumentException | IllegalAccessException | SQLException | InstantiationException e) {
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
			return Manager.tips(error.toString(),
					display());
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
		System.out.println(">> TableOperationAction:download > tableName="+this.getTableName());
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		this.setDownloadFileName(Base.getSQLTableName(clazz)+"模板.xlsx");//设置下载文件名称
		System.out.println(">> TableOperationAction:download > tableName="+this.getTableName());
		System.out.println(">> TableOperationAction:download > downloadFielName="+this.getDownloadFileName());
		downloadOutputStream=new ByteArrayOutputStream();
		try{
			SQLCollection.io.getModelExcel(clazz,downloadOutputStream);
			this.downloadOutputStream.flush();
		}catch(IOException e){
			downloadOutputStream=null;
			return Manager.tips("服务器开小差去了，暂时无法下载！",
					e,display());
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
