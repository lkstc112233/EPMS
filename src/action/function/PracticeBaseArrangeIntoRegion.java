package action.function;

import java.sql.*;

import action.*;
import obj.annualTable.*;
import obj.annualTable.list.*;
import obj.staticObject.PracticeBase;

/**
 * 分配实习基地到大区
 */
public class PracticeBaseArrangeIntoRegion extends Action{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}

	private boolean[] checkBox;
	private List_Region_PracticeBaseRegion list;
	private String regionName;
	
	public void setCheckBox(boolean[] a){this.checkBox=a;}
	public boolean[] getCheckBox(){return this.checkBox;}
	public List_Region_PracticeBaseRegion getList(){return this.list;}
	public String getRegionName(){return this.regionName;}
	public void setRegionName(String a){this.regionName=a;}
	public PracticeBase getPracticeBase(){return new PracticeBase();}
	

	static public final String SessionListKey="RegionArrangement_lists"; 
	
	public PracticeBaseArrangeIntoRegion(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBaseRegion.class,SessionListKey);
		this.setupCheckBox();
	}

	private void setupCheckBox(){
		this.checkBox=null;
		if(this.list!=null){
			int len=0;
			for(Leaf<Region, PracticeBaseWithRegion> rp:this.list.getList())
				len=Math.max(len,rp.getSize());
			this.checkBox=new boolean[len];
		}
	}
	
	public String display(){
		this.regionName=null;
		System.out.println(">> RegionArrangement:display > year="+this.getAnnual().getYear());
		this.list=null;
		try {
			this.list=new List_Region_PracticeBaseRegion(this.getAnnual().getYear(),/*containsNullRegion*/true);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return this.returnWithTips(NONE,"数据库开小差去了！",e);
		}
		Manager.saveSession(SessionListKey,this.list);
		this.setupCheckBox();
		return NONE;
	}
	
	/**
	 * 用于创建实习大区
	 */
	@Override
	public String execute(){
		if(this.list==null)
			return display();
		System.out.println(">> RegionArrangement:execute > regionName= "+this.regionName);
		if(this.regionName==null || this.regionName.isEmpty())
			return this.returnWithTips(NONE,"请输入大区名称！");
		System.out.println(">> RegionArrangement:execute > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> RegionArrangement:execute > ]");
		//RegionArrangement:execute
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag)
			return this.returnWithTips(NONE,"请至少选择一个实习基地添加到大区！");
		java.util.List<PracticeBaseWithRegion> nullRegionPracticeBases=this.list.get((Region)null).getList();
		//	List<PracticeBase> tmp=new ArrayList<PracticeBase>();
		StringBuilder sb=new StringBuilder();
		StringBuilder error=new StringBuilder();
		for(int i=0;i<nullRegionPracticeBases.size();i++){
			if(checkBox[i]){
				//选中了
				PracticeBase pb=nullRegionPracticeBases.get(i).getPracticeBase();
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
					if(error.length()>0) error.append('\n');
					error.append(pb.getName()+"("+e.getMessage()+")");
					continue;
				}
				if(sb.length()>0) sb.append(',');
				sb.append(pb.getName());
			}
		}
		Manager.removeSession(SessionListKey);
		return this.jumpToMethodWithTips("display",
				(sb.length()>0?(sb.toString()+" 已经添加到大区("+this.regionName+")！"):"")+
				(error.length()>0?("\n\n错误信息：\n"+error.toString()):""));
	}
	
	/**
	 * 用于从大区移除基地
	 */
	public String delete(){
		if(this.list==null)
			return display();
		System.out.println(">> RegionArrangement:delete > regionName= "+this.regionName);
		if(this.regionName==null || this.regionName.isEmpty())
			return this.returnWithTips(NONE,"未选中大区！");
		System.out.println(">> RegionArrangement:delete > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> RegionArrangement:delete > ]");
		//RegionArrangement:execute
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag)
			return this.returnWithTips(NONE,"请至少选择一个实习基地来移除！");
		java.util.List<PracticeBaseWithRegion> deletePracticeBases=this.list.getByRegionName(this.regionName).getList();
		if(deletePracticeBases==null)
			return this.returnWithTips(NONE,"选中了一个不存在的大区("+this.regionName+")!");
		//	List<PracticeBase> tmp=new ArrayList<PracticeBase>();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<deletePracticeBases.size();i++){
			if(checkBox[i]){
				//选中了
				PracticeBase pb=deletePracticeBases.get(i).getPracticeBase();
				if(pb==null||pb.getName()==null)
					continue;
				//	tmp.add(pb);
				try{
					Region newRegion=new Region();
					newRegion.setYear(this.getAnnual().getYear());
					newRegion.setName(this.regionName);
					newRegion.setPracticeBase(pb.getName());
					newRegion.delete();
				}catch(SQLException|IllegalArgumentException e){
					e.printStackTrace();
					continue;
				}
				if(sb.length()>0) sb.append(',');
				sb.append(pb.getName());
			}
		}
		Manager.removeSession(SessionListKey);
		return this.jumpToMethodWithTips("display",sb.toString()+" 已经从大区("+this.regionName+")移除！");
	}
	
	
}
