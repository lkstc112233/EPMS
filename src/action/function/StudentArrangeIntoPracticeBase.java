package action.function;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.annualTable.*;
import obj.staticObject.PracticeBase;
import obj.staticSource.Major;

/**
 * 导入免费师范生数据
 */
public class StudentArrangeIntoPracticeBase extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private String majorName;
	private boolean[] checkBox;
	private ListOfPracticeBaseAndStudents practiceBaseAndStudents;
	private String practiceBaseName;
	
	public void setMajorName(String a){this.majorName=a==null||a.isEmpty()?null:a;}
	public String getMajorName(){return majorName;}
	public void setCheckBox(boolean[] a){this.checkBox=a;}
	public boolean[] getCheckBox(){return this.checkBox;}
	public ListOfPracticeBaseAndStudents getPracticeBaseAndStudents(){return this.practiceBaseAndStudents;}
	public String getPracticeBaseName(){return this.practiceBaseName;}
	public void setPracticeBaseName(String a){this.practiceBaseName=a;}
	
	private List<PracticeBase> practiceBases;
		public List<PracticeBase> getPracticeBases(){return this.practiceBases;}
		public Student getStudent(){return new Student();}
	

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
		Major major=null;
		try{
			major=new Major(this.majorName);
		}catch(IllegalArgumentException | SQLException e){
			return Manager.tips("专业("+this.majorName+")不存在！",NONE);
		}
		try {
			this.practiceBaseAndStudents=new ListOfPracticeBaseAndStudents(this.getAnnual().getYear(),
					major,/*containsNullPracticeBase*/true);
			//列出可用的PracticeBase列表（可用:=已分配至实习大区）
			this.practiceBases=new ArrayList<PracticeBase>();
			for(ListOfRegionAndPracticeBases.Pair p:
				new ListOfRegionAndPracticeBases(this.getAnnual().getYear(),/*containsNullRegion*/false)
				.getList())
				if(p.getPracticeBases()!=null)
					this.practiceBases.addAll(p.getPracticeBases());
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return Manager.tips("数据库开小差去了！",
					e,NONE);
		}
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
		ListOfPracticeBaseAndStudents.Pair pair=this.practiceBaseAndStudents.get(this.practiceBaseName);
		if(this.practiceBaseName==null || this.practiceBaseName.isEmpty() || pair==null || pair.getPlan()==null)
			return Manager.tips("请选择一个实习基地！",
					display());
		System.out.println(">> RegionArrangement:execute > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> RegionArrangement:execute > ]");
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
				//TODO 需要用Pair.getPlan()来check新student
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
		System.out.println(">> RegionArrangement:delete > checkBox=[");
		for(boolean s:checkBox) System.out.println(s);
		System.out.println(">> RegionArrangement:delete > ]");
		//RegionArrangement:execute
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
