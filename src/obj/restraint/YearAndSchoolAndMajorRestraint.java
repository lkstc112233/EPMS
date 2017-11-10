package obj.restraint;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.staticSource.Major;
import obj.staticSource.School;

/**
 * 输入HardPart，对HardPart的约束进行强行限制
 * 一方面，查询时对HardPart的约束不能修改
 * 另一方面checkBase方法会对HardPart的部分进行检查和修正
 */
public class YearAndSchoolAndMajorRestraint extends HardRestraint{

	private boolean[] isHardPart;
		public boolean[] getIsHeardPart(){return this.isHardPart;}
	private List<Major> majors;
		public List<Major> getMajors(){return this.majors;}
	
	public YearAndSchoolAndMajorRestraint(JoinParam param,int orderFieldsCount,
			int year,School school) throws IllegalArgumentException, InstantiationException, SQLException {
		super(param,orderFieldsCount,null);
		this.majors=Base.list(Major.class,new Restraint(
				Field.getField(Major.class,"school"),school.getName()));
		List<Field> yearFields=new ArrayList<Field>();
		List<Field> schoolFields=new ArrayList<Field>();
		List<Field> majorFields=new ArrayList<Field>();
		for(JoinParam.Part p:param.getList()){
			for(Field f:p.getFields()){
				if(f.getName().equals("year"))
					yearFields.add(f);
				else{
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
		for(Field f:schoolFields)
			hps.add(new Restraint.Part(f,school.getName()));
		String[] majorsName=new String[this.majors.size()];
		for(int i=0;i<majorsName.length;i++) majorsName[i]=this.majors.get(i).getName();
		for(Field f:majorFields)
			hps.add(new Restraint.OrPart(f,majorsName));
		Restraint.Part[] hardPart=new Restraint.Part[hps.size()];
		for(int i=0;i<hardPart.length;i++) hardPart[i]=hps.get(i);
		super.setupHardPart(hardPart);
	}
	
	
	@Override
	public boolean checkBase(Base b,boolean setIfFalse) {
		return super.checkBase(b, setIfFalse);
	}
	
}