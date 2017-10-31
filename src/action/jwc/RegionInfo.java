package action.jwc;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import obj.annualTable.Region;
import obj.staticObject.InnerPerson;

/**
 * 导入免费师范生数据
 */
public class RegionInfo extends ActionSupport{
	private static final long serialVersionUID = 5998268336475528662L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}

	
	private Region region;
	private List<InnerPerson> innerPersons;
	private String newRegionName=null;
	
	public Region getRegion(){return this.region;}
	public List<InnerPerson> getInnerPersons(){return this.innerPersons;}
	public String getNewRegionName(){return this.newRegionName;}
	public void setNewRegionName(String a){this.newRegionName=a;}
	
	public RegionInfo() throws SQLException, NoSuchFieldException, SecurityException{
		super();
		System.out.println(">> RegionInfo_info:constructor > year="+this.getAnnual().getYear());
		this.region=new Region();
	}
	
	@Override
	public String execute(){
		if(this.region==null)
			return display();
		Map<String, Object> session=ActionContext.getContext().getSession();
		System.out.println(">> RegionInfo_info:execute > region= "+this.region);
		try {
			Region tmp=new Region();
			this.region.copyTo(tmp);
			if(newRegionName!=null || !newRegionName.isEmpty())
				tmp.setName(this.newRegionName);
			this.region.update(tmp);
		} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开了点小差！");
			return display();
		}
		session.put(token.ActionInterceptor.ErrorTipsName,
				"修改成功！");
		return display();
	}
		
	/**
	 * 用于显示
	 */
	public String display(){
		Map<String, Object> session=ActionContext.getContext().getSession();
		try {
			this.innerPersons=InnerPerson.list(InnerPerson.class);
		} catch (SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"数据库读取校内人员列表失败！");
			return NONE;
		}
		if(this.region==null || this.region.getName()==null || this.region.getName().isEmpty()){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"大区名称不正确！");
			return NONE;
		}
		String regionName=region.getName();
		try {
			region=Region.LoadOneRegionByName(regionName);
		} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
			session.put(token.ActionInterceptor.ErrorTipsName,
					"服务器开了点小差！");
			return NONE;
		}
		if(region==null){
			session.put(token.ActionInterceptor.ErrorTipsName,
					"不存在大区名为"+regionName+"！");
			return NONE;
		}
		System.out.println(">> RegionInfo_info:display > region="+this.region);
		System.out.println(">> RegionInfo_info:display <NONE");
		return NONE;
	}
	
	
}
