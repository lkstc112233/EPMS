package action.jwc;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.ListableBase;
import obj.annualTable.*;
import obj.staticSource.Major;

public class PlanAllDesign extends ActionSupport{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private List<Major> majors;
	private ListOfRegionAndPracticeBases regionAndPracticeBase;
	private String[][][] numbers;
	static public final String SessionMajorsKey="PlanAllDesign_Majors";
	static public final String SessionListKey="PlanAllDesign_List";

	
	public List<Major> getMajors(){return this.majors;}
	public ListOfRegionAndPracticeBases getRegionAndPracticeBase(){return this.regionAndPracticeBase;}
	public String[][][] getNumbers(){return this.numbers;}
	public void setNumbers(String[][][] numbers){this.numbers=numbers;}

	@SuppressWarnings("unchecked")
	public PlanAllDesign(){
		super();
		System.out.println(">> PlanAllDesign:constructor > year="+this.getAnnual().getYear());
		if(!this.annual.checkYear()){
			System.err.println(">> PlanAllDesign:constructor > year has been setup!");
			this.annual.setupYear();
		}
		this.majors=Manager.loadSession(List.class,SessionMajorsKey);
		this.regionAndPracticeBase=Manager.loadSession(ListOfRegionAndPracticeBases.class, SessionListKey);
		this.setupNumbers();
	}
	private void setupNumbers(){
		if(this.majors==null || this.regionAndPracticeBase==null)
			return;
		this.numbers=new String[this.majors.size()]
				[this.regionAndPracticeBase.getList().size()][];
		for(int i=0;i<this.majors.size();i++)
			for(int j=0;j<this.regionAndPracticeBase.getList().size();j++)
				this.numbers[i][j]=new String[this.regionAndPracticeBase.getList().get(j).getPracticeBases().size()];
	}
	
	public String display(){
		try {
			this.regionAndPracticeBase=new ListOfRegionAndPracticeBases(this.getAnnual().getYear(),/*containsNullRegion*/false);
		} catch (SQLException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			return Manager.tips("数据库读取实习基地及大区信息失败",
					e,NONE);
		}
		try {
			this.majors=ListableBase.list(Major.class);
		} catch (SQLException e) {
			return Manager.tips("数据库读取专业列表失败！",
					e,NONE);
		}
		this.setupNumbers();
		Map<String,Integer> majorsMap=new HashMap<String,Integer>();
		for(int i=0;i<this.majors.size();i++)
			majorsMap.put(this.majors.get(i).getName(),i);
		List<Plan> plans;
		try {
			plans = Plan.list(Plan.class,
					new String[]{"year"},
					new Object[]{Integer.valueOf(this.annual.getYear())});
		} catch (NoSuchFieldException | SQLException e) {
			return Manager.tips("服务器读取布局规划失败！",
					e,NONE);
		}
		for(Plan p:plans){
			int[] index=this.regionAndPracticeBase.indexOf(p.getPracticeBase());
			if(index!=null && index.length>=2)
				this.numbers[majorsMap.get(p.getMajor())][index[0]][index[1]] = String.valueOf(p.getNumber());
		}
		if(this.regionAndPracticeBase!=null)
			Manager.saveSession(SessionListKey,this.regionAndPracticeBase);
		if(this.majors!=null)
			Manager.saveSession(SessionMajorsKey,this.majors);
		return NONE;
	}
	
	@Override
	public String execute(){
		boolean ok=false;
		StringBuilder error=new StringBuilder();
		if(this.regionAndPracticeBase==null||this.numbers==null||this.majors==null)
			return display();
		for(int i=0;i<this.numbers.length;i++){
			for(int j=0;j<this.numbers[i].length;j++){
				for(int k=0;k<this.numbers[i][j].length;k++){
					if(this.numbers[i][j][k]==null || this.numbers[i][j][k].isEmpty())
						continue;
					int num;
					try{
						num=Integer.valueOf(this.numbers[i][j][k]);
					}catch(NumberFormatException e){
						e.printStackTrace();
						error.append("\n("+i+","+j+","+k+")"+e.getMessage());
						continue;
					}
					if(num<0) num=0;
					Plan p;
					try {
						p = new Plan();
					} catch (SQLException e) {
						e.printStackTrace();
						error.append("\n("+i+","+j+","+k+")"+e.getMessage());
						continue;
					}
					p.setYear(this.getAnnual().getYear());
					p.setMajor(this.majors.get(i).getName());
					p.setPracticeBase(this.regionAndPracticeBase.getList().get(j).getPracticeBases().get(k).getName());
					try {
						if(p.existAndLoad()){
							if(num==0)
								p.delete();
							else{
								p.setNumber(num);
								p.update();
							}
						}else{
							p.setNumber(num);
							p.create();
						}
						ok=true;
					} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
						e.printStackTrace();
						error.append("\n("+i+","+j+","+k+")"+e.getMessage());
						continue;
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
