package action;

import com.opensymphony.xwork2.ActionSupport;

import obj.Field;

public abstract class Action extends ActionSupport{
	private static final long serialVersionUID = 95833227412820750L;
	
	private String chainAction;
		public String getChainAction() {return this.chainAction;}
	//	public void setChainAction(String a) {this.chainAction=a;}
	
	public Action() {
		super();
	}

	@Override
	public abstract String execute();
	
	protected final String jumpToAction(String actionName) {
		if(Field.s2S(actionName)==null)
			actionName="menu";
		this.chainAction=actionName;
		return "jump";
	}
	protected final String jumpToMethod(String methodName) {
		String actionName=Manager.getActionName();
		String ss=actionName.substring(0,actionName.lastIndexOf('_')+1);
		return this.jumpToAction(ss+methodName);
	}
	protected final String jumpBack() {
		String actionName=Manager.getActionName();
		String action=null;
		System.out.print("Action: list=[");
		while(true) {
			action=Manager.removeFirstActionName();
			System.out.print(action+",");
			if(action==null) {
				action="menu";
				break;
			}
			if(actionName==null)
				break;
			if(action.contains("_")) {
				if(actionName.contains("_")){
					if(!action.substring(0,action.lastIndexOf("_")).equals(actionName.substring(0,actionName.lastIndexOf("_"))))
						break;
				}else
					break;
			}else
				if(!action.equals(actionName))
					break;
		}
		System.out.println("] action jump=:"+action);
		return this.jumpToAction(action);
	}
	
	protected final String returnWithTips(String result,String tips) {
		Manager.tips(tips);
		return result;
	}protected final String returnWithTips(String result,String tips,Throwable e) {
		Manager.tips(tips,e);
		return result;
	}
	protected final String jumpToActionWithTips(String actionName,String tips) {
		Manager.tips(tips);
		return this.jumpToAction(actionName);
	}protected final String jumpToActionWithTips(String actionName,String tips,Throwable e) {
		Manager.tips(tips,e);
		return this.jumpToAction(actionName);
	}
	protected final String jumpToMethodWithTips(String methodName,String tips) {
		Manager.tips(tips);
		return this.jumpToMethod(methodName);
	}protected final String jumpToMethodWithTips(String methodName,String tips,Throwable e) {
		Manager.tips(tips,e);
		return this.jumpToMethod(methodName);
	}
	protected final String jumpBackWithTips(String tips) {
		Manager.tips(tips);
		return this.jumpBack();
	}protected final String jumpBackWithTips(String tips,Throwable e) {
		Manager.tips(tips,e);
		return this.jumpBack();
	}

}
