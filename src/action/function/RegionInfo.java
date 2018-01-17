package action.function;

import java.sql.SQLException;
import java.util.*;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.ListOfRegionAndPracticeBases.RegionPair.PracticeBasePair;
import obj.staticObject.*;

/**
 * 设置当年实习基地信息表（总领队、督导老师、入校时间、入校地点等）
 */
public class RegionInfo extends Action{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private ListOfRegionAndPracticeBases regionAndPracticeBase;
	private Supervise[][][] supervises;
	private List<InnerPerson> innerPersons;
	static public final String SessionListKey="RegionInfo_List";

	public ListOfRegionAndPracticeBases getRegionAndPracticeBase(){return this.regionAndPracticeBase;}
	public int[] getSuperviseTypeList(){return Supervise.getTypeList();}
	public String[] getSuperviseTypeNameList(){return Supervise.getTypeNameList();}
	public List<InnerPerson> getInnerPersons(){
		if(this.innerPersons!=null) return this.innerPersons;
		try {
			return this.innerPersons=Base.list(InnerPerson.class);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			e.printStackTrace();
		}return this.innerPersons=null;
	}
	public Supervise[][][] getSupervises(){
		if(this.regionAndPracticeBase==null) return this.supervises=null;
		if(this.supervises!=null) return this.supervises;
		this.supervises=new Supervise[this.getSuperviseTypeList().length]
				[this.regionAndPracticeBase.getList().size()][];
		for(int type:this.getSuperviseTypeList()){
			for(int i=0;i<this.supervises[type].length;i++){
				List<PracticeBasePair> pbs=this.regionAndPracticeBase.getList().get(i).getList();
				this.supervises[type][i]=new Supervise[pbs.size()];
				for(int j=0;j<this.supervises[type][i].length;j++){
					Supervise tmp=new Supervise();
					tmp.setYear(this.getAnnual().getYear());
					tmp.setPracticeBase(pbs.get(j).getPracticeBase().getName());
					tmp.setSuperviseType(type);
					try {
						tmp.load();
					} catch (SQLException | IllegalArgumentException e) {
						e.printStackTrace();
					}
					this.supervises[type][i][j]=tmp;
				}
			}
		}
		return this.supervises;
	}

	public RegionInfo(){
		super();
		this.regionAndPracticeBase=Manager.loadSession(ListOfRegionAndPracticeBases.class, SessionListKey);
		this.getSupervises();
	}
	
	public String display(){
		if(this.getInnerPersons()==null)
			return this.returnWithTips(NONE,"数据库读取校内人员列表失败！");
		try {
			this.regionAndPracticeBase=new ListOfRegionAndPracticeBases(this.getAnnual().getYear(),/*containsNullRegion*/false);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return this.returnWithTips(NONE,"数据库读取实习基地及大区信息失败！");
		}
		this.getSupervises();
		if(this.regionAndPracticeBase!=null)
			Manager.saveSession(SessionListKey,this.regionAndPracticeBase);
		return NONE;
	}
	
	private String practiceBaseName;
		public String getPracticeBaseName() {return this.practiceBaseName;}
		public void setPracticeBaseName(String a) {this.practiceBaseName=Field.s2S(a);}
	
	@Override
	public String execute(){
		boolean ok=false;
		StringBuilder error=new StringBuilder();
		if(this.regionAndPracticeBase==null)
			return display();
		//保存Region
		ListOfRegionAndPracticeBases.RegionPair.PracticeBasePair pair=this.regionAndPracticeBase.get(this.practiceBaseName);
		if(pair==null)
			return this.returnWithTips(NONE,"实习基地选择错误!("+this.practiceBaseName+")");
		Region region=pair.getRegion();
		try {
			region.update();
			ok=true;
		} catch (IllegalArgumentException | SQLException e) {
			e.printStackTrace();
			if(error.length()>0) error.append(',');
			error.append(region.getName()+"的相关信息保存失败!("+e.getMessage()+")");
		}
		//保存Supervise
		int[] index=this.regionAndPracticeBase.indexOf(this.practiceBaseName);
		for(int type:this.getSuperviseTypeList()){
			Supervise tmp=null;
			try {
				tmp=this.supervises[type][index[0]][index[1]];
			} catch (IndexOutOfBoundsException e) {
				return this.returnWithTips(NONE,"实习基地选择错误!("+this.practiceBaseName+")");
			}
			try {
				if(tmp.getSupervisorId()==null||tmp.getSupervisorId().isEmpty())
					tmp.delete();
				else if(!tmp.exist())
					tmp.create();
				else
					tmp.update();
			} catch (SQLException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
				if(error.length()>0) error.append(',');
				error.append(tmp.getPracticeBase()+"的"+Supervise.getTypeNameList()[type]
						+"的相关信息保存失败!("+e.getMessage()+")");
			}
		}
		return this.jumpToMethodWithTips("display",
				"修改"+(ok?"成功":"失败")+"！"
				+(error.length()>0?("\n"+error.toString()):""));
	}

	
	
}
