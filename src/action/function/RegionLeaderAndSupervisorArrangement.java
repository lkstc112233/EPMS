package action.function;

import java.sql.SQLException;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.*;
import obj.annualTable.*;
import obj.staticObject.InnerPerson;
import obj.staticObject.PracticeBase;

public class RegionLeaderAndSupervisorArrangement extends ActionSupport{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private ListOfRegionAndPracticeBases regionAndPracticeBase;
	private Supervise[][][] supervises;
	private InnerPerson[] innerPersonLeaders;
	private InnerPerson[][][] innerPersonSupervisors;
	private List<InnerPerson> innerPersons;
	static public final String SessionListKey="RegionLeaderAndSupervisorDesign_List";

	public ListOfRegionAndPracticeBases getRegionAndPracticeBase(){return this.regionAndPracticeBase;}
	public Supervise[][][] getSupervises(){return this.supervises;}
	public int[] getSuperviseTypeList(){return Supervise.getTypesList();}
	public InnerPerson[] getInnerPersonLeaders(){return this.innerPersonLeaders;}
	public InnerPerson[][][] getInnerPersonSupervisors(){return this.innerPersonSupervisors;}
	public String getUserSchool(){return Manager.getUser().getSchool();}
	public List<InnerPerson> getInnerPersons(){return this.innerPersons;}

	public RegionLeaderAndSupervisorArrangement(){
		super();
		this.regionAndPracticeBase=Manager.loadSession(ListOfRegionAndPracticeBases.class, SessionListKey);
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
		if(this.regionAndPracticeBase==null)
			return;
		this.innerPersonLeaders=new InnerPerson[this.regionAndPracticeBase.getList().size()];
		for(int i=0;i<this.innerPersonLeaders.length;i++)
			this.innerPersonLeaders[i]=createInnerPerson(this.regionAndPracticeBase.getList().get(i).getRegion().getLeaderId());
		this.supervises=new Supervise[this.getSuperviseTypeList().length]
				[this.regionAndPracticeBase.getList().size()][];
		this.innerPersonSupervisors=new InnerPerson[this.getSuperviseTypeList().length]
				[this.regionAndPracticeBase.getList().size()][];
		for(int type:this.getSuperviseTypeList()){
			for(int i=0;i<this.supervises[type].length;i++){
				List<PracticeBase> pbs=this.regionAndPracticeBase.getList().get(i).getPracticeBases();
				this.supervises[type][i]=new Supervise[pbs.size()];
				this.innerPersonSupervisors[type][i]=new InnerPerson[pbs.size()];
				for(int j=0;j<this.supervises[type][i].length;j++){
					Supervise tmp=new Supervise();
					tmp.setYear(this.getAnnual().getYear());
					tmp.setPracticeBase(pbs.get(j).getName());
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
			if(Manager.getUser().getSchool().equals("教务处"))
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
			return Manager.tips("数据库读取校内人员列表失败！",e,NONE);
		}
		try{
			this.regionAndPracticeBase=new ListOfRegionAndPracticeBases(this.getAnnual().getYear(),/*containsNullRegion*/false);
		}catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return Manager.tips("数据库读取实习基地及大区信息失败！",e,NONE);
		}
		this.setupSupervises();
		if(this.regionAndPracticeBase!=null)
			Manager.saveSession(SessionListKey,this.regionAndPracticeBase);
		return NONE;
	}
	
	@Override
	public String execute(){
		boolean ok=false;
		StringBuilder error=new StringBuilder();
		if(this.regionAndPracticeBase==null)
			return display();
		//保存所有的Region的Leader
		for(ListOfRegionAndPracticeBases.Pair pair:this.regionAndPracticeBase.getList()){
			Region region=pair.getRegion();
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
		return Manager.tips("修改"+(ok?"成功":"失败")+"！"
				+(error.length()>0?("\n"+error.toString()):""),
				display());
	}

	
	
}
