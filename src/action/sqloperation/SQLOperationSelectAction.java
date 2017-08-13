package action.sqloperation;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import obj.*;


public class SQLOperationSelectAction extends ActionSupport{
	private static final long serialVersionUID = -2075656836784414352L;
	static public final String SESSION_List="SQLOperationSelectAction_List";
	
	private String table=null;
	private SQLCollection<?> collection;
	@SuppressWarnings("rawtypes")
	private List listBase_SQLOperationSelectAction;
	private List<String[]> list_SQLOperationSelectAction;
	private String[] labels_SQLOperationSelectAction;

	public String getTable() {return table;}
	public void setTable(String table) {this.table=table;}
	public List<String[]> getList_SQLOperationSelectAction() {return list_SQLOperationSelectAction;}
	public void setList_SQLOperationSelectAction(List<String[]> list) {this.list_SQLOperationSelectAction = list;}
	public String[] getLabels_SQLOperationSelectAction() {return labels_SQLOperationSelectAction;}
	public void setLabels_SQLOperationSelectAction(String[] labels) {this.labels_SQLOperationSelectAction = labels;}
	public SQLCollection<?> getCollection() {return collection;}
	public void setCollection(SQLCollection<?> collection) {this.collection = collection;}
	public List<?> getListBase_SQLOperationSelectAction() {return listBase_SQLOperationSelectAction;}
	public void setListBase_SQLOperationSelectAction(List<?> listObject) {this.listBase_SQLOperationSelectAction= listObject;}

	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String execute(){
		System.out.println(">> SQLOperationSelectAction:execute > table="+table);
		if(table==null)
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		Class<? extends Base> clazz=Base.getClassForName(table);
		if(clazz==null){
			System.out.println(">> SQLOperationSelectAction:display > 数据表名称有误");
			session.put(token.ActionInterceptor.ErrorTipsName,"请选择正确的表名称！");
			return display();
		}
		//已经选了table
		System.out.println(">> SQLOperationSelectAction:display > 列举所有条目");
		collection=new SQLCollection(clazz);
		try {
			listBase_SQLOperationSelectAction=collection.selectAll(null,null);
			this.list_SQLOperationSelectAction=SQLCollection.getOutAll(listBase_SQLOperationSelectAction,this.labels_SQLOperationSelectAction);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(">> SQLOperationSelectAction:display > 条目检索出错 Exception("+e.getMessage()+")");
			session.put(token.ActionInterceptor.ErrorTipsName,"服务器没搜索到条目！");
			return display();
		}
		System.out.println(">> SQLOperationSelectAction:display > 检索到条目数:"+listBase_SQLOperationSelectAction.size());
		session.put(SESSION_List,listBase_SQLOperationSelectAction);
		System.out.println(">> SQLOperationSelectAction:display <SELECTED");
		return SUCCESS;
	}
	public String display(){
		System.out.println(">> SQLOperationSelectAction:display >");
		System.out.println(">> SQLOperationSelectAction:display <NONE");
		return NONE;
	}

	
	
}
