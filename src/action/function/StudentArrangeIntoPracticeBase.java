package action.function;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.Base;
import obj.Field;
import obj.JoinParam;
import obj.Restraint;
import obj.annualTable.*;
import obj.staticObject.PracticeBase;
import obj.staticSource.Major;
import token.Role;

/**
 * 导入免费师范生数据
 */
public class StudentArrangeIntoPracticeBase extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private String majorName="汉语言文学（师范）";
	private boolean[] checkBox;
	private ListOfPracticeBaseAndStudents practiceBaseAndStudents;
	private String practiceBaseName;
	
	public void setMajorName(String a){this.majorName=Field.s2S(a);}
	public String getMajorName(){return majorName;}
	public void setCheckBox(boolean[] a){this.checkBox=a;}
	public boolean[] getCheckBox(){return this.checkBox;}
	public ListOfPracticeBaseAndStudents getPracticeBaseAndStudents(){return this.practiceBaseAndStudents;}
	public String getPracticeBaseName(){return this.practiceBaseName;}
	public void setPracticeBaseName(String a){this.practiceBaseName=a;}
	
	//记忆化部件
	public Student getStudent(){return new Student();}
	private List<PracticeBase> practiceBases;
		public List<PracticeBase> getPracticeBases(){
			if(this.practiceBases!=null) return this.practiceBases;
			try {
				this.practiceBases=new ArrayList<PracticeBase>();
				List<Base[]> tmp=Base.list(new JoinParam(PracticeBase.class).append(
						JoinParam.Type.InnerJoin,Region.class,
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Plan.class,"practiceBase"),
						new Field[]{
								Field.getField(Plan.class,"year"),
								Field.getField(Plan.class,"major")},
						new Object[]{
								Integer.valueOf(this.getAnnual().getYear()),
								this.majorName}
						),
						new Restraint(Field.getField(Plan.class,"number"),Restraint.Type.Bigger,0));
				for(Base[] bs:tmp) {
					if(bs!=null &&bs.length>=2 && bs[0]!=null && bs[1]!=null) {
						PracticeBase pb=(PracticeBase)bs[0];
						this.practiceBases.add(pb);
					}
				}
			}catch(SQLException | IllegalArgumentException | InstantiationException e) {
				e.printStackTrace();
			}return this.practiceBases=null;
		}
	private List<Major> majors;
		public List<Major> getMajors(){
			if(this.majors!=null) return this.majors;
			Role role=Role.getRoleByInnerPerson(Manager.getUser());
			if(role==null) return this.majors=null;
			try{
				if(role==Role.jwc)
					return this.majors=Base.list(Major.class);
				else
					return this.majors=Base.list(Major.class,
							new Restraint(Field.getField(Major.class,"school"),Manager.getUser().getSchool()));
			}catch(SQLException | IllegalArgumentException | InstantiationException e) {
				e.printStackTrace();
			}return this.majors=null;
		}
	

	static public final String SessionListKey="RegionArrangement_RegionAndPracticeBases"; 
	
	public StudentArrangeIntoPracticeBase(){
		super();
		this.practiceBaseAndStudents=Manager.loadSession(ListOfPracticeBaseAndStudents.class,SessionListKey);
		this.setupCheckBox();
	}

	private void setupCheckBox(){
		this.checkBox=null;
		if(this.practiceBaseAndStudents!=null){
			int len=0;
			for(ListOfPracticeBaseAndStudents.Pair p:this.practiceBaseAndStudents.getList())
				len=Math.max(len,p.getStudents().size());
			this.checkBox=new boolean[len];
		}
	}

	
	/**
	 * 用于显示
	 */
	public String display(){
		this.practiceBaseName=null;
		System.out.println(">> RegionArrangement:display > year="+this.getAnnual().getYear()+",majorName="+majorName);
		this.practiceBaseAndStudents=null;
		try{
			new Major(this.majorName);
		}catch(IllegalArgumentException | SQLException e){
			return Manager.tips("专业("+this.majorName+")不存在！",e,NONE);
		}
		if(this.getPracticeBases()==null)
			return Manager.tips("读取实习基地列表失败!",NONE);
		if(this.getMajors()==null)
			return Manager.tips("读取实习专业列表失败!",NONE);
		Manager.saveSession(SessionListKey,this.practiceBaseAndStudents);
		this.setupCheckBox();
		System.out.println(">> RegionArrangement:display <NONE");
		return NONE;
	}
	
	/**
	 * 用于创建实习大区
	 */
	@Override
	public String execute(){
		if(this.practiceBaseAndStudents==null)
			return display();
		System.out.println(">> RegionArrangement:execute > practiceBaseName= "+this.practiceBaseName);
		if(this.practiceBaseName==null || this.practiceBaseName.isEmpty())
			return Manager.tips("请选择一个实习基地！",
					display());
		ListOfPracticeBaseAndStudents.Pair pair=this.practiceBaseAndStudents.get(this.practiceBaseName);
		if(pair==null) {
			try {
				PracticeBase pb=new PracticeBase(this.practiceBaseName);
				Major major=new Major(this.majorName);
				this.practiceBaseAndStudents.put(pb,null,this.annual.getYear(),major);
				pair=this.practiceBaseAndStudents.get(this.practiceBaseName);
			}catch(IllegalArgumentException | SQLException | InstantiationException e) {
				return Manager.tips("数据库开小差去了！",
						e,display());
			}
		}else if(pair.getPlan()==null)
			return Manager.tips("基地("+this.practiceBaseName+")没有("+this.majorName+")专业派遣计划！",
					display());
		//RegionArrangement:execute
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag)
			return Manager.tips("请至少选择一个实习生分配到实习基地！",
					display());
		ListOfPracticeBaseAndStudents.Pair nullPair=this.practiceBaseAndStudents.get((PracticeBase)null);
		//	List<PracticeBase> tmp=new ArrayList<PracticeBase>();
		StringBuilder sb=new StringBuilder();
		StringBuilder error=new StringBuilder();
		for(int i=0;i<nullPair.getStudents().size();i++){
			if(checkBox[i]){
				//选中了
				Student stu=nullPair.getStudents().get(i);
				if(stu==null||stu.getName()==null)
					continue;
				//	tmp.add(s);
				try{
					if(pair.getPlan().check(stu,pair.getStudents().size())){
						stu.setPracticeBase(this.practiceBaseName);
						stu.update();
					}
				}catch(SQLException | IllegalArgumentException e){
					e.printStackTrace();
					if(error.length()>0) error.append('\n');
					error.append(stu.getName()+"("+e.getMessage()+")");
					continue;
				}
				pair.getStudents().add(stu);
				nullPair.getStudents().remove(stu);
				if(sb.length()>0) sb.append(',');
				sb.append(stu.getName());
			}
		}
		Manager.tips(sb.toString()+" 已经分配到基地("+this.practiceBaseName+")！"+
				(error.length()>0?("\n错误信息："+error.toString()):""));
		Manager.removeSession(SessionListKey);
		return display();
	}
	
	/**
	 * 用于从大区移除基地
	 */
	public String delete(){
		if(this.practiceBaseAndStudents==null)
			return display();
		System.out.println(">> RegionArrangement:delete > practiceBaseName= "+this.practiceBaseName);
		if(this.practiceBaseName==null || this.practiceBaseName.isEmpty())
			return Manager.tips("未选中实习生！",
					display());
		boolean flag=false;
		for(boolean s:checkBox) flag|=s;
		if(!flag)
			return Manager.tips("请至少选择一个实习生来移除！",
					display());
		List<Student> deleteStudents=this.practiceBaseAndStudents.get(this.practiceBaseName).getStudents();
		if(deleteStudents==null)
			return Manager.tips("选中了一个不存在的实习基地（"+this.practiceBaseName+"）！",
					display());
		//	List<PracticeBase> tmp=new ArrayList<PracticeBase>();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<deleteStudents.size();i++){
			if(checkBox[i]){
				//选中了
				Student s=deleteStudents.get(i);
				if(s==null||s.getName()==null)
					continue;
				//	tmp.add(s);
				try{
					s.setPracticeBase(null);
					s.update();
				}catch(SQLException | IllegalArgumentException e){
					e.printStackTrace();
					continue;
				}
				if(sb.length()>0) sb.append(',');
				sb.append(s.getName());
			}
		}
		Manager.tips(sb.toString()+" 已经从实习基地("+this.practiceBaseName+")移出！");
		Manager.removeSession(SessionListKey);
		return display();
	}
	
	
}
