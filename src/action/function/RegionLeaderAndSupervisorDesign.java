package action.function;

import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.list.Leaf;
import obj.annualTable.list.List_Region_PracticeBaseRegion;
import obj.annualTable.list.PracticeBaseWithRegion;
import obj.staticObject.*;
import obj.staticSource.Major;
import obj.staticSource.School;

/**
 * 实习总领队和督导任务学科规划
 */
public class RegionLeaderAndSupervisorDesign extends Action{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private List_Region_PracticeBaseRegion list;
	private Supervise[][][] supervises;
	private List<InnerPerson> innerPersons;
	static public final String SessionListKey="RegionLeaderAndSupervisorDesign_List";

	public List_Region_PracticeBaseRegion getList(){return this.list;}
	public int[] getSuperviseTypeList(){return Supervise.getTypeList();}
	public String[] getSuperviseTypeNameList(){return Supervise.getTypeNameList();}
	public List<InnerPerson> getInnerPersons(){
		if(this.innerPersons!=null) return this.innerPersons;
		try {
			return this.innerPersons=Base.list(InnerPerson.class,new Restraint(
					Field.getField(InnerPerson.class,"name"),Restraint.Type.Like,InnerPerson.UndefinedName));
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

	public RegionLeaderAndSupervisorDesign(){
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
			return this.returnWithTips(NONE,"数据库读取实习基地及大区信息失败！",e);
		}
		this.getSupervises();
		if(this.list!=null)
			Manager.saveSession(SessionListKey,this.list);
		return NONE;
	}
	
	@Override
	public String execute(){
		boolean ok=false;
		StringBuilder error=new StringBuilder();
		if(this.list==null)
			return this.jumpBackWithTips("实习基地选择错误!");
		//保存所有的Region的Leader
		for(Leaf<Region,PracticeBaseWithRegion> rp:this.list.getList()){
			Region region=rp.getT();
			try {
				region.update();
				ok=true;
			} catch (IllegalArgumentException | SQLException e) {
				e.printStackTrace();
				if(error.length()>0) error.append(',');
				error.append(region.getName()+"的相关信息保存失败!("+e.getMessage()+")");
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
						error.append(tmp.getPracticeBase()+"的"+Supervise.getTypeNameList()[type]
								+"的相关信息保存失败!("+e.getMessage()+")");
					}
				}
			}
		}
		return this.jumpToMethodWithTips("display",
				"修改"+(ok?"成功":"失败")+"！"
				+(error.length()>0?("\n"+error.toString()):""));
	}

	
	private class Pair implements Comparable<Pair>{
		School school;
		int sup,stu;
		public Pair(School s,int a,int b) {school=s;sup=a;stu=b;}
		public int compareTo(Pair p) {
			if(p==null) return 1;
			if(stu==0 && p.stu==0) return Integer.compare(sup,p.sup);
			if(stu==0) return -1;
			if(p.stu==0) return 1;
			int cmp=Double.compare(sup*1.0/stu,p.sup*1.0/p.stu);
			if(cmp!=0) return cmp;
			return Integer.compare(p.stu,stu);
		}
	}
	/**
	 * 自动填充空白的总领队和督导学科规划
	 */
	public String create() {
		for(Leaf<Region,PracticeBaseWithRegion> rp:this.list.getList()){
			Region region=rp.getT();
			if(region.getLeaderId()==null || region.getLeaderId().isEmpty())
				return this.returnWithTips(NONE,"请先将总领队学科规划填充完毕！");
		}
		Map<String,Pair> prepared=new HashMap<String,Pair>();
		try {
			for(Base[] tmp:Base.list(new JoinParam(Student.class)
					.append(JoinParam.Type.LeftJoin,
							Major.class,
							Field.getField(Student.class,"major"),
							Field.getField(Major.class,"name"),
							Field.getField(Student.class,"year"),
							this.getAnnual().getYear()))){
				if(tmp!=null && tmp.length>=2 && tmp[0]!=null && tmp[1]!=null) try{
					Major m=(Major)tmp[1];
					if(!prepared.containsKey(m.getSchool()))
						prepared.put(m.getSchool(),new Pair(new School(m.getSchool()),0,1));
					else
						prepared.get(m.getSchool()).stu++;
				} catch (IllegalArgumentException | SQLException e) {
					return this.returnWithTips(NONE,"读取部院系列表失败!",e);
				}
			}
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return this.returnWithTips(NONE,"读取实习生列表失败!",e);
		}
		if(prepared.isEmpty())
			return this.returnWithTips(NONE,"第一阶段读取部院系列表失败，已停止!");
		int j=0;for(Leaf<Region, PracticeBaseWithRegion> rp:this.list.getList()){
			String leaderId=rp.getT().getLeaderId();
			for(int type:this.getSuperviseTypeList()) {
				for(Supervise sup:this.supervises[type][j]) {
					if((type==this.getSuperviseTypeList()[0] || type==this.getSuperviseTypeList()[this.getSuperviseTypeList().length-1]) && 
							(sup.getSupervisorId()==null || sup.getSupervisorId().isEmpty())) try {
						//只包含头尾督导且是空白的
						sup.setSupervisorId(leaderId);
						if(!sup.exist())
							sup.create();
						else
							sup.update();
					}catch(SQLException | IllegalArgumentException | InstantiationException | IllegalAccessException e) {
						return this.jumpToMethodWithTips("display","第二阶段出现问题，已停止!",e);
					}
					//统计计数
					if(sup.getSupervisorId()!=null && !sup.getSupervisorId().isEmpty()) try {
						InnerPerson inner=new InnerPerson(sup.getSupervisorId());
						if(!prepared.containsKey(inner.getSchool()))
							;//无学生的不管
						else
							prepared.get(inner.getSchool()).sup++;
					}catch(SQLException | IllegalArgumentException e) {
						return this.jumpToMethodWithTips("display","第二阶段出现未知老师工号，已停止!",e);
					}
				}
			}j++;
		}
		List<Pair> preparedSchool=new ArrayList<Pair>();
		for(Entry<String,Pair> entry:prepared.entrySet())
			preparedSchool.add(entry.getValue());
		StringBuilder error=new StringBuilder();
		for(int i=1;i<this.getSuperviseTypeList().length-1;i++){
			//不包含头尾督导
			int type=this.getSuperviseTypeList()[i];
			for(j=0;j<this.getSupervises()[type].length;j++) {
				for(int k=0;k<this.getSupervises()[type][j].length;k++) {
					Supervise sup=this.getSupervises()[type][j][k];
					PracticeBase pb=this.getList().getList().get(j).getList().get(k).getPracticeBase();
					if(sup.getSupervisorId()==null || sup.getSupervisorId().isEmpty()) {
						Collections.sort(preparedSchool);
						Pair pair=null;
						for(Pair p:preparedSchool) {
							//检查当前实习基地pb是否有pair.school部院系的学生
							//需要调用plan
							List<Base[]> tmp=null;
							try{tmp=Base.list(new JoinParam(Plan.class)
										.append(JoinParam.Type.InnerJoin,
												Major.class,
												Field.getField(Plan.class,"major"),
												Field.getField(Major.class,"name"),
												new Field[]{
														Field.getField(Plan.class,"year"),
														Field.getField(Plan.class,"practiceBase"),
														Field.getField(Major.class,"school")},
												new Object[] {
														this.getAnnual().getYear(),
														pb.getName(),
														p.school.getName()})
										);
							} catch (IllegalArgumentException | InstantiationException | SQLException e) {
								e.printStackTrace();
								continue;
							}
							if(tmp!=null && !tmp.isEmpty()) {
								Base[] bs=tmp.get(0);
								if(bs!=null && bs.length>=2 && bs[0]!=null) {
									if(((Plan)bs[0]).getNumber()>0)//有
										pair=p;
								}
							}
							if(pair!=null) break;
						}
						InnerPerson inner=null;
						if(pair!=null) for(InnerPerson in:this.getInnerPersons())
							if(pair.school.getName().equals(in.getSchool())){
								inner=in;
								break;
							}
						if(inner==null) error.append("\n"+pb.getDescription()+"没有合适的督导老师!");
						else try{
							sup.setSupervisorId(inner.getId());
							if(!sup.exist())
								sup.create();
							else
								sup.update();
							pair.sup++;//TODO check: 排序内容是否更新？
						}catch(SQLException | IllegalArgumentException | InstantiationException | IllegalAccessException e) {
							e.printStackTrace(); 
							error.append("\n"+pb.getDescription()+"没有合适的"+this.getSuperviseTypeNameList()[type]+"老师!("+e.getMessage()+")");
						}
					}
				}
			}
		}
		return this.jumpToMethodWithTips("display",
				error.length()<=0?"填充完毕！":
					("填充结束！\n错误信息如下：\n"+error.toString()));
	}
	
}
