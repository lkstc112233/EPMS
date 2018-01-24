package obj.annualTable.list;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import obj.*;
import obj.annualTable.*;
import obj.staticObject.*;
import obj.staticSource.*;

public class List_Region_PracticeBaseRegionMoneyPB_MoneyPB extends ListTree<Node<Region,Leaf<PracticeBaseWithRegionWithMoneyPB,MoneyPB>>>{
	public List_Region_PracticeBaseRegionMoneyPB_MoneyPB(int year)
			throws IllegalArgumentException, InstantiationException, SQLException{
		this(year,new DefaultComparator_LeafRegion<Leaf<PracticeBaseWithRegionWithMoneyPB,MoneyPB>>());
	}
	public List_Region_PracticeBaseRegionMoneyPB_MoneyPB(int year,
			Comparator<? super Node<Region,Leaf<PracticeBaseWithRegionWithMoneyPB,MoneyPB>>> comparator)
			throws IllegalArgumentException, InstantiationException, SQLException{
		super();
		List<Base[]> tmp=Base.list(
				new JoinParam(MoneyPB.class)
				.append(JoinParam.Type.RightJoin,
						PracticeBase.class,
						Field.getField(MoneyPB.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"),
						Field.getField(MoneyPB.class,"year"),
						year)
				.append(JoinParam.Type.LeftJoin,
						Region.class,
						Field.getField(Region.class,"practiceBase"),
						Field.getField(PracticeBase.class,"name"),
						Field.getField(Region.class,"year"),
						year),
				new Restraint(Field.getField(MoneyPB.class,"remark"))
				);
		for(Base[] bs:tmp){
			if(bs!=null && bs.length>=3){
				MoneyPB money=null;
				PracticeBase pb=null;
				Region region=null;
				if(bs[0]!=null) money=(MoneyPB)bs[0];
				if(bs[1]!=null) pb=(PracticeBase)bs[1];
				if(bs[2]!=null && pb!=null) region=(Region)bs[2];
				this.put(region,pb,money);
			}
		}
		for(Node<Region, Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB>> rp:this.getList()) {
			for(Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB> pair:rp.getList()) {
				MoneyPB sum=new MoneyPB();
				sum.setYear(year);
				sum.setRemark("sum");
				for(MoneyPB p:pair.getList())
					sum.appendSum(p);
				Restraint restraint=new Restraint(
						Field.getFields(Student.class,"year","practiceBase"),
						new Object[] {year,pair.getT().getPracticeBase().getName()}
				);
				int numberOfStudent=Base.list(Student.class,restraint).size();
				int numberOfStudentSYY=Base.list(new JoinParam(Student.class)
						.append(JoinParam.Type.InnerJoin,
								Major.class,
								Field.getField(Major.class,"name"),
								Field.getField(Student.class,"major"),
								Field.getField(Major.class,"isSYY"),
								true
								),restraint).size();
				pair.getT().setSum(sum,numberOfStudent,numberOfStudentSYY);
			}
		}
		this.getList().sort(comparator);
	}

	public int[] indexOf(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(int[] index=new int[]{0,0};index[0]<this.getSize();index[0]++){
			Node<Region, Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB>> rp=this.getList().get(index[0]);
			for(index[1]=0;index[1]<rp.getList().size();index[1]++){
				PracticeBase pb=rp.getList().get(index[1]).getT().getPracticeBase();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}
	public Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB> getByPracticeBaseName(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(Node<Region, Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB>> rp:this.getList())
			for(Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB> pair:rp.getList()){
				PracticeBase pb=pair.getT().getPracticeBase();
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return pair;
			}
		return null;
	}
	public void put(Region region,PracticeBase pb,MoneyPB money) throws IllegalArgumentException, InstantiationException, SQLException {
		if(region==null || pb==null || money==null)
			return;
		Node<Region, Leaf<PracticeBaseWithRegionWithMoneyPB, MoneyPB>> tmp=
				new Node<Region,Leaf<PracticeBaseWithRegionWithMoneyPB,MoneyPB>>(region);
		tmp=this.insert(region,tmp);
		PracticeBaseWithRegionWithMoneyPB pbrm=new PracticeBaseWithRegionWithMoneyPB(
				pb,region,null);
		Leaf<PracticeBaseWithRegionWithMoneyPB,MoneyPB> tmp2=
				new Leaf<PracticeBaseWithRegionWithMoneyPB,MoneyPB>(pbrm);
		tmp2=tmp.insert(pbrm,tmp2);
		tmp2.insert(money);
	}
}
