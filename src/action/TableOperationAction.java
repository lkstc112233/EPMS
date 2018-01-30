package action;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import obj.*;
import obj.restraint.BaseRestraint;

public abstract class TableOperationAction extends Action{
	private static final long serialVersionUID = 5998268336475528662L;

	static public final String SessionSearchKey="TableOperationAction_Search";
	static public final String SessionFieldsDisplayKey="TableOperationAction_FieldsDisplay";
	static public final String SessionChooseKey="TableOperationAction_Choose";
	
	
	protected Search search=null;//查询信息
	private boolean[][] fieldsDisplay;//显示的Field，与search同等的设置和释放资源
	private Integer[] choose;//操作项
	private Integer jumpX;//操作项
		public Integer getJumpX() {return this.jumpX;}
	private Base operateBase;//当为update|delete时，此处存放旧数据
	
	public Search getSearch(){return this.search;}
	public Integer[] getChoose(){return this.choose;}
	public boolean[][] getFieldsDisplay(){return this.fieldsDisplay;}
	public Base getOperateBase(){return this.operateBase;}
	public Field[] getAllSelectFields(){
		int len=0;
		for(JoinParam.Part part:this.getSearch().getParam().getList())
			len+=Field.getFields(part.getClazz()).length;
		Field[] res=new Field[len];
		int i=0;
		for(JoinParam.Part part:this.getSearch().getParam().getList())
			for(Field f:Field.getFields(part.getClazz()))
				res[i++]=f;
		return res;
	}


	public TableOperationAction(){
		super();
		this.setup();
	}
	
