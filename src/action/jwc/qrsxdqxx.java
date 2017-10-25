package action.jwc;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;

import action.jwc.qrsxdqxx.SetOfRegionAndPractice.Pair;
import obj.Base;
import obj.ListableBase;
import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

/**
 * 导入免费师范生数据
 */
public class qrsxdqxx extends action.login.AnnualAction{
	private static final long serialVersionUID = 5998268336475528662L;
	
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
		public List<PracticeBase> getNullRegion(){
			return list.get(0).practiceBases;
		}
		public List<PracticeBase> get(String regionName){
			for(Pair p:this.list){
				Region t=p.region;
				if(t!=null && t.getName()!=null && t.getName().equals(regionName))
					return p.practiceBases;
			}
			return null;
		}
		public List<PracticeBase> get(Region r) {
			if(r==null) return this.getNullRegion();
			return this.get(r.getName());
		}
		public void put(Region r, PracticeBase l) {
			if(l==null) return;
			List<PracticeBase> tmp=this.get(r);
			Pair p=null;
			if(tmp==null){
				this.list.add(p=new Pair(r));
				tmp=p.practiceBases;
			}
			tmp.add(l);
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
	private String newRegionName;
	
	public void setCheckBox(boolean[] a){this.checkBox=a;}
	public boolean[] getCheckBox(){return this.checkBox;}
	public SetOfRegionAndPractice getRegionAndPracticeBase(){return this.regionAndPracticeBase;}
	public String getNewRegionName(){return this.newRegionName;}
	public void setNewRegionName(String a){this.newRegionName=a;}
	

	static public final String SessionListKey="qrsxdqxx_RegionAndPracticeBases"; 
	
	public qrsxdqxx() throws SQLException, NoSuchFieldException, SecurityException{
		super();
		System.out.println(">> qrsxdqxx:constructor > year="+this.getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		Object o=session.get(SessionListKey);
		this.regionAndPracticeBase=o==null?null:((SetOfRegionAndPractice)o);
		this.setupCheckBox();
	}
	
	
	@Override
	public String execute(){
		if(!executive || this.regionAndPracticeBase==null)
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		System.out.println(">> qrsxdqxx:execute > newRegionName= "+this.newRegionName);
		if(this.newRegionName==null || this.newRegionName.isEmpty()){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"请输入新大区名称！");
			System.out.println(">> qrsxdqxx:execute <NONE");
			return NONE;
		}
		System.out.println(">> qrsxdqxx:execute > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> qrsxdqxx:execute > ]");
		//qrsxdqxx:execute
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"请至少选择一个实习基地添加到新大区！");
			System.out.println(">> qrsxdqxx:execute <NONE");
			return NONE;
		}
		List<PracticeBase> nullRegionPracticeBases=this.regionAndPracticeBase.getNullRegion();
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
					newRegion.setYear(this.getYear());
					newRegion.setName(this.newRegionName);
					newRegion.setPracticeBase(pb.getName());
					try {
						newRegion.create();
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
						continue;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					continue;
				}
				if(sb.length()>0) sb.append(',');
				sb.append(pb.getName());
			}
		}
		session.put(token.ActionInterceptor.ErrorTipsName,
				sb.toString()+" 已经添加到新大区("+this.newRegionName+")！");
		session.remove(SessionListKey);
		return display();
	}
	

	public String delete() throws SQLException{
		if(!executive || this.regionAndPracticeBase==null)
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		System.out.println(">> qrsxdqxx:delete > newRegionName= "+this.newRegionName);
		if(this.newRegionName==null || this.newRegionName.isEmpty()){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"未选中大区！");
			System.out.println(">> qrsxdqxx:delete <NONE");
			return NONE;
		}
		System.out.println(">> qrsxdqxx:delete > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> qrsxdqxx:delete > ]");
		//qrsxdqxx:execute
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"请至少选择一个实习基地来移除！");
			System.out.println(">> qrsxdqxx:delete <NONE");
			return NONE;
		}
		List<PracticeBase> deletePracticeBases=this.regionAndPracticeBase.get(this.newRegionName);
		if(deletePracticeBases==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"选中了一个不存在的大区（"+this.newRegionName+"）！");
			System.out.println(">> qrsxdqxx:delete <NONE");
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
				Region newRegion=new Region();
				newRegion.setYear(this.getYear());
				newRegion.setName(this.newRegionName);
				newRegion.setPracticeBase(pb.getName());
				try {
					newRegion.delete();
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
					continue;
				}
				if(sb.length()>0) sb.append(',');
				sb.append(pb.getName());
			}
		}
		session.put(token.ActionInterceptor.ErrorTipsName,
				sb.toString()+" 已经从大区("+this.newRegionName+")移除！");
		session.remove(SessionListKey);
		return display();
	}
	
	@Override
	public String display(){
		this.newRegionName=null;
		System.out.println(">> qrsxdqxx:display > year="+this.getYear());
		Map<String, Object> session=ActionContext.getContext().getSession();
		this.regionAndPracticeBase=null;
		try {
			this.regionAndPracticeBase=SetOfRegionAndPractice.listRegionAndPracticeBase(this.getYear());
		} catch (SQLException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库开小差去了！");
		}
		System.out.println(">> qrsxdqxx:display > regionAndPracticeBase=[");
		for(SetOfRegionAndPractice.Pair t:this.regionAndPracticeBase.getList()){
			System.out.print((t.region==null?"null":t.region.getName())+":[");
			for(PracticeBase pb:t.practiceBases)
				System.out.print(pb.getName()+",");
			System.out.println("]");
		}
		System.out.println(">> qrsxdqxx:display > ]");
		if(this.regionAndPracticeBase==null)
			session.remove(SessionListKey);
		else
			session.put(SessionListKey,this.regionAndPracticeBase);
		this.setupCheckBox();
		System.out.println(">> qrsxdqxx:display <NONE");
		return NONE;
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
}
