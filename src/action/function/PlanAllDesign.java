package action.function;

import java.sql.SQLException;
import java.util.*;

import action.Action;
import action.Manager;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.list.Leaf;
import obj.annualTable.list.List_Region_PracticeBaseRegion;
import obj.annualTable.list.PracticeBaseWithRegion;
import obj.staticObject.PracticeBase;
import obj.staticSource.Major;

/**
 * 全面确定派遣计划
 */
public class PlanAllDesign extends Action{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private List<Major> majors;
	private int[] majorsCounts;
	private int[] majorsNHxCounts;
	private int[][] majorsRegionsHxCounts;
	private Boolean[][] majorsRegionsCountsIsError;
	private int lastNHxRegionIndex=-1;
	private List_Region_PracticeBaseRegion list;
	private int[][][] numbers;
	static public final String SessionMajorsKey="PlanAllDesign_Majors";
	static public final String SessionListKey="PlanAllDesign_List";

	
	public List_Region_PracticeBaseRegion getList(){return this.list;}
	public int[][][] getNumbers(){
		if(this.majors==null || this.list==null)
			return this.numbers=null;
		if(this.numbers!=null) return this.numbers;
		this.numbers=new int[this.majors.size()]
				[this.list.getList().size()][];
		for(int i=0;i<this.majors.size();i++)
			for(int j=0;j<this.list.getList().size();j++)
				this.numbers[i][j]=new int[this.list.getList().get(j).getList().size()];
		Map<String,Integer> majorsMap=new HashMap<String,Integer>();
		for(int i=0;i<this.majors.size();i++)
			majorsMap.put(this.majors.get(i).getName(),i);
		List<Plan> plans;
		try {
			plans=Base.list(Plan.class,new Restraint(
					Field.getField(Plan.class,"year"),
					this.getAnnual().getYear()));
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			e.printStackTrace();
			return this.numbers=null;
		}
		for(Plan p:plans){
			int[] index=this.list.indexOf(p.getPracticeBase());
			if(index!=null && index.length>=2)
				this.numbers[majorsMap.get(p.getMajor())][index[0]][index[1]]=
				p.getNumber();
		}
		return this.numbers;
	}
	public List<Major> getMajors(){
		if(this.majors==null) try {
			return this.majors=Base.list(Major.class);
		}catch (SQLException | IllegalArgumentException | InstantiationException e){
			e.printStackTrace();
			this.majors=null;
		}return this.majors;
	}
	public int[] getMajorsCounts() {
		if(this.majorsCounts==null) try{
			this.majorsCounts=new int[this.getMajors().size()];
			int i=0;for(Major m:this.getMajors())
				this.majorsCounts[i++]=Base.list(Student.class,new Restraint(
						Field.getFields(Student.class,"year","major"),
						new Object[]{this.getAnnual().getYear(),m.getName()}))
						.size();
			return this.majorsCounts;
		}catch (SQLException | IllegalArgumentException | InstantiationException | NullPointerException e){
			e.printStackTrace();
			this.majorsCounts=null;
		}return this.majorsCounts;
	}
	public int[] getMajorsNHxCounts() {
		if(this.majorsNHxCounts==null) try{
			this.majorsNHxCounts=new int[this.getMajors().size()];
			int i=0;for(Major m:this.getMajors())
				this.majorsNHxCounts[i++]=Base.list(Student.class,new Restraint(
						Field.getFields(Student.class,"year","major","hxyx"),
						new Object[]{this.getAnnual().getYear(),m.getName(),false}))
						.size();
			return this.majorsNHxCounts;
		}catch (SQLException | IllegalArgumentException | InstantiationException | NullPointerException e){
			e.printStackTrace();
			this.majorsNHxCounts=null;
		}return this.majorsNHxCounts;
	}
	public int[][] getMajorsRegionsHxCounts(){
		if(this.majorsRegionsHxCounts==null) try{
			this.majorsRegionsHxCounts=new int[this.getMajors().size()][];
			int i=-1;for(Major m:this.getMajors()) {
				this.majorsRegionsHxCounts[++i]=new int[this.getList().getList().size()];
				for(int j=0;j<this.majorsRegionsHxCounts[i].length;j++) {
					PracticeBase pb=this.getList().getList().get(j).getList().get(0).getPracticeBase();
					this.majorsRegionsHxCounts[i][j]=Base.list(Student.class,new Restraint(
						Field.getFields(Student.class,"year","major","hxyx","province"),
						new Object[]{this.getAnnual().getYear(),m.getName(),true,pb.getProvince()}))
						.size();
				}
			}
			return this.majorsRegionsHxCounts;
		}catch (SQLException | IllegalArgumentException | InstantiationException | NullPointerException | IndexOutOfBoundsException e){
			e.printStackTrace();
			this.majorsRegionsHxCounts=null;
		}return this.majorsRegionsHxCounts;
	}
	public Boolean[][] getMajorsRegionsCountsIsError(){
		if(this.majorsRegionsCountsIsError==null) try{
			this.majorsRegionsCountsIsError=new Boolean[this.getMajors().size()][];
			for(int i=0;i<this.majorsRegionsCountsIsError.length;i++){
				this.majorsRegionsCountsIsError[i]=new Boolean[this.getList().getList().size()];
				//先计算北京及周边地区实习大区
				int cnt=0;
				for(int j=0;j<this.majorsRegionsCountsIsError[i].length;j++) {
					if(!this.getList().getList().get(j).getList().get(0).getPracticeBase().getHx())
						for(int k=0;k<this.getList().getList().get(j).getList().size();k++)
							cnt+=this.getNumbers()[i][j][k];
				}
				int CMP=Integer.compare(cnt,this.getMajorsNHxCounts()[i]);
				//在计算回乡实习大区
				for(int j=0;j<this.majorsRegionsCountsIsError[i].length;j++) {
					PracticeBase pb=this.getList().getList().get(j).getList().get(0).getPracticeBase();
					int cmp=cnt=0;
					if(pb.getHx()) {
						for(int k=0;k<this.getList().getList().get(j).getList().size();k++)
							cnt+=this.getNumbers()[i][j][k];
						cmp=Integer.compare(cnt,this.getMajorsRegionsHxCounts()[i][j]);
					}else cmp=CMP;
					this.majorsRegionsCountsIsError[i][j]=cmp==0?null:(cmp>0);
				}
			}
			return this.majorsRegionsCountsIsError;
		}catch (NullPointerException | IndexOutOfBoundsException e){
			e.printStackTrace();
			this.majorsRegionsCountsIsError=null;
		}return this.majorsRegionsCountsIsError;
	}
	public int getLastNHxRegionIndex() {
		if(lastNHxRegionIndex>=0) return this.lastNHxRegionIndex;
		int i=0,res=-1;
		try{for(Leaf<Region,PracticeBaseWithRegion> rp:this.getList().getList()) {
				if(!rp.getList().get(0).getPracticeBase().getHx())
					res=i;
				i++;
		}}catch (NullPointerException | IndexOutOfBoundsException e){
			e.printStackTrace();
		}return this.lastNHxRegionIndex=res;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public PlanAllDesign(){
		super();
		System.out.println(">> PlanAllDesign:constructor > year="+this.getAnnual().getYear());
		this.list=Manager.loadSession(List_Region_PracticeBaseRegion.class, SessionListKey);
		this.majors=Manager.loadSession(List.class, SessionMajorsKey);
		this.getNumbers();
	}
	
	public String display(){
		try {
			this.list=new List_Region_PracticeBaseRegion(this.getAnnual().getYear(),/*containsNullRegion*/false);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return this.returnWithTips(NONE,"数据库读取实习基地及大区信息失败",e);
		}
		if(this.getMajors()==null)
			return this.returnWithTips(NONE,"数据库读取专业列表失败！");
		if(this.getNumbers()==null)
			return this.returnWithTips(NONE,"数据库读取布局规划失败！");
		if(this.getMajorsCounts()==null)
			return this.returnWithTips(NONE,"数据库读取专业实习生人数失败！");
		if(this.getMajorsNHxCounts()==null)
			return this.returnWithTips(NONE,"数据库读取在北京及周边地区实习人数失败！");
		if(this.getMajorsRegionsHxCounts()==null)
			return this.returnWithTips(NONE,"数据库读取回生源地实习人数失败！");
		if(getMajorsRegionsCountsIsError()==null)
			return this.returnWithTips(NONE,"数据库检测实习生人数与规划人数失败！");
		if(this.list!=null)
			Manager.saveSession(SessionListKey,this.list);
		if(this.majors!=null)
			Manager.saveSession(SessionMajorsKey,this.majors);
		return NONE;
	}
	
	@Override
	public String execute(){
		boolean ok=false;
		StringBuilder error=new StringBuilder();
		if(this.list==null||this.numbers==null||this.majors==null)
			return display();
		for(int i=0;i<this.numbers.length;i++){
			for(int j=0;j<this.numbers[i].length;j++){
				for(int k=0;k<this.numbers[i][j].length;k++){
					int num=this.numbers[i][j][k];
					if(num<0) num=0;
					Plan p=new Plan();
					p.setYear(this.getAnnual().getYear());
					p.setMajor(this.majors.get(i).getName());
					p.setPracticeBase(this.list.getList().get(j).getList().get(k).getPracticeBase().getName());
					try {
						if(p.existAndLoad()){
							if(p.getNumber()!=num){
								if(num==0)
									p.delete();
								else {
									p.setNumber(num);
									p.update();
								}
							}
						}else if(num>0){
							p.setNumber(num);
							p.create();
						}
						ok=true;
					} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
						e.printStackTrace();
						error.append("\n("+this.getMajors().get(i)+","+
						this.list.getList().get(j).getT().getDescription()+","+
						this.list.getList().get(j).getList().get(k).getPracticeBase().getDescription()+
						")"+e.getMessage());
						continue;
					}
				}
			}
		}
		for(Leaf<Region,PracticeBaseWithRegion> rp:this.list.getList()) {
			for(PracticeBaseWithRegion pair:rp.getList()) try {
				pair.getPracticeBase().update();
				ok=true;
			}catch(IllegalArgumentException | SQLException e) {
				e.printStackTrace();
				error.append("\n"+pair.getPracticeBase().getDescription()+"备注修改失败！("+e.getMessage()+")");
			}
		}
		if(!ok)
			return this.jumpToMethodWithTips("display","修改失败！\n\n失败条目:"+error.toString());
		else if(error.length()>0)
			return this.jumpToMethodWithTips("display","修改成功！\n\n失败条目:"+error.toString());
		else
			return this.jumpToMethodWithTips("display","修改成功！");
	}

	
	
}
