package action.function;

import java.sql.SQLException;
import java.util.*;

import com.opensymphony.xwork2.ActionSupport;

import action.Manager;
import obj.*;
import obj.annualTable.*;
import obj.staticObject.PracticeBase;
import obj.staticSource.Major;

/**
 * 分配媒体设备
 */
public class PlanMedia extends ActionSupport{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	
	private List<Major> majors;
	private ListOfRegionAndPracticeBases regionAndPracticeBase;
	private int[][][] numbers;
	private boolean[][][] media;
	private int[] mediaMajorCounts;
	private int[][] mediaPracticeBaseCounts;
	static public final String SessionMajorsKey="PlanAllDesign_Majors";
	static public final String SessionListKey="PlanAllDesign_List";

	
	public ListOfRegionAndPracticeBases getRegionAndPracticeBase(){return this.regionAndPracticeBase;}
	public int[][][] getNumbers(){
		if(this.majors==null || this.regionAndPracticeBase==null)
			return this.numbers=null;
		if(this.numbers!=null) return this.numbers;
		this.numbers=new int[this.majors.size()]
				[this.regionAndPracticeBase.getList().size()][];
		for(int i=0;i<this.majors.size();i++)
			for(int j=0;j<this.regionAndPracticeBase.getList().size();j++)
				this.numbers[i][j]=new int[this.regionAndPracticeBase.getList().get(j).getPracticeBases().size()];
		Map<String,Integer> majorsMap=new HashMap<String,Integer>();
		for(int i=0;i<this.majors.size();i++)
			majorsMap.put(this.majors.get(i).getName(),i);
		List<Plan> plans;
		try {
			plans=Base.list(Plan.class,new Restraint(
					Field.getField(Plan.class,"year"),
					this.getAnnual().getYear()));
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return this.numbers=null;
		}
		for(Plan p:plans){
			int[] index=this.regionAndPracticeBase.indexOf(p.getPracticeBase());
			if(index!=null && index.length>=2)
				this.numbers[majorsMap.get(p.getMajor())][index[0]][index[1]]=
				p.getNumber();
		}
		return this.numbers;
	}
	public boolean[][][] getMedia() {
		if(this.getNumbers()==null) return this.media=null;
		if(this.media!=null) return this.media;
		this.media=new boolean[this.numbers.length][][];
		for(int i=0;i<this.media.length;i++) {
			this.media[i]=new boolean[this.numbers[i].length][];
			for(int j=0;j<this.media[i].length;j++) {
				this.media[i][j]=new boolean[this.numbers[i][j].length];
				for(int k=0;k<this.media[i][j].length;k++)
					this.media[i][j][k]=false;
			}
		}
		Map<String,Integer> majorsMap=new HashMap<String,Integer>();
		for(int i=0;i<this.majors.size();i++)
			majorsMap.put(this.majors.get(i).getName(),i);
		List<Plan> plans;
		try {
			plans=Base.list(Plan.class,new Restraint(
					Field.getField(Plan.class,"year"),
					this.getAnnual().getYear()));
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return this.media=null;
		}
		for(Plan p:plans){
			int[] index=this.regionAndPracticeBase.indexOf(p.getPracticeBase());
			if(index!=null && index.length>=2)
				this.media[majorsMap.get(p.getMajor())][index[0]][index[1]]=
				p.getMedia();
		}
		return this.media;
	}
	public int getMediaCount() {
		int ans=0;
		for(int x:this.getMediaMajorCounts())
			ans+=x;
		return ans;
	}
	public int[] getMediaMajorCounts() {
		if(this.getNumbers()==null) return this.mediaMajorCounts=null;
		if(this.mediaMajorCounts!=null) return this.mediaMajorCounts;
		this.mediaMajorCounts=new int[this.media.length];
		for(int i=0;i<this.mediaMajorCounts.length;i++) {
			this.mediaMajorCounts[i]=0;
			for(int j=0;j<this.media[i].length;j++)
				for(int k=0;k<this.media[i][j].length;k++)
					if(this.media[i][j][k])
						this.mediaMajorCounts[i]++;
		}
		return this.mediaMajorCounts;
	}
	public int[][] getMediaPracticeBaseCounts() {
		if(this.getNumbers()==null) return this.mediaPracticeBaseCounts=null;
		if(this.mediaPracticeBaseCounts!=null) return this.mediaPracticeBaseCounts;
		this.mediaPracticeBaseCounts=new int[this.media[0].length][];
		for(int i=0;i<this.mediaPracticeBaseCounts.length;i++) {
			this.mediaPracticeBaseCounts[i]=new int[this.media[0][i].length];
			for(int j=0;j<this.mediaPracticeBaseCounts[i].length;j++)
				for(int k=0;k<this.media.length;k++)
					if(this.media[k][i][j])
						this.mediaPracticeBaseCounts[i][j]++;
		}
		return this.mediaPracticeBaseCounts;
	}
	public List<Major> getMajors(){
		if(this.majors==null) try {
			return this.majors=Base.list(Major.class);
		}catch (SQLException | IllegalArgumentException | InstantiationException e){
			e.printStackTrace();
			this.majors=null;
		}return this.majors;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public PlanMedia(){
		super();
		System.out.println(">> PlanAllDesign:constructor > year="+this.getAnnual().getYear());
		this.regionAndPracticeBase=Manager.loadSession(ListOfRegionAndPracticeBases.class, SessionListKey);
		this.majors=Manager.loadSession(List.class, SessionMajorsKey);
		this.getNumbers();
	}
	
	public String display(){
		try {
			this.regionAndPracticeBase=new ListOfRegionAndPracticeBases(this.getAnnual().getYear(),/*containsNullRegion*/false);
		} catch (SQLException | IllegalArgumentException | InstantiationException e) {
			return Manager.tips("数据库读取实习基地及大区信息失败",e,ERROR);
		}
		if(this.getMajors()==null)
			return Manager.tips("数据库读取专业列表失败！",ERROR);
		if(this.getNumbers()==null)
			return Manager.tips("数据库读取布局规划失败！",ERROR);
		if(this.getMedia()==null)
			return Manager.tips("数据库读取数字媒体设备规划失败！",ERROR);
		if(this.regionAndPracticeBase!=null)
			Manager.saveSession(SessionListKey,this.regionAndPracticeBase);
		if(this.majors!=null)
			Manager.saveSession(SessionMajorsKey,this.majors);
		return NONE;
	}
	
	/**
	 * 自动填充
	 */
	@Override
	public String execute(){
	//	StringBuilder error=new StringBuilder();
		if(this.regionAndPracticeBase==null||this.numbers==null||this.majors==null)
			return Manager.tips("该项目不可用!",display());
		return Manager.tips("该项目不可用!",display());
	}


	private int clickIndex[]=new int[] {-1,-1,-1};
	/**
	 * 单击
	 */
	public String create(){
		if(this.regionAndPracticeBase==null||this.numbers==null||this.majors==null)
			return Manager.tips("该项目不可用!",display());
		if(clickIndex==null || clickIndex.length<3)
			clickIndex=new int[] {-1,-1,-1};
		try {
			Major major=this.getMajors().get(clickIndex[0]);
			PracticeBase pb=this.regionAndPracticeBase.getList().get(clickIndex[1])
					.getPracticeBases().get(clickIndex[2]);
			if(this.numbers[clickIndex[0]][clickIndex[1]][clickIndex[2]]<=0)
				return Manager.tips(major.getDescription()+"至"+pb.getDescription()+"无派遣计划!",NONE);
			Plan p=new Plan();
			p.setYear(this.getAnnual().getYear());
			p.setMajor(major.getName());
			p.setPracticeBase(pb.getName());
			try {
				if(p.existAndLoad()){
					p.setMedia(!p.getMedia());
					p.update();
				}
			} catch (IllegalArgumentException | SQLException e) {
				return Manager.tips("数据库开小差去了，修改失败!",e,NONE);
			}
		}catch(IndexOutOfBoundsException e) {
			return Manager.tips("点击错误!",e,NONE);
		}catch(NullPointerException e) {
			return Manager.tips("读取错误!",e,NONE);
		}
		return Manager.tips("修改成功！",display());
	}

	
}
