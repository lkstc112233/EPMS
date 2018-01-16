package action.function;

import java.sql.SQLException;
import java.util.*;

import action.Manager;
import obj.*;
import obj.annualTable.Student;
import obj.staticObject.PracticeBase;

public class StudentImport extends action.TableOperationAction{
	private static final long serialVersionUID = 8833385464572061925L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}

	
	private Set<String> provinceWithPracticeBase;
		public Set<String> getProvinceWithPracticeBase(){
			if(provinceWithPracticeBase!=null) return provinceWithPracticeBase;
			this.provinceWithPracticeBase=new HashSet<String>();
			try {
				for(PracticeBase pb:Base.list(PracticeBase.class,new Restraint(
						Field.getFields(PracticeBase.class,"status","hx"),
						new Object[] {true,true})))
					this.provinceWithPracticeBase.add(pb.getProvince());
			} catch (IllegalArgumentException | InstantiationException | SQLException e) {
				e.printStackTrace();
				return this.provinceWithPracticeBase=null;
			}
			return this.provinceWithPracticeBase;
		}
	
	public StudentImport(){
		super();
		this.getProvinceWithPracticeBase();
	}
	
	@Override
	protected Search createSearch() throws Exception {
		JoinParam param=new JoinParam(obj.annualTable.Student.class);
		obj.staticObject.InnerPerson user=Manager.getUser();
		token.Role role=token.Role.getRole(user);
		return new Search(param,new obj.restraint.YearAndSchoolAndMajorRestraint(param,3,
				this.getAnnual().getYear(),
				role==token.Role.jwc ? null : new obj.staticSource.School(user.getSchool())
						));
	}

	@Override
	protected Field[] refuseDisplayField() {
		return Field.getFields(Student.class,
				//	"year",
				//	"id","name","mobile","email",
				//	"major",
					"practiceBase",
				//	"sfzh","zzmm","province","nation",
					"hxyx",
					"recommend","teacherId",
					"outstandingType","outstandingMaterial",
					"status"
					);
	}
	
	@Override
	public String updateBase(Base b) throws InstantiationException, IllegalArgumentException, IllegalAccessException, SQLException {
		if(b==null) return "未找到!";
		if(b.exist()) {
			b.update();
			return "更新成功!";
		}else {
			//需要新建
			Student stu=(Student)b;
			if(this.getProvinceWithPracticeBase().contains(stu.getProvince()))
				stu.setHxyx(true);
			else
				stu.setHxyx(false);
			b.create();
			return "添加成功!";
		}
	}
}
