package obj.annualTable;

import java.sql.*;
import java.util.List;

import persistence.DB;
import obj.*;
import obj.staticObject.PracticeBase;

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
	public void setEnterPracticeBasePlace(String a) {this.enterPracticeBasePlace=Field.s2S(a);}
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
	
	
	public Boolean getHX() {
		try {
			return new PracticeBase(this.getPracticeBase()).getHx();
		}catch(SQLException | IllegalArgumentException | NullPointerException e) {
			e.printStackTrace();
		}return null;
	}
	/**
	 * 需要检查当前大区包含的实习基地是否和新加入的实习基地有冲突<br/>
	 * 冲突是指：<br/>
	 * 1. 实习基地hx属性不一致（一个大区不能既包含回乡实习基地又包含北京附近实习基地）<br/>
	 * 2. 实习基地hx均为true时，province不一致（一个大区是回乡实习基地大区时，必须在同一省份）<br/>
	 */
	@Override
	public void create() throws IllegalArgumentException, SQLException, IllegalAccessException{
		if(!this.checkNotNullField())
			throw new IllegalArgumentException("The notNull fields are not completed!");
		PracticeBase pb=new PracticeBase(this.getPracticeBase());
		if(this.checkPracticeBase(pb))
			super.create();
	}
	/**
	 * 检查实习基地是否能放入当前大区<br/>
	 * 检查当前大区包含的实习基地是否和新加入的实习基地有冲突<br/>
	 * 冲突是指：<br/>
	 * 1. 实习基地hx属性不一致（一个大区不能既包含回乡实习基地又包含北京附近实习基地）<br/>
	 * 2. 实习基地hx均为true时，province不一致（一个大区是回乡实习基地大区时，必须在同一省份）<br/>
	 * @return 只会返回true
	 * @throws IllegalArgumentException 错误信息
	 */
	public boolean checkPracticeBase(PracticeBase pb) throws IllegalArgumentException{
		if(pb==null) throw new IllegalArgumentException("实习基地为空！");
		List<Base[]> tmp;
		try {
			tmp = Base.list(new JoinParam(PracticeBase.class)
					.append(JoinParam.Type.InnerJoin,Region.class,
							Field.getField(Region.class,"practiceBase"),
							Field.getField(PracticeBase.class,"name"),
							Field.getFields(Region.class,"year","name"),
							new Object[] {this.getYear(),this.getName()})
					);
		} catch (InstantiationException | SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		for(Base[] bs:tmp) if(bs!=null && bs.length>=2 && bs[0]!=null){
			PracticeBase p=(PracticeBase)bs[0];
			if(pb.getHx() ^ p.getHx())
				throw new IllegalArgumentException("与\""+p.getDescription()+"\"回生源地规划不同!");
			if(pb.getHx()) {
				if(!pb.getProvince().equals(p.getProvince()))
					throw new IllegalArgumentException("与\""+p.getDescription()+"\"所属地区不同!");
			}
		}
		return true;
	}
	
	/**
	 * 需要同时修改所有该大区内容（包括year）
	 */
	@Override
	public void update(Base region) throws IllegalArgumentException, SQLException{
		if(region==null) return;
		if(!region.getClass().equals(this.getClass()))
			throw new IllegalArgumentException("类型不同！");
		//先更新studentGroupLearderId
		if(((Region)region).getStudentGroupLeaderId()==null&&this.getStudentGroupLeaderId()!=null
				||!((Region)region).getStudentGroupLeaderId().equals(this.getStudentGroupLeaderId())) {
			this.setStudentGroupLeaderId(((Region)region).getStudentGroupLeaderId());
			this.updateStudentGroupLeaderId();
		}
		//再更新其他的
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
			if(f.getName().equals("studentGroupLeaderId")) continue;
			pst.setObject(parameterIndex++,f.get(region));
		}
		pst.setObject(parameterIndex++,this.getYear());
		pst.setObject(parameterIndex++,this.getName());
		int num=pst.executeUpdate();
		System.err.println("Region:update更新了"+num+"重值！("+pst.toString()+")");
		region.copyTo(this);
	}
	
	
	public void updateStudentGroupLeaderId() throws SQLException {
		StringBuilder sb=new StringBuilder();
		sb.append("UPDATE ");
		sb.append(this.getSQLTableName());
		sb.append(" SET ");
		sb.append("studentGroupLeaderId = ?");
		sb.append(" WHERE ");
		sb.append("year = ? AND name = ? AND practiceBase = ?");
		PreparedStatement pst=DB.con().prepareStatement(sb.toString());
		int parameterIndex=1;
		pst.setObject(parameterIndex++,this.getStudentGroupLeaderId());
		pst.setObject(parameterIndex++,this.getYear());
		pst.setObject(parameterIndex++,this.getName());
		pst.setObject(parameterIndex++,this.getPracticeBase());
		int num=pst.executeUpdate();
		System.err.println("Region:update更新了"+num+"重值！("+pst.toString()+")");
	}
	
	
	
	
	
	@Override
	public String getDescription() {
		return this.name;
	}
	

	
}