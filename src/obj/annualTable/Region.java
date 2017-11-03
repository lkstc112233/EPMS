package obj.annualTable;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import obj.ListableBase.ListableBaseWithNoSave;
import persistence.DB;
import obj.*;

@SQLTable("Region")
public class Region extends AnnualBase implements ListableBaseWithNoSave{
	
	@SQLField(value="大区名称",isKey=true,ps="大区名称不能重复")
	private String name;
	@SQLField(value="实习基地",isKey=true,source="PracticeBase.name")
	private String practiceBase;
	@SQLField(value="总领队工号",source="InnerPerson.id")
	private String leaderId;
	@SQLField(value="入校时间")
	private Timestamp enterPracticeBaseTime;
	@SQLField(value="入校地点")
	private String enterPracticeBasePlace;
	@SQLField(value="动员会时间")
	private Timestamp mobilizationTime;
	@SQLField(value="动员会地点")
	private String mobilizationPlace;
	@SQLField(value="备注",ps="文本储存")
	private String remark;


	public String getName() {return name;}
	public void setName(String name) {this.name = name==null||name.isEmpty()?null:name;}
	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String practiceBase) {this.practiceBase = practiceBase==null||practiceBase.isEmpty()?null:practiceBase;}
	public String getLeaderId() {return leaderId;}
	public void setLeaderId(String leaderId) {this.leaderId = leaderId==null||leaderId.isEmpty()?null:leaderId;}
	public Timestamp getEnterPracticeBaseTime() {return enterPracticeBaseTime;}
	public void setEnterPracticeBaseTime(Timestamp enterPracticeBaseTime) {this.enterPracticeBaseTime = enterPracticeBaseTime;}
	public String getEnterPracticeBasePlace() {return enterPracticeBasePlace;}
	public void setEnterPracticeBasePlace(String enterPracticeBasePlace) {this.enterPracticeBasePlace = enterPracticeBasePlace;}
	public Timestamp getMobilizationTime() {return mobilizationTime;}
	public void setMobilizationTime(Timestamp mobilizationTime) {this.mobilizationTime = mobilizationTime;}
	public String getMobilizationPlace() {return mobilizationPlace;}
	public void setMobilizationPlace(String mobilizationPlace) {this.mobilizationPlace=mobilizationPlace;}
	public String getRemark() {return remark;}
	public void setRemark(String remark) {this.remark = remark;}


	
	public Region() throws SQLException {
		super();
	}
	
	
	
	
	static public Region LoadOneRegionByName(String regionName) throws SQLException, IllegalArgumentException, IllegalAccessException{
		List<Field> fs=Base.getFields(Region.class);
		Region r=new Region();
		r.setName(regionName);
		StringBuilder sb=new StringBuilder();
		sb.append("SELECT ");
		boolean first=true;
		for(Field f:fs){
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
		}
		sb.append(" FROM ");
		sb.append(Base.getSQLTableName(Region.class));
		sb.append(" WHERE name = ?");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		pst.setObject(1,regionName);
		ResultSet res=pst.executeQuery();
		while(res.next()){
			for(Field f:fs){
				f.setAccessible(true);
				Object o=res.getObject(f.getName());
				f.set(r,o);
			}
			return r;
		}
		return null;
	}
	
	
	
	
	/**
	 * 需要同时修改所有该大区内容
	 */
	@Override
	public void update() throws IllegalArgumentException, IllegalAccessException, SQLException{
		Class<? extends Base> clazz=this.getClass();
		List<Field> fs=Base.getFields(clazz);
		StringBuilder sb=new StringBuilder();
		sb.append("UPDATE ");
		sb.append(Base.getSQLTableName(clazz));
		sb.append(" SET ");
		boolean first=true;
		for(Field f:fs){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null || s.isKey()) continue;
			if(f.getName().equals("practiceBase")) continue;
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
			sb.append(" = ?");
		}
		sb.append(" WHERE ");
		sb.append("year = ? AND name = ?");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int parameterIndex=1;
		for(Field f:fs){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null || s.isKey()) continue;
			f.setAccessible(true);
			pst.setObject(parameterIndex++,f.get(this));
		}
		pst.setObject(parameterIndex++,this.getYear());
		pst.setObject(parameterIndex++,this.getName());
		int num=pst.executeUpdate();
		System.out.println("+> Region:update > update "+num+" column!");
	}
	/**
	 * 需要同时修改所有该大区内容（包括year）
	 */
	@Override
	public void update(Base r) throws IllegalArgumentException, IllegalAccessException, SQLException{
		if(r==null) return;
		if(!r.getClass().equals(this.getClass()))
			throw new IllegalArgumentException("类型不同！");
		Class<? extends Base> clazz=this.getClass();
		List<Field> fs=Base.getFields(clazz);
		StringBuilder sb=new StringBuilder();
		sb.append("UPDATE ");
		sb.append(Base.getSQLTableName(clazz));
		sb.append(" SET ");
		boolean first=true;
		for(Field f:fs){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			if(f.getName().equals("practiceBase")) continue;
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
			sb.append(" = ?");
		}
		sb.append(" WHERE ");
		sb.append("year = ? AND name = ?");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int parameterIndex=1;
		for(Field f:fs){
			SQLField s=f.getAnnotation(SQLField.class);
			if(s==null) continue;
			if(f.getName().equals("practiceBase")) continue;
			f.setAccessible(true);
			Object o=f.get(r);
			pst.setObject(parameterIndex++,o);
		}
		pst.setObject(parameterIndex++,this.getYear());
		pst.setObject(parameterIndex++,this.getName());
		int num=pst.executeUpdate();
		System.out.println("+> Region:update(Base) > update "+num+" column!");
		r.copyTo(this);
	}
	
	
	
	

	
}