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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import obj.*;

public abstract class TableOperationAction extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private String tableName;
		public String getTableName(){return this.tableName;}
		//public void setTableName(String tableName){this.setupTableName(tableName);}
	public final String SessionSearchKey;
	
	
	private Search2<? extends Base> search=null;//查询信息
	private int choose=-1;//操作项
	private Base chooseBase;
	private Base createNewBase;
	
	public Search2<? extends Base> getSearch(){return this.search;}
	public List<String> getTypes(){return Search.RestraintType.list();}
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
		this.SessionSearchKey="TableOperationAction_Search2";
		this.search=Manager.loadSession(Search2.class,SessionSearchKey);
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
		Map<String, Object> session=ActionContext.getContext().getSession();
		System.out.println(">> TableOperationAction:display > tableName="+this.getTableName());
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		try {
			if(this.search==null){
				this.search=new Search2(clazz);
				this.setupSearchRestraint();
			}
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"搜索结果实例初始化失败！\n("+e.getMessage()+")");
			return NONE;
		}
		if(this.search!=null)
			Manager.saveSession(SessionSearchKey,this.search);
		System.out.println(">> TableOperationAction:display <NONE");
		return NONE;
	}
	
	
	@Override
	public String execute(){//执行查询
		System.out.println(">> TableOperationAction:execute > tableName="+this.getTableName());
		Map<String, Object> session=ActionContext.getContext().getSession();
		if(this.search==null){
			System.out.println("搜索结果实例初始化失败！");
			return display();
		}
		//===
		try {
			this.search.execute();
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"搜索结果实例初始化失败！\n("+e.getMessage()+")");
			return NONE;
		} catch (SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库开小差去了！");
			return NONE;
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
		Map<String, Object> session=ActionContext.getContext().getSession();
		if(this.search==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"搜索结果实例初始化失败！");
			return display();
		}
		//===
		Base b=null;
		try{
			b=this.search.getResult().get(this.getChoose());
		}catch(IllegalArgumentException e){
			session.put(token.ActionInterceptor.ErrorTipsName,
					e.getMessage());
		}catch(IndexOutOfBoundsException e){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"条目选择错误！");
		}
		if(b==null || this.chooseBase==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"条目选择为空！");
			return NONE;
		}
		if(!this.search.getRestraint().fitBase(this.chooseBase)){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"不能修改为其他年份/部院系条目！");
			return NONE;
		}
		//b -> update to ->this.chooseBase
		try{
			this.chooseBase.update(b);
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"修改参数错误！("+e.getMessage()+")");
			return NONE;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"修改权限错误！("+e.getMessage()+")");
			return NONE;
		} catch (SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库错误！("+e.getMessage()+")");
			return NONE;
		}
		this.setChoose(-1);//will clear the chooseBase
		System.out.println(">> TableOperationAction:delete > 修改成功");
		this.execute();
		session.put(token.ActionInterceptor.ErrorTipsName,
				"修改成功！");
		return display();
	}
	/**
	 * 删除选中条（根据choose值）
	 */
	public String delete(){
		System.out.println(">> TableOperationAction:delete > tableName="+this.getTableName());
		Map<String, Object> session=ActionContext.getContext().getSession();
		if(this.search==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"搜索结果实例初始化失败！");
			return display();
		}
		//===
		Base b=null;
		try{
			b=this.search.getResult().get(this.choose);
		}catch(IllegalArgumentException e){
			session.put(token.ActionInterceptor.ErrorTipsName,
					e.getMessage());
		}catch(IndexOutOfBoundsException e){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"条目选择错误！");
		}
		if(b==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"条目选择为空！");
			return NONE;
		}
		if(!this.search.getRestraint().fitBase(b)){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"不能删除其他年份/部院系条目！");
			return NONE;
		}
		//delete b (in the search result)
		try {
			b.delete();
			this.search.getResult().remove(this.choose);
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"修改参数错误！("+e.getMessage()+")");
			return NONE;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"修改权限错误！("+e.getMessage()+")");
			return NONE;
		} catch (SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库错误！("+e.getMessage()+")");
			return NONE;
		}
		System.out.println(">> TableOperationAction:delete > 删除成功");
		this.execute();
		session.put(token.ActionInterceptor.ErrorTipsName,
				"删除成功！");
		return display();
	}
	/**
	 * 新建条（新建createBase）
	 */
	public String create(){
		System.out.println(">> TableOperationAction:create > tableName="+this.getTableName());

		Map<String, Object> session=ActionContext.getContext().getSession();
		if(this.search==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"搜索结果实例初始化失败！");
			return display();
		}
		if(this.createNewBase==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"条目选择为空！");
			return NONE;
		}
		try{
			if(this.createNewBase.checkKeyNull()){
				session.put(token.ActionInterceptor.ErrorTipsName,
						"新建条目内容不充分，请补全！");
				return NONE;
			}
		}catch(IllegalArgumentException | IllegalAccessException e){
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开小差去了，暂时无法创建！("+e.getMessage()+")");
			return NONE;
		}
		if(!this.search.getRestraint().fitBase(this.createNewBase)){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"不能新建其他年份/部院系条目！");
			return NONE;
		}
		//create this.getCreateNewBase()
		try {
			this.createNewBase.create();
		}catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开小差去了，暂时无法创建！("+e.getMessage()+")");
			return NONE;
		}catch(SQLException e){
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库发现问题，无法创建该条目！("+e.getMessage()+")");
			return NONE;
		}
		this.createNewBase.clear();
		session.put(token.ActionInterceptor.ErrorTipsName,
				"创建成功！");
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
		Map<String, Object> session=ActionContext.getContext().getSession();
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		if(this.getUploadFile()==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"上传了空文件！");
			System.out.println(">> TableOperationAction:upload > 上传了空文件！");
			System.out.println(">> TableOperationAction:upload <NONE");
			return display();
		}
		List<? extends Base> content=null;
		List<Integer> errorIndex=new ArrayList<Integer>();
		try(FileInputStream in=new FileInputStream(this.getUploadFile());){
			content=SQLCollection.io.readExcel(clazz,in,errorIndex,this.getSearch().getRestraint());
		}
		catch(IOException e){
			e.printStackTrace();
			System.out.println(">> TableOperationAction:upload > 文件错误！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"文件错误！");
			System.out.println(">> TableOperationAction:upload <NONE");
			return display();
		}
		catch (EncryptedDocumentException e) {
			e.printStackTrace();
			System.out.println(">> TableOperationAction:upload > 解码错误！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"解码错误！");
			System.out.println(">> TableOperationAction:upload <NONE");
			return display();
		}
		catch (InvalidFormatException e) {
			e.printStackTrace();
			System.out.println(">> TableOperationAction:upload > 格式错误！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"格式错误！");
			System.out.println(">> TableOperationAction:upload <NONE");
			return display();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			System.out.println(">> TableOperationAction:upload > 初始化实例错误！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"初始化实例错误！");
			System.out.println(">> TableOperationAction:upload <NONE");
			return display();
		}
		if(content==null){
			System.out.println(">> TableOperationAction:upload > 文件读取失败！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"文件读取失败！");
			System.out.println(">> TableOperationAction:upload <NONE");
			return display();
		}
		if(content.isEmpty()){
			System.out.println(">> TableOperationAction:upload > 文件为空！");
			session.put(token.ActionInterceptor.ErrorTipsName,
					"文件为空！");
			System.out.println(">> TableOperationAction:upload <NONE");
			return NONE;
		}
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
			System.out.println(">> TableOperationAction:upload > "+error);
			session.put(token.ActionInterceptor.ErrorTipsName,
					error);
			System.out.println(">> TableOperationAction:upload <SUCCESS");
			return display();
		}
		System.out.println(">> TableOperationAction:upload > 上传成功！");
		session.put(token.ActionInterceptor.ErrorTipsName,
				"上传成功！");
		System.out.println(">> TableOperationAction:upload <SUCCESS");
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
		Map<String, Object> session=ActionContext.getContext().getSession();
		Class<? extends Base> clazz=Base.getClassForName(this.getTableName());
		this.setDownloadFileName(Base.getSQLTableName(clazz)+"模板.xlsx");//设置下载文件名称
		System.out.println(">> TableOperationAction:download > tableName="+this.getTableName());
		System.out.println(">> TableOperationAction:download > downloadFielName="+this.getDownloadFileName());
		downloadOutputStream=new ByteArrayOutputStream();
		try{
			SQLCollection.io.getModelExcel(clazz,downloadOutputStream);
			this.downloadOutputStream.flush();
		}catch(IOException e){
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开小差去了，暂时无法下载！");
			downloadOutputStream=null;
			return display();
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
		return in;
	}
	


}
