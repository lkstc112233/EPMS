package obj.restraint;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.staticObject.InnerPerson;
import obj.staticSource.Major;
import obj.staticSource.School;

/**
 * 输入HardPart，对HardPart的约束进行强行限制
 * 一方面，查询时对HardPart的约束不能修改
 * 另一方面checkBase方法会对HardPart的部分进行检查和修正
 */
public class YearAndSchoolAndMajorRestraint extends HardRestraint{

	private List<Major> majors;
		public List<Major> getMajors(){return this.majors;}
	

	static public obj.Pair<List<Major>,List<Restraint.Part>>
	CreateRestraintList(JoinParam param,
				int year,School school) throws IllegalArgumentException, InstantiationException, SQLException {
		List<Major> majors;	
		if(school==null)
			majors=null;// which means 'school!=null'
		else
			majors=Base.list(Major.class,new Restraint(
					Field.getField(Major.class,"school"),school.getName()));
		List<Field> yearFields=new ArrayList<Field>();
		List<Field> schoolFields=new ArrayList<Field>();
		List<Field> majorFields=new ArrayList<Field>();
		List<Field> nameFields=school==null?null:new ArrayList<Field>();
		for(JoinParam.Part p:param.getList()){
			for(Field f:Field.getFields(p.getClazz())){
				if(f.getName().equals("year"))
					yearFields.add(f);
				else if(nameFields!=null && f.getName().equals("name"))
					nameFields.add(f);
				else if(f.source()!=null && majors!=null){// which means 'school!=null'
					Class<? extends Base> sourceClazz=f.source().getClazz();
					if(sourceClazz.equals(School.class))
						schoolFields.add(f);
					else if(sourceClazz.equals(Major.class))
						majorFields.add(f);
				}
			}
		}
		List<Restraint.Part> hps=new ArrayList<Restraint.Part>();
		for(Field f:yearFields)
			hps.add(new Restraint.Part(f,year));
		if(nameFields!=null)
			for(Field f:nameFields)
				hps.add(new Restraint.Part(f,Restraint.Type.NotLike,InnerPerson.UndefinedName));
		for(Field f:schoolFields)
			hps.add(new Restraint.Part(f,school.getName()));
		if(majors!=null){// which means 'school!=null'
			String[] majorsName=new String[majors.size()];
			for(int i=0;i<majorsName.length;i++) majorsName[i]=majors.get(i).getName();
			for(Field f:majorFields)
				hps.add(new Restraint.OrPart(f,majorsName));
		}
		return new obj.Pair<List<Major>,List<Restraint.Part>>(
				majors,hps);
	}
	public YearAndSchoolAndMajorRestraint(JoinParam param,int orderFieldsCount,
			obj.Pair<List<Major>,List<Restraint.Part>> p)throws IllegalArgumentException, InstantiationException, SQLException {
		super(param,orderFieldsCount,null);
		this.majors=p.getKey();
		Restraint.Part[] hardPart=new Restraint.Part[p.getValue().size()];
		for(int i=0;i<hardPart.length;i++) hardPart[i]=p.getValue().get(i);
		super.setupHardPart(hardPart);
	}
	public YearAndSchoolAndMajorRestraint(JoinParam param,int orderFieldsCount,
			int year,School school) throws IllegalArgumentException, InstantiationException, SQLException {
		super(param,orderFieldsCount,null);
		obj.Pair<List<Major>,List<Restraint.Part>> p=
				CreateRestraintList(param,year,school);
		this.majors=p.getKey();
		Restraint.Part[] hardPart=new Restraint.Part[p.getValue().size()];
		for(int i=0;i<hardPart.length;i++) hardPart[i]=p.getValue().get(i);
		super.setupHardPart(hardPart);
	}
	
	
	@Override
	public boolean checkBase(Base b,boolean setIfFalse) {
		return super.checkBase(b, setIfFalse);
	}
	
}