	protected void setup(){
		if(this.search==null)
			this.search=Manager.loadSession(Search.class,SessionSearchKey);
		else
			Manager.saveSession(SessionSearchKey,this.search);
		if(this.fieldsDisplay==null)
			this.fieldsDisplay=Manager.loadSession(boolean[][].class,SessionFieldsDisplayKey);
		else
			Manager.saveSession(SessionFieldsDisplayKey,this.fieldsDisplay);
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
			if(this.search.getBaseRestraint()!=null)
				this.search.getBaseRestraint().checkBase(this.operateBase,/*setIfFalse*/true);
			Manager.saveSession(SessionChooseKey,this.choose);
		}else{
			this.operateBase=null;
			this.choose=new Integer[]{-1,-1};
			Manager.removeSession(SessionChooseKey);
		}
	}

	protected abstract Search createSearch() throws Exception;
	protected Field[] refuseDisplayField() {return null;}

	public String display(){
		if(this.search==null) {
			try{
				this.search=this.createSearch();
			}catch(Exception e){
				return this.returnWithTips(NONE,"搜索结果实例初始化失败！",e);
			}
			this.fieldsDisplay=new boolean[this.search.getParam().size()][];
			for(int i=0;i<this.fieldsDisplay.length;i++){
				JoinParam.Part part=this.search.getParam().getList().get(i);
				this.fieldsDisplay[i]=new boolean[Field.getFields(part.getClazz()).length];
				int j=0;
				for(Field f2:Field.getFields(part.getClazz())){
					this.fieldsDisplay[i][j]=true;
					Field[] tmp=this.refuseDisplayField();
					if(tmp!=null) for(Field f:tmp) {
						if(f!=null && f2.getClazz().equals(f.getClazz()) && f2.equals(f)){
							this.fieldsDisplay[i][j]=false;
							break;
						}
					}
					j++;
				}
			}
		}
		this.setup();
		if(choose[1]!=null && choose[1]>=0)
			jumpX=choose[1];
		return NONE;
	}
	
	
	@Override
	public String execute(){//执行查询
		if(this.search==null)
			this.display();
		if(this.search==null)
			return this.jumpToMethodWithTips("display","搜索结果实例初始化失败！");
		System.out.println(">> TableOperationAction:execute > start");
		try {
			this.search.execute();
		} catch (IllegalAccessException | InstantiationException e) {
			return this.returnWithTips(NONE,"搜索结果生成失败！",e);
		} catch (SQLException e) {
			return this.returnWithTips(NONE,"搜索操作失败，可能是数据库开小差去了！",e);
		}
		Manager.removeSession(SessionChooseKey);
		jumpX=this.choose[1];
		this.choose=null;
		System.out.println(">> TableOperationAction:execute > resultSet count="+this.search.getResult().size());
		return this.jumpToMethod("display");
	}
	
	

	/**
	 * 更新选中条（根据choose值）
	 */
	public String update(){
		if(this.search==null)
			return this.jumpToMethodWithTips("display","搜索结果实例初始化失败！");
		System.out.println(">> TableOperationAction:update > start");
		if(this.operateBase==null)
			return this.returnWithTips(NONE,"操作选择错误!");
		Base newBase=null;
		jumpX=this.choose[1];
		try{
			newBase=this.search.getResult().get(this.choose[1])[this.choose[0]];
		}catch(Exception e){
		}
		if(newBase==null)
			return this.returnWithTips(NONE,"选择条目错误！");
		if(!this.search.getBaseRestraint().checkBase(newBase,/*setIfFalse*/false))
			return this.returnWithTips(NONE,"不能修改为其他[年份/部院系]条目！");
		try{
			this.operateBase.update(newBase);
		}catch (IllegalArgumentException e) {
			return this.returnWithTips(NONE,"修改时参数错误！",e);
		} catch (SQLException e) {
			return this.returnWithTips(NONE,"修改时遇到数据库错误！",e);
		}
		return this.jumpToMethodWithTips("execute","修改成功！");
	}
	/**
	 * 删除选中条（根据choose值）
	 */
	public String delete(){
		if(this.search==null)
			return this.jumpToMethodWithTips("display","搜索结果实例初始化失败！");
		System.out.println(">> TableOperationAction:delete > start");
		this.operateBase=null;
		try{
			this.operateBase=this.search.getResult().get(this.choose[1])[this.choose[0]];
		}catch(Exception e){
		}
		if(this.operateBase==null)
			return this.returnWithTips(NONE,"操作选择错误!");
		if(!this.search.getBaseRestraint().checkBase(this.operateBase,/*setIfFalse*/false))
			return this.returnWithTips(NONE,"不能删除其他[年份/部院系]条目！");
		try{
			this.operateBase.delete();
		}catch (IllegalArgumentException e) {
			return this.returnWithTips(NONE,"删除时参数错误！",e);
		} catch (SQLException e) {
			return this.returnWithTips(NONE,"删除时遇到数据库错误！",e);
		}
		return this.jumpToMethodWithTips("execute","删除成功！");
	}
	/**
	 * 新建条（新建createBase）
	 */
	public String create(){
		if(this.search==null)
			return this.jumpToMethodWithTips("display","搜索结果实例初始化失败！");
		System.out.println(">> TableOperationAction:update > start");
		if(this.operateBase==null)
			return this.returnWithTips(NONE,"操作选择错误！");
		if(!this.search.getBaseRestraint().checkBase(this.operateBase,/*setIfFalse*/false))
			return this.returnWithTips(NONE,"不能新建其他[年份/部院系]条目！");
		try{
			this.operateBase.create();
		} catch (IllegalArgumentException e) {
			return this.returnWithTips(NONE,"创建时参数错误！",e);
		} catch (IllegalAccessException e) {
			return this.returnWithTips(NONE,"创建时参数权限错误！",e);
		} catch (SQLException e) {
			return this.returnWithTips(NONE,"创建时遇到数据库错误！",e);
		}
		return this.jumpToMethodWithTips("execute","创建成功！");
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
	protected List<? extends Base> uploadByIO(SQLIO io,Class<? extends Base> clazz,
			InputStream stream,List<String> error,BaseRestraint restraint) throws IOException, EncryptedDocumentException, InvalidFormatException, InstantiationException, IllegalAccessException{
		int i=0;
		for(JoinParam.Part part:this.search.getParam().getList()) {
			if(part.getClazz().equals(clazz)) break;
			i++;
		}
		List<Field> tmp=new ArrayList<Field>();
		int j=0;for(Field f:Field.getFields(clazz)) {
			if(this.fieldsDisplay[i][j++])
				tmp.add(f);
		}
		return io.readExcel(clazz,tmp,stream,error,restraint);
	}
	public String upload(){//上传文件
		System.out.println(">> TableOperationAction:upload > uploadFileContentType="+this.getUploadFileContentType());
		System.out.println(">> TableOperationAction:upload > uploadFileFileName="+this.getUploadFileFileName());
		Class<? extends Base> clazz=Base.getClassForName(this.fileTableName);
		if(clazz==null)
			return this.jumpToMethodWithTips("display","选择了错误的表名称！");
		if(this.getUploadFile()==null)
			return this.jumpToMethodWithTips("display","上传了空文件！");
		List<? extends Base> content=null;
		List<String> error=new ArrayList<String>();
		try(FileInputStream in=new FileInputStream(this.getUploadFile());){
			content=this.uploadByIO(Base.io(),clazz,in,error,this.getSearch().getBaseRestraint());
		}
		catch(IOException e){
			return this.jumpToMethodWithTips("display","文件错误！");
		}
		catch (EncryptedDocumentException e) {
			return this.jumpToMethodWithTips("display","解码错误！");
		}
		catch (InvalidFormatException e) {
			return this.jumpToMethodWithTips("display","格式错误！");
		} catch (InstantiationException | IllegalAccessException e) {
			return this.jumpToMethodWithTips("display","始化实例错误！");
		}
		if(content==null)
			return this.jumpToMethodWithTips("display","文件读取失败！");
		if(content.isEmpty())
			return this.jumpToMethodWithTips("display","文件为空！");
		for(int i=0;i<content.size();i++) try{
			Base b=content.get(i);
			String msg=this.updateBase(b);
			error.set(i,msg);
		}catch (IllegalArgumentException | IllegalAccessException | SQLException | InstantiationException e) {
			e.printStackTrace();
			error.set(i,"出错!("+e.getMessage()+")");
		}
		if(!error.isEmpty()){
			StringBuilder errorMsg=new StringBuilder("上传情况：\n\n");
			int cnt=0;
			for(int i=0;i<error.size();i++)
				if(error.get(i).contains("成功"))
					cnt++;
				else
					errorMsg.append("Row["+i+"]:"+error.get(i)+"\n");
			errorMsg.append("成功"+cnt+"条!");
			return this.jumpToMethodWithTips("display",errorMsg.toString());
		}
		Manager.tips("上传成功！");
		return this.jumpToMethod("display");
	}
	
	public String updateBase(Base b) throws InstantiationException, IllegalArgumentException, IllegalAccessException, SQLException {
		if(b==null) return "未找到!";
		if(b.exist()) {
			b.update();
			return "更新成功!";
		}else {
			b.create();
			return "添加成功!";
		}
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
	private OutputStream downloadOutputStream=null;
	protected void downloadByIO(SQLIO io,Class<? extends Base> clazz,OutputStream stream) throws IOException{
		int i=0;
		for(JoinParam.Part part:this.search.getParam().getList()) {
			if(part.getClazz().equals(clazz)) break;
			i++;
		}
		List<Field> tmp=new ArrayList<Field>();
		int j=0;for(Field f:Field.getFields(clazz)) {
			if(this.fieldsDisplay[i][j++])
				tmp.add(f);
		}
		io.getModelExcel(clazz,tmp,stream);
	}
	public String download(){//下载模板
		System.out.println(">> TableOperationAction:download > tableName="+this.fileTableName);
		Class<? extends Base> clazz=Base.getClassForName(this.fileTableName);
		if(clazz==null)
			return this.jumpToMethodWithTips("display","选择了错误的表名称");
		this.setDownloadFileName(Base.getSQLTableName(clazz)+"模板.xlsx");//设置下载文件名称
		System.out.println(">> TableOperationAction:download > tableName="+this.fileTableName);
		System.out.println(">> TableOperationAction:download > downloadFielName="+this.getDownloadFileName());
		downloadOutputStream=new ByteArrayOutputStream();
		try{
			this.downloadByIO(Base.io(),clazz,downloadOutputStream);
			this.downloadOutputStream.flush();
		}catch(IOException e){
			downloadOutputStream=null;
			return this.jumpToMethodWithTips("display","服务器开小差去了，暂时无法下载!",e);
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
	/*	try {
			ServletActionContext.getResponse().setHeader("Content-Disposition","attachment;downloadFileName="+java.net.URLEncoder.encode(this.downloadFileName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}//*/
		return in;
	}
	


}
