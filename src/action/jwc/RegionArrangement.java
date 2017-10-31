package action.jwc;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import action.jwc.RegionArrangement.SetOfRegionAndPractice.Pair;
import obj.Base;
import obj.ListableBase;
import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

/**
 * 导入免费师范生数据
 */
public class RegionArrangement extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	static public class SetOfRegionAndPractice{
		static public class Pair{
			private Region region;
			private List<PracticeBase> practiceBases=new ArrayList<PracticeBase>();
			public Region getRegion(){return this.region;}
			public List<PracticeBase> getPracticeBases(){return this.practiceBases;}
			public Pair(Region r){this.region=r;}
		}
		private List<Pair> list=new ArrayList<Pair>();
			public List<Pair> getList(){return list;}
		
		public SetOfRegionAndPractice(){
			list.add(new Pair(null));
		}
		public Pair getNullRegionPair(){
			return list.get(0);
		}
		public Pair get(String regionName){
			for(Pair p:this.list){
				Region t=p.region;
				if(t!=null && t.getName()!=null && t.getName().equals(regionName))
					return p;
			}
			return null;
		}
		public Pair get(Region r) {
			if(r==null) return this.getNullRegionPair();
			return this.get(r.getName());
		}
		public void put(Region r, PracticeBase pb) {
			if(pb==null) return;
			Pair tmp=this.get(r);
			if(tmp==null){
				this.list.add(tmp=new Pair(r));
			}
			tmp.practiceBases.add(pb);
		}
		static private SetOfRegionAndPractice listRegionAndPracticeBase(int year) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InstantiationException, SQLException{
			SetOfRegionAndPractice res=new SetOfRegionAndPractice();
			ListableBase.JoinParam param=new ListableBase.JoinParam(PracticeBase.class);
			param.append("name",ListableBase.JoinType.LeftJoin,Region.class,"practiceBase",
					new String[]{"year"},
					new Object[]{Integer.valueOf(year)});
			List<Base[]> tmp=ListableBase.list(param,null,null,null);
			for(Base[] bs:tmp){
				PracticeBase pb=null;
				Region r=null;
				if(bs!=null && bs.length>=2){
					if(bs[0]!=null && bs[0] instanceof PracticeBase){
						pb=(PracticeBase)bs[0];
						if(bs[1]==null || bs[1] instanceof Region){
							if(bs[1]!=null) r=(Region)bs[1];
							res.put(r,pb);
						}
					}
				}
			}
			return res;
		}
	}

	private boolean[] checkBox;
	private SetOfRegionAndPractice regionAndPracticeBase;
	private String regionName;
	
	public void setCheckBox(boolean[] a){this.checkBox=a;}
	public boolean[] getCheckBox(){return this.checkBox;}
	public SetOfRegionAndPractice getRegionAndPracticeBase(){return this.regionAndPracticeBase;}
	public String getRegionName(){return this.regionName;}
	public void setRegionName(String a){this.regionName=a;}
	

	static public final String SessionListKey="RegionArrangement_RegionAndPracticeBases"; 
	
	public RegionArrangement() throws SQLException, NoSuchFieldException, SecurityException{
		super();
		System.out.println(">> RegionArrangement:constructor > year="+this.getAnnual().getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Object o=session.get(SessionListKey);
		this.regionAndPracticeBase=o==null?null:((SetOfRegionAndPractice)o);
		this.setupCheckBox();
	}

	private void setupCheckBox(){
		this.checkBox=null;
		if(this.regionAndPracticeBase!=null){
			int len=0;
			for(Pair p:this.regionAndPracticeBase.getList())
				len=Math.max(len,p.getPracticeBases().size());
			this.checkBox=new boolean[len];
		}
	}
	
	/**
	 * 用于创建实习大区
	 */
	@Override
	public String execute(){
		if(this.regionAndPracticeBase==null)
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		System.out.println(">> RegionArrangement:execute > regionName= "+this.regionName);
		if(this.regionName==null || this.regionName.isEmpty()){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"请输入大区名称！");
			System.out.println(">> RegionArrangement:execute <NONE");
			return NONE;
		}
		System.out.println(">> RegionArrangement:execute > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> RegionArrangement:execute > ]");
		//RegionArrangement:execute
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"请至少选择一个实习基地添加到大区！");
			System.out.println(">> RegionArrangement:execute <NONE");
			return NONE;
		}
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
		session.put(token.ActionInterceptor.ErrorTipsName,
				sb.toString()+" 已经添加到大区("+this.regionName+")！");
		session.remove(SessionListKey);
		return display();
	}
	
	/**
	 * 用于从大区移除基地
	 */
	public String delete(){
		if(this.regionAndPracticeBase==null)
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		System.out.println(">> RegionArrangement:delete > regionName= "+this.regionName);
		if(this.regionName==null || this.regionName.isEmpty()){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"未选中大区！");
			System.out.println(">> RegionArrangement:delete <NONE");
			return NONE;
		}
		System.out.println(">> RegionArrangement:delete > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> RegionArrangement:delete > ]");
		//RegionArrangement:execute
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"请至少选择一个实习基地来移除！");
			System.out.println(">> RegionArrangement:delete <NONE");
			return NONE;
		}
		List<PracticeBase> deletePracticeBases=this.regionAndPracticeBase.get(this.regionName).getPracticeBases();
		if(deletePracticeBases==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"选中了一个不存在的大区（"+this.regionName+"）！");
			System.out.println(">> RegionArrangement:delete <NONE");
			return NONE;
		}
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
		session.put(token.ActionInterceptor.ErrorTipsName,
				sb.toString()+" 已经从大区("+this.regionName+")移除！");
		session.remove(SessionListKey);
		return display();
	}
	
	/**
	 * 用于显示
	 */
	public String display(){
		this.regionName=null;
		System.out.println(">> RegionArrangement:display > year="+this.getAnnual().getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		this.regionAndPracticeBase=null;
		try {
			this.regionAndPracticeBase=SetOfRegionAndPractice.listRegionAndPracticeBase(this.getAnnual().getYear());
		} catch (SQLException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库开小差去了！");
		}
		System.out.println(">> RegionArrangement:display > regionAndPracticeBase=[");
		for(SetOfRegionAndPractice.Pair t:this.regionAndPracticeBase.getList()){
			System.out.print((t.region==null?"null":t.region.getName())+":[");
			for(PracticeBase pb:t.practiceBases)
				System.out.print(pb.getName()+",");
			System.out.println("]");
		}
		System.out.println(">> RegionArrangement:display > ]");
		if(this.regionAndPracticeBase==null)
			session.remove(SessionListKey);
		else
			session.put(SessionListKey,this.regionAndPracticeBase);
		this.setupCheckBox();
		System.out.println(">> RegionArrangement:display <NONE");
		return NONE;
	}
	
	
}
