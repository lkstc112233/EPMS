package action.function;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.*;
import obj.annualTable.Student;
import obj.annualTable.ListOfPracticeBaseAndStudents;
import obj.staticObject.InnerPerson;
import obj.staticSource.Major;
import token.Role;

/**
 * 确定实习生大组长和指导教师
 */
public class StudentGroupLeader extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}


	private ListOfPracticeBaseAndStudents practiceBaseAndStudents;
	private String[] choose=new String[]{null,null};//[0]基地,[1]学生Id
	
	public ListOfPracticeBaseAndStudents getPracticeBaseAndStudents(){return this.practiceBaseAndStudents;}
	public String[] getChoose(){return this.choose;}
	
	//记忆化部件
	public Student getStudent(){return new Student();}
	private List<InnerPerson> innerPersons;
	public List<InnerPerson> getInnerPersons(){
		if(this.innerPersons!=null) return this.innerPersons;
		try {
			if(Manager.getUser().getSchool().equals(Role.jwc.getName()))
				return this.innerPersons=InnerPerson.list(InnerPerson.class
						,new Restraint(Field.getField(InnerPerson.class,"name"),Restraint.Type.NotLike,InnerPerson.UndefinedName)
						);
			else
				return this.innerPersons=Base.list(InnerPerson.class,new Restraint(new Restraint.Part[]{
						new Restraint.Part(Field.getField(InnerPerson.class,"school"),Manager.getUser().getSchool()),
						new Restraint.Part(Field.getField(InnerPerson.class,"name"),Restraint.Type.NotLike,InnerPerson.UndefinedName)
						}));
		}catch(SQLException | IllegalArgumentException | InstantiationException e) {
			e.printStackTrace();
		}return this.innerPersons=null;
	}
	

	static public final String SessionListKey=action.function.student.Export.SessionListKey; 
	
	public StudentGroupLeader(){
		super();
		this.practiceBaseAndStudents=Manager.loadSession(ListOfPracticeBaseAndStudents.class,SessionListKey);
	}
	
	

	/**
	 * 用于显示
	 */
	public String display(){
		System.out.println(">> Export:display > year="+this.getAnnual().getYear());
		this.practiceBaseAndStudents=null;
		try{
			this.practiceBaseAndStudents=new ListOfPracticeBaseAndStudents(
					this.getAnnual().getYear(),/*major*/null);
		} catch (IllegalArgumentException | InstantiationException | SQLException e) {
			return Manager.tips("数据库开小差去了！",e,NONE);
		}
		Manager.saveSession(SessionListKey,this.practiceBaseAndStudents);
		System.out.println(">> Export:display <NONE");
		return NONE;
	}
	
	

	private class Pair implements Comparable<Pair>{
		Major major;
		int gro,stu;
		public Pair(Major major,int a,int b) {this.major=major;gro=a;stu=b;}
		public int compareTo(Pair p) {
			if(p==null) return 1;
			if(stu==0 && p.stu==0) return Integer.compare(gro,p.gro);
			if(stu==0) return -1;
			if(p.stu==0) return 1;
			int cmp=Double.compare(gro*1.0/stu,p.gro*1.0/p.stu);
			if(cmp!=0) return cmp;
			return Integer.compare(p.stu,stu);
		}
	}
	
	@Override
	public String execute(){
		if(this.practiceBaseAndStudents==null)
			return Manager.tips("数据库开小差去了!",
					display());
		System.out.println(">> StudentGroupLeaderRecommend:execute > choose= ["+this.choose[0]+","+this.choose[1]+"]");
		ListOfPracticeBaseAndStudents.RegionPair.PracticeBasePair choose_pair=
				this.practiceBaseAndStudents.get(this.choose[0]);//choose[0]是大区名称
		if(choose_pair==null)
			return Manager.tips("请选择正确的实习基地！",
					display());
		//StudenGroupLeaderRecommend:execute
		//推荐大组长：choose[1]学生
		Student pro=null;
		for(Student stu:choose_pair.getStudents()) if(stu.getId().equals(this.choose[1])) {
			pro=stu;
			break;
		}
		if(pro==null)
			return Manager.tips("基地("+this.choose[0]+")没有学生学号为("+this.choose[1]+")!",
					display());
		choose_pair.getRegion().setStudentGroupLeaderId(this.choose[1]);
		try {
			choose_pair.getRegion().update();
		}catch(SQLException | IllegalArgumentException e) {
			return Manager.tips("基地("+choose_pair.getPracticeBase().getDescription()+")学生大组长设置失败!",
					e,display());
		}
		return Manager.tips("基地("+choose_pair.getPracticeBase().getDescription()+")学生大组长("+pro.getDescription()+")设置成功!",
				display());
	}
	
	
	public String create(){
		if(this.practiceBaseAndStudents==null)
			return Manager.tips("数据库开小差去了!",
					display());
		StringBuilder error=new StringBuilder();
		Map<String,Pair> prepared=new HashMap<String,Pair>();
		for(ListOfPracticeBaseAndStudents.RegionPair rp:this.practiceBaseAndStudents.getList()){
			for(ListOfPracticeBaseAndStudents.RegionPair.PracticeBasePair pair:rp.getList()){
				for(Student s:pair.getStudents()) try{
					if(!prepared.containsKey(s.getMajor()))
						prepared.put(s.getMajor(),new Pair(new Major(s.getMajor()),0,1));
					else
						prepared.get(s.getMajor()).stu++;
				}catch(IllegalArgumentException | SQLException e) {
					return Manager.tips("读取专业列表失败，已停止!",display());
				}
			}
		}
		if(prepared.isEmpty())
			return Manager.tips("读取学生列表失败，已停止!",display());
		for(ListOfPracticeBaseAndStudents.RegionPair rp:this.practiceBaseAndStudents.getList()){
			for(ListOfPracticeBaseAndStudents.RegionPair.PracticeBasePair pair:rp.getList()) {
				String groupLeaderId=pair.getRegion().getStudentGroupLeaderId();
				if(groupLeaderId==null || groupLeaderId.isEmpty())
					continue;
				try {
					Student stu=new Student(this.getAnnual().getYear(),groupLeaderId);
					//若学生不在的话则清空！
					boolean flag=false;
					if(prepared.containsKey(stu.getMajor())) {
						for(Student s:pair.getStudents()) {
							if(s.getId().equals(stu.getId())) {
								flag=true;
								break;
							}
						}
					}
					if(!flag) {
						error.append("\n基地("+pair.getPracticeBase().getDescription()+")原有学生大组长("+stu.getDescription()+")不存在该大区实习生名单中，已剔除!");
						pair.getRegion().setStudentGroupLeaderId(null);
						try{pair.getRegion().update();
						}catch(IllegalArgumentException | SQLException e2) {
						}
					}else
						prepared.get(stu.getMajor()).gro++;
				}catch(IllegalArgumentException | SQLException e) {
					return Manager.tips("服务器开小差去了，无法读取学生大组长！\n"+error.toString(),e,display());
				}
			}
		}
		List<Pair> preparedMajor=new ArrayList<Pair>();
		for(Entry<String,Pair> entry:prepared.entrySet())
			preparedMajor.add(entry.getValue());
		for(ListOfPracticeBaseAndStudents.RegionPair rp:this.practiceBaseAndStudents.getList()){
			for(ListOfPracticeBaseAndStudents.RegionPair.PracticeBasePair pair:rp.getList()) {
				if(Field.s2S(pair.getRegion().getStudentGroupLeaderId())==null) {
					//每个学院都一定会推荐学生的
					Collections.sort(preparedMajor);
					boolean ok=false;
					for(Pair p:preparedMajor) if(!pair.getStudents().isEmpty()){
						Student gro=null;
						boolean flag=false;
						for(Student s:pair.getStudents()) {
							if(p.major.getName().equals(s.getMajor())) {
								flag=true;
								if(s.getRecommend()) {
									gro=s;
									break;
								}
							}
						}
						if(gro==null) {
							if(flag)
								error.append("\n专业("+p.major.getDescription()+")在基地("+pair.getPracticeBase().getDescription()+")未推荐学生大组长!");
						}
						else {
							pair.getRegion().setStudentGroupLeaderId(gro.getId());
							try{
								pair.getRegion().update();
								p.gro++;//TODO check: 排序内容是否更新？
								ok=true;
								break;
							}catch(IllegalArgumentException | SQLException e) {
								error.append("\n基地("+pair.getPracticeBase().getDescription()+")设定学生大组长("+gro.getDescription()+")时，数据库开小差去了!");
							}
						}
					}
					if(!ok)
						error.append("\n基地("+pair.getPracticeBase().getDescription()+")未能成功设置学生大组长!");
				}
			}
		}
		return Manager.tips(error.length()<=0?"设定完毕！":
			("设定结束！\n错误信息如下：\n"+error.toString()),display());
	}
	
	
	
}
