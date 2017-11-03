package action.jwc;

import java.sql.SQLException;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.annualTable.*;
import obj.staticObject.InnerPerson;
import obj.staticObject.PracticeBase;

public class RegionLeaderAndSupervisorDesign extends ActionSupport{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private ListOfRegionAndPracticeBases regionAndPracticeBase;
	private Supervise[][][] supervises;
	private List<InnerPerson> innerPersons;
	static public final String SessionListKey="RegionLeaderAndSupervisorDesign_List";

	public ListOfRegionAndPracticeBases getRegionAndPracticeBase(){return this.regionAndPracticeBase;}
	public Supervise[][][] getSupervises(){return this.supervises;}
	public int[] getSuperviseTypeList(){return Supervise.getTypesList();}
	public List<InnerPerson> getInnerPersons(){return this.innerPersons;}

	public RegionLeaderAndSupervisorDesign(){
		super();
		System.out.println(">> RegionLeaderAndSupervisorDesign:constructor > year="+this.getAnnual().getYear());
		if(!this.annual.checkYear()){
			System.err.println(">> RegionLeaderAndSupervisorDesign:constructor > year has been setup!");
			this.annual.setupYear();
		}
		this.regionAndPracticeBase=Manager.loadSession(ListOfRegionAndPracticeBases.class, SessionListKey);
		this.setupSupervises();
	}
	private void setupSupervises(){
		if(this.regionAndPracticeBase==null)
			return;
		this.supervises=new Supervise[this.getSuperviseTypeList().length]
				[this.regionAndPracticeBase.getList().size()][];
		try{
			for(int type:this.getSuperviseTypeList()){
				for(int i=0;i<this.supervises[type].length;i++){
					List<PracticeBase> pbs=this.regionAndPracticeBase.getList().get(i).getPracticeBases();
					this.supervises[type][i]=new Supervise[pbs.size()];
					for(int j=0;j<this.supervises[type][i].length;j++){
						Supervise tmp=new Supervise();
						tmp.setYear(this.getAnnual().getYear());
						tmp.setPracticeBase(pbs.get(j).getName());
						tmp.setSuperviseType(type);
						try {
							tmp.load();
						} catch (SQLException | IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						this.supervises[type][i][j]=tmp;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			this.supervises=null;
		}
	}
	
	public String display(){
		try {
			this.innerPersons=InnerPerson.list(InnerPerson.class,new String[]{"name"},new Object[]{"未定%"});
		} catch (SQLException | NoSuchFieldException e) {
			return Manager.tips("数据库读取校内人员列表失败！",
					e,NONE);
		}
		try {
			this.regionAndPracticeBase=new ListOfRegionAndPracticeBases(this.getAnnual().getYear(),/*containsNullRegion*/false);
		} catch (SQLException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			return Manager.tips("数据库读取实习基地及大区信息失败！",
					e,NONE);
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
			} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
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
		if(!ok)
			return Manager.tips("修改失败！失败条目:"+error.toString(),
					display());
		else if(error.length()>0)
			return Manager.tips("修改成功！\n失败条目:"+error.toString(),
					display());
		else
			return Manager.tips("修改成功！",
					display());
	}

	
	
}
