package action.function;

import java.sql.SQLException;
import java.util.*;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.list.Leaf;
import obj.annualTable.list.List_Region_PracticeBaseRegion;
import obj.annualTable.list.PracticeBaseWithRegion;
import obj.staticObject.*;

/**
 * 设置当年实习基地信息表（总领队、督导老师、入校时间、入校地点等）
 */
public class RegionInfo extends Action{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private List_Region_PracticeBaseRegion list;
	private Supervise[][][] supervises;
	private List<InnerPerson> innerPersons;
	static public final String SessionListKey="RegionInfo_List";

	public List_Region_PracticeBaseRegion getList(){return this.list;}
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
		if(this.list==null) return this.supervises=null;
		if(this.supervises!=null) return this.supervises;
		this.supervises=new Supervise[this.getSuperviseTypeList().length]
				[this.list.getList().size()][];
		for(int type:this.getSuperviseTypeList()){
			for(int i=0;i<this.supervises[type].length;i++){
				List<PracticeBaseWithRegion> pbrs=this.list.getList().get(i).getList();
				this.supervises[type][i]=new Supervise[pbrs.size()];
				for(int j=0;j<this.supervises[type][i].length;j++){
					Supervise tmp=new Supervise();
					tmp.setYear(this.getAnnual().getYear());
					tmp.setPracticeBase(pbrs.get(j).getPracticeBase().getName());
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
		this.list=Manager.loadSession(List_Region_PracticeBaseRegion.class, SessionListKey);
		this.getSupervises();
	}
	
	public String display(){
		if(this.getInnerPersons()==null)
			return this.returnWithTips(NONE,"数据库读取校内人员列表失败！");
		try {
			this.list=new List_Region_PracticeBaseRegion(this.getAnnual().getYear(),/*containsNullRegion*/false);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return this.returnWithTips(NONE,"数据库读取实习基地及大区信息失败！");
		}
		this.getSupervises();
		if(this.list!=null)
			Manager.saveSession(SessionListKey,this.list);
		return NONE;
	}
	
	private String practiceBaseName;
		public String getPracticeBaseName() {return this.practiceBaseName;}
		public void setPracticeBaseName(String a) {this.practiceBaseName=Field.s2S(a);}
	
	@Override
	public String execute(){
		boolean ok=false;
		StringBuilder error=new StringBuilder();
		if(this.list==null)
			return display();
		//保存Region
		Leaf<Region,PracticeBaseWithRegion> pair=this.list.get(this.practiceBaseName);
		if(pair==null)
			return this.returnWithTips(NONE,"实习基地选择错误!("+this.practiceBaseName+")");
		Region region=pair.getT();
		try {
			region.update();
			ok=true;
		} catch (IllegalArgumentException | SQLException e) {
			e.printStackTrace();
			if(error.length()>0) error.append(',');
			error.append(region.getName()+"的相关信息保存失败!("+e.getMessage()+")");
		}
		//保存Supervise
		int[] index=this.list.indexOf(this.practiceBaseName);
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
