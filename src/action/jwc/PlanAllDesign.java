package action.jwc;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.ListableBase;
import obj.annualTable.Plan;
import obj.annualTable.Region;
import obj.staticObject.PracticeBase;
import obj.staticSource.Major;

public class PlanAllDesign extends ActionSupport{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private List<Major> majors;
	private List<PracticeBase> practiceBaseWithARegion;
	private String[][] numbers;
	
	public List<Major> getMajors(){return this.majors;}
	public List<PracticeBase> getPracticeBaseWithARegion(){return this.practiceBaseWithARegion;}
	public String[][] getNumbers(){return this.numbers;}
	public void setNumbers(String[][] numbers){this.numbers=numbers;}

	public PlanAllDesign() throws SQLException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InstantiationException {
		super();
		System.out.println(">> PlanAllDesign:constructor > year="+this.getAnnual().getYear());
		this.majors=ListableBase.list(Major.class);
		if(!this.annual.checkYear()){
			System.err.println(">> PlanAllDesign:constructor > year has been setup!");
			this.annual.setupYear();
		}
		this.practiceBaseWithARegion=Region.listPracticeBasesWhichHaveARegion(this.annual.getYear());
		this.numbers=new String[this.practiceBaseWithARegion.size()][this.majors.size()];
		this.numbers[0][0]="x";
	}
	
	
	public String display(){
		Map<String,Integer> majorsMap=new HashMap<String,Integer>();
		for(int i=0;i<this.majors.size();i++)
			majorsMap.put(this.majors.get(i).getName(),i);
		Map<String,Integer> practiceBasesMap=new HashMap<String,Integer>();
		for(int i=0;i<this.practiceBaseWithARegion.size();i++)
			practiceBasesMap.put(this.practiceBaseWithARegion.get(i).getName(),i);
		List<Plan> plans;
		try {
			plans = Plan.list(Plan.class,
					new String[]{"year"},
					new Object[]{Integer.valueOf(this.annual.getYear())});
		} catch (NoSuchFieldException | SQLException e) {
			return Manager.tips("服务器开小差去了！",
					e,NONE);
		}
		for(Plan p:plans)
			this.numbers[practiceBasesMap.get(p.getPracticeBase())]
					[majorsMap.get(p.getMajor())] = String.valueOf(p.getNumber());
		return NONE;
	}
	
	@Override
	public String execute(){
		boolean ok=false;
		StringBuilder error=new StringBuilder();
		for(int i=0;i<this.numbers.length;i++){
			for(int j=0;j<this.numbers[i].length;j++){
				if(this.numbers[i][j]==null || this.numbers[i][j].isEmpty())
					continue;
				int num;
				try{
					num=Integer.valueOf(this.numbers[i][j]);
				}catch(NumberFormatException e){
					e.printStackTrace();
					error.append("\n("+i+","+j+")"+e.getMessage());
					continue;
				}
				if(num<0) num=0;
				Plan p;
				try {
					p = new Plan();
				} catch (SQLException e) {
					e.printStackTrace();
					error.append("\n("+i+","+j+")"+e.getMessage());
					continue;
				}
				p.setYear(this.getAnnual().getYear());
				p.setPracticeBase(this.practiceBaseWithARegion.get(i).getName());
				p.setMajor(this.majors.get(j).getName());
				try {
					if(p.existAndLoad()){
						p.setNumber(num);
						p.update();
					}else{
						p.setNumber(num);
						p.create();
					}
					ok=true;
				} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
					e.printStackTrace();
					error.append("\n("+i+","+j+")"+e.getMessage());
					continue;
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
