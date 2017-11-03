package action.jwc;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.Base;
import obj.annualTable.*;
import obj.staticObject.PracticeBase;

/**
 * 导入免费师范生数据
 */
public class RegionArrangement extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}

	private boolean[] checkBox;
	private ListOfRegionAndPracticeBases regionAndPracticeBase;
	private String regionName;
	
	public void setCheckBox(boolean[] a){this.checkBox=a;}
	public boolean[] getCheckBox(){return this.checkBox;}
	public ListOfRegionAndPracticeBases getRegionAndPracticeBase(){return this.regionAndPracticeBase;}
	public String getRegionName(){return this.regionName;}
	public void setRegionName(String a){this.regionName=a;}
	

	private List<String> practiceBaseAllFieldsNameString=Base.getAllFieldsNameString(PracticeBase.class);
		public List<String> getPracticeBaseAllFieldsNameString(){return this.practiceBaseAllFieldsNameString;}
	private List<String> practiceBaseAllFieldsDescriptionString=Base.getAllFieldsDescriptionString(PracticeBase.class);
		public List<String> getPracticeBaseAllFieldsDescriptionString(){return this.practiceBaseAllFieldsDescriptionString;}
	private List<Boolean> practiceBaseAllFieldsKeyBoolean=Base.getAllFieldsKeyBoolean(PracticeBase.class);
		public List<Boolean> getPracticeBaseAllFieldsKeyBoolean(){return this.practiceBaseAllFieldsKeyBoolean;}
	/*private int choose;
		public int getChoose(){return this.choose;}
		public void setChoose(String s){try{this.choose=Integer.parseInt(s);}catch(NumberFormatException e){e.printStackTrace();}}
		*/
	

	static public final String SessionListKey="RegionArrangement_RegionAndPracticeBases"; 
	
	public RegionArrangement() throws SQLException, NoSuchFieldException, SecurityException{
		super();
		System.out.println(">> RegionArrangement:constructor > year="+this.getAnnual().getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Object o=session.get(SessionListKey);
		this.regionAndPracticeBase=o==null?null:((ListOfRegionAndPracticeBases)o);
		this.setupCheckBox();
	}

	private void setupCheckBox(){
		this.checkBox=null;
		if(this.regionAndPracticeBase!=null){
			int len=0;
			for(ListOfRegionAndPracticeBases.Pair p:this.regionAndPracticeBase.getList())
				len=Math.max(len,p.getPracticeBases().size());
			this.checkBox=new boolean[len];
		}
	}

	
	/**
	 * 用于显示
	 */
	public String display(){
		this.regionName=null;
		System.out.println(">> RegionArrangement:display > year="+this.getAnnual().getYear());
		this.regionAndPracticeBase=null;
		try {
			this.regionAndPracticeBase=new ListOfRegionAndPracticeBases(this.getAnnual().getYear(),/*containsNullRegion*/true);
		} catch (SQLException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			return Manager.tips("数据库开小差去了！",
					e,NONE);
		}
		System.out.println(">> RegionArrangement:display > regionAndPracticeBase=[");
		for(ListOfRegionAndPracticeBases.Pair t:this.regionAndPracticeBase.getList()){
			System.out.print((t.getRegion()==null?"null":t.getRegion().getName())+":[");
			for(PracticeBase pb:t.getPracticeBases())
				System.out.print(pb.getName()+",");
			System.out.println("]");
		}
		System.out.println(">> RegionArrangement:display > ]");
		Manager.saveSession(SessionListKey,this.regionAndPracticeBase);
		this.setupCheckBox();
		System.out.println(">> RegionArrangement:display <NONE");
		return NONE;
	}
	
	/**
	 * 用于创建实习大区
	 */
	@Override
	public String execute(){
		if(this.regionAndPracticeBase==null)
			return display();
		System.out.println(">> RegionArrangement:execute > regionName= "+this.regionName);
		if(this.regionName==null || this.regionName.isEmpty())
			return Manager.tips("请输入大区名称！",
					NONE);
		System.out.println(">> RegionArrangement:execute > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> RegionArrangement:execute > ]");
		//RegionArrangement:execute
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag)
			return Manager.tips("请至少选择一个实习基地添加到大区！",
					NONE);
		List<PracticeBase> nullRegionPracticeBases=this.regionAndPracticeBase.get((Region)null).getPracticeBases();
		//	List<PracticeBase> tmp=new ArrayList<PracticeBase>();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<nullRegionPracticeBases.size();i++){
			if(checkBox[i]){
				//选中了
				PracticeBase pb=nullRegionPracticeBases.get(i);
				if(pb==null||pb.getName()==null)
					continue;
				//	tmp.add(pb);
				try{
					Region newRegion=new Region();
					newRegion.setYear(this.getAnnual().getYear());
					newRegion.setName(this.regionName);
					newRegion.setPracticeBase(pb.getName());
					newRegion.create();
				}catch(SQLException|IllegalArgumentException|IllegalAccessException e){
					e.printStackTrace();
					continue;
				}
				if(sb.length()>0) sb.append(',');
				sb.append(pb.getName());
			}
		}
		Manager.tips(sb.toString()+" 已经添加到大区("+this.regionName+")！");
		Manager.removeSession(SessionListKey);
		return display();
	}
	
	/**
	 * 用于从大区移除基地
	 */
	public String delete(){
		if(this.regionAndPracticeBase==null)
			return display();
		System.out.println(">> RegionArrangement:delete > regionName= "+this.regionName);
		if(this.regionName==null || this.regionName.isEmpty())
			return Manager.tips("未选中大区！",
					NONE);
		System.out.println(">> RegionArrangement:delete > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> RegionArrangement:delete > ]");
		//RegionArrangement:execute
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag)
			return Manager.tips("请至少选择一个实习基地来移除！",
					NONE);
		List<PracticeBase> deletePracticeBases=this.regionAndPracticeBase.get(this.regionName).getPracticeBases();
		if(deletePracticeBases==null)
			return Manager.tips("选中了一个不存在的大区（"+this.regionName+"）！",
					NONE);
		//	List<PracticeBase> tmp=new ArrayList<PracticeBase>();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<deletePracticeBases.size();i++){
			if(checkBox[i]){
				//选中了
				PracticeBase pb=deletePracticeBases.get(i);
				if(pb==null||pb.getName()==null)
					continue;
				//	tmp.add(pb);
				try{
					Region newRegion=new Region();
					newRegion.setYear(this.getAnnual().getYear());
					newRegion.setName(this.regionName);
					newRegion.setPracticeBase(pb.getName());
					newRegion.delete();
				}catch(SQLException|IllegalArgumentException|IllegalAccessException e){
					e.printStackTrace();
					continue;
				}
				if(sb.length()>0) sb.append(',');
				sb.append(pb.getName());
			}
		}
		Manager.tips(sb.toString()+" 已经从大区("+this.regionName+")移除！");
		Manager.removeSession(SessionListKey);
		return display();
	}
	
	
}
