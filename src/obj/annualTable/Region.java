package obj.annualTable;

import java.sql.*;

import persistence.DB;
import obj.*;

@SQLTable("Region")
public class Region extends AnnualBase{
	
	@SQLField(value="大区名称",weight=1,isKey=true,notNull=true,ps="大区名称不能重复")
	private String name;
	@SQLField(value="实习基地",weight=2,isKey=true,notNull=true,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="学生大组长学号",weight=3,source="Student.id")
	private String studentGroupLeaderId;
	@SQLField(value="总领队工号",weight=10,source="InnerPerson.id")
	private String leaderId;
	@SQLField(value="入校时间",weight=11)
	private Timestamp enterPracticeBaseTime;
	@SQLField(value="入校地点",weight=12)
	private String enterPracticeBasePlace;
	@SQLField(value="动员会时间",weight=13)
	private Timestamp mobilizationTime;
	@SQLField(value="动员会地点",weight=14)
	private String mobilizationPlace;
	@SQLField(value="备注",weight=15,ps="文本储存")
	private String remark;


	public String getName() {return name;}
	public void setName(String a) {this.name=Field.s2S(a);}
	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String a) {this.practiceBase=Field.s2S(a);}
	public String getStudentGroupLeaderId() {return studentGroupLeaderId;}
	public void setStudentGroupLeaderId(String a) {this.studentGroupLeaderId=Field.s2S(a);}
	public String getLeaderId() {return leaderId;}
	public void setLeaderId(String a) {this.leaderId=Field.s2S(a);}
	public Timestamp getEnterPracticeBaseTime() {return enterPracticeBaseTime;}
	public void setEnterPracticeBaseTime(Timestamp enterPracticeBaseTime) {this.enterPracticeBaseTime = enterPracticeBaseTime;}
	public void setEnterPracticeBaseTime(String a) {this.enterPracticeBaseTime=Field.s2TS(a);}
	public String getEnterPracticeBasePlace() {return enterPracticeBasePlace;}
	public void setEnterPracticeBasePlace(String enterPracticeBasePlace) {this.enterPracticeBasePlace = enterPracticeBasePlace;}
	public Timestamp getMobilizationTime() {return mobilizationTime;}
	public void setMobilizationTime(Timestamp mobilizationTime) {this.mobilizationTime = mobilizationTime;}
	public void setMobilizationTime(String a) {this.mobilizationTime=Field.s2TS(a);}
	public String getMobilizationPlace() {return mobilizationPlace;}
	public void setMobilizationPlace(String mobilizationPlace) {this.mobilizationPlace=mobilizationPlace;}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}

	
	public Region() {
		super();
	}
	
	
	static public Region LoadOneRegionByPracticeBaseName(String practiceBaseName,int year) throws SQLException, IllegalArgumentException, IllegalAccessException{
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT ");
		boolean first=true;
		for(Field f:Field.getFields(Region.class)){
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
		}
		sb.append(" FROM ");
		sb.append(Base.getSQLTableName(Region.class));
		sb.append(" WHERE practiceBase = ? AND year = ?");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		pst.setObject(1,practiceBaseName);
		pst.setObject(2,year);
		ResultSet res=pst.executeQuery();
		Region r=new Region();
		r.setPracticeBase(practiceBaseName);
		while(res.next()){
			for(Field f:Field.getFields(Region.class))
				f.set(r,res.getObject(f.getName()));
			return r;
		}
		return null;
	}
	
	
	
	static public Region LoadOneRegionByName(String regionName,int year) throws SQLException, IllegalArgumentException, IllegalAccessException{
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT ");
		boolean first=true;
		for(Field f:Field.getFields(Region.class)){
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
		}
		sb.append(" FROM ");
		sb.append(Base.getSQLTableName(Region.class));
		sb.append(" WHERE name = ? AND year = ? LIMIT 1");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		pst.setObject(1,regionName);
		pst.setObject(2,year);
		ResultSet res=pst.executeQuery();
		Region r=new Region();
		r.setName(regionName);
		while(res.next()){
			for(Field f:Field.getFields(Region.class))
				f.set(r,res.getObject(f.getName()));
			return r;
		}
		return null;
	}
	
	
	/**
	 * 需要同时修改所有该大区内容（包括year）
	 */
	@Override
	public void update(Base region) throws IllegalArgumentException, SQLException{
		if(region==null) return;
		if(!region.getClass().equals(this.getClass()))
			throw new IllegalArgumentException("类型不同！");
		StringBuilder sb=new StringBuilder();
		sb.append("UPDATE ");
		sb.append(this.getSQLTableName());
		sb.append(" SET ");
		boolean first=true;
		for(Field f:this.getFields()){
			if(f.getName().equals("practiceBase")) continue;
			if(f.getName().equals("studentGroupLeaderId")) continue;
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
			sb.append(" = ?");
		}
		sb.append(" WHERE ");
		sb.append("year = ? AND name = ?");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int parameterIndex=1;
		for(Field f:this.getFields()){
			if(f.getName().equals("practiceBase")) continue;
			pst.setObject(parameterIndex++,f.get(region));
		}
		pst.setObject(parameterIndex++,this.getYear());
		pst.setObject(parameterIndex++,this.getName());
		int num=pst.executeUpdate();
		System.out.println("+> Region:update(Base) > update "+num+" column!");
		region.copyTo(this);
	}
	
	
	
	
	
	
	@Override
	public String getDescription() {
		return this.name;
	}
	

	
}