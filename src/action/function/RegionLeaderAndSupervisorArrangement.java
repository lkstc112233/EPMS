package action.function;

import java.sql.SQLException;
import java.util.List;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.list.Leaf;
import obj.annualTable.list.List_Region_PracticeBase;
import obj.annualTable.list.PracticeBaseWithRegion;
import obj.staticObject.InnerPerson;
import token.Role;

/**
 * 指定总领队和督导老师
 */
public class RegionLeaderAndSupervisorArrangement extends Action{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private List_Region_PracticeBase list;
	private Supervise[][][] supervises;
	private InnerPerson[] innerPersonLeaders;
	private InnerPerson[][][] innerPersonSupervisors;
	private List<InnerPerson> innerPersons;
	static public final String SessionListKey="RegionLeaderAndSupervisorDesign_List";

	public List_Region_PracticeBase getList(){return this.list;}
	public Supervise[][][] getSupervises(){return this.supervises;}
	public int[] getSuperviseTypeList(){return Supervise.getTypeList();}
	public InnerPerson[] getInnerPersonLeaders(){return this.innerPersonLeaders;}
	public InnerPerson[][][] getInnerPersonSupervisors(){return this.innerPersonSupervisors;}
	public String getUserSchool(){return Manager.getUser().getSchool();}
	public List<InnerPerson> getInnerPersons(){return this.innerPersons;}

	public RegionLeaderAndSupervisorArrangement(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBase.class, SessionListKey);
		this.setupSupervises();
	}
	static private InnerPerson createInnerPerson(String id){
		try{if(id!=null && !id.isEmpty())
				return new InnerPerson(id);
		} catch (IllegalArgumentException | SQLException e) {
			e.printStackTrace();
		}return null;
	}
	private void setupSupervises(){
		if(this.list==null)
			return;
		this.innerPersonLeaders=new InnerPerson[this.list.getList().size()];
		for(int i=0;i<this.innerPersonLeaders.length;i++)
			this.innerPersonLeaders[i]=createInnerPerson(this.list.getList().get(i).getT().getLeaderId());
		this.supervises=new Supervise[this.getSuperviseTypeList().length]
				[this.list.getList().size()][];
		this.innerPersonSupervisors=new InnerPerson[this.getSuperviseTypeList().length]
				[this.list.getList().size()][];
		for(int type:this.getSuperviseTypeList()){
			for(int i=0;i<this.supervises[type].length;i++){
				List<PracticeBaseWithRegion> pbrs=this.list.getList().get(i).getList();
				this.supervises[type][i]=new Supervise[pbrs.size()];
				this.innerPersonSupervisors[type][i]=new InnerPerson[pbrs.size()];
				for(int j=0;j<this.supervises[type][i].length;j++){
					Supervise tmp=new Supervise();
					tmp.setYear(this.getAnnual().getYear());
					tmp.setPracticeBase(pbrs.get(j).getFirst().getName());
					tmp.setSuperviseType(type);
					try {
						tmp.load();
					} catch (SQLException | IllegalArgumentException e) {
						e.printStackTrace();
					}
					this.supervises[type][i][j]=tmp;
					this.innerPersonSupervisors[type][i][j]=createInnerPerson(tmp.getSupervisorId());
				}
			}
		}
	}
	
	public String display(){
		try{
			if(Manager.getUser().getSchool().equals(Role.jwc.getName()))
				this.innerPersons=InnerPerson.list(InnerPerson.class
					//	,new Restraint(Field.getField(InnerPerson.class,"name"),Restraint.Type.NotLike,InnerPerson.UndefinedName)
					//	注意教务处人员可以在该处修正各类学科规划（各部院系未定人员）
						);
			else
				this.innerPersons=Base.list(InnerPerson.class,new Restraint(new Restraint.Part[]{
						new Restraint.Part(Field.getField(InnerPerson.class,"school"),Manager.getUser().getSchool()),
				//		new Restraint.Part(Field.getField(InnerPerson.class,"name"),Restraint.Type.NotLike,InnerPerson.UndefinedName)
						}));
		}catch(SQLException | IllegalArgumentException | InstantiationException e){
			return this.returnWithTips(NONE,"数据库读取校内人员列表失败！",e);
		}
		try{
			this.list=new List_Region_PracticeBase(this.getAnnual().getYear(),/*containsNullRegion*/false);
		}catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return this.returnWithTips(NONE,"数据库读取实习基地及大区信息失败！",e);
		}
		this.setupSupervises();
		if(this.list!=null)
			Manager.saveSession(SessionListKey,this.list);
		return NONE;
	}
	
	@Override
	public String execute(){
		boolean ok=false;
		StringBuilder error=new StringBuilder();
		if(this.list==null)
			return this.returnWithTips(NONE,"实习基地选择错误!");
		//保存所有的Region的Leader
		for(Leaf<Region,PracticeBaseWithRegion> rp:this.list.getList()){
			Region region=rp.getT();
			try {
				region.update();
				ok=true;
			} catch (IllegalArgumentException | SQLException e) {
				e.printStackTrace();
				if(error.length()>0) error.append(',');
				error.append(region.getName());
			}
		}
		//保存所有的Supervise
		for(int type:this.getSuperviseTypeList()){
			for(int i=0;i<this.supervises[type].length;i++){
				for(int j=0;j<this.supervises[type][i].length;j++){
					Supervise tmp=this.supervises[type][i][j];
					try {
						if(tmp.getSupervisorId()==null||tmp.getSupervisorId().isEmpty())
							tmp.delete();
						else if(!tmp.exist())
							tmp.create();
						else
							tmp.update();
					} catch (SQLException | IllegalAccessException | InstantiationException e) {
						e.printStackTrace();
					}
				}
			}
		}		
		return this.jumpToMethodWithTips("display",
				"修改"+(ok?"成功":"失败")+"！"
				+(error.length()>0?("\n"+error.toString()):""));
	}

	
	
}
