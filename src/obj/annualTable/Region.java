package obj.annualTable;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

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
	@SQLField(value="入校时间",source="ZZMM.name")
	private Timestamp enterPracticeBaseTime;
	@SQLField(value="入校地点",source="Province.name")
	private String enterPracticeBasePlace;
	@SQLField(value="动员会时间")
	private Timestamp mobilizationTime;
	@SQLField(value="动员会地点",source="InnerPerson.id")
	private String mobilizationPlace;
	@SQLField(value="备注",ps="二进制储存")
	private String remark;


	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getPracticeBase() {return practiceBase;}
	public void setPracticeBase(String practiceBase) {this.practiceBase = practiceBase;}
	public String getLeaderId() {return leaderId;}
	public void setLeaderId(String leaderId) {this.leaderId = leaderId;}
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
		List<Field> fields=Base.getFields(this.getClass());
		Field[] fs=new Field[fields.size()];
		fs=fields.toArray(fs);
		this.update(fs);//调用下面那个方法
	}
	/**
	 * 需要同时修改所有该大区内容（包括year）
	 */
	@Override
	public void update(Field[] updateFields) throws IllegalArgumentException, IllegalAccessException, SQLException{
		Class<? extends Base> clazz=this.getClass();
		StringBuilder sb=new StringBuilder();
		sb.append("UPDATE ");
		sb.append(Base.getSQLTableName(clazz));
		sb.append(" SET ");
		boolean first=true;
		for(Field f:updateFields){
			if(first) first=false;
			else sb.append(" , ");
			sb.append(f.getName());
			sb.append(" = ?");
		}
		sb.append(" WHERE ");
		sb.append("year = ? AND name = ?");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		for(int i=0;i<updateFields.length;i++)
			pst.setObject(i+1,updateFields[i].get(this));
		pst.setObject(updateFields.length  ,this.getYear());
		pst.setObject(updateFields.length+1,this.getName());
		int num=pst.executeUpdate();
		System.out.println("+> Region:update > update "+num+" column!");
	}
	
	
	
}