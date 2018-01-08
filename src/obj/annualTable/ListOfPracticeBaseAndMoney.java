package obj.annualTable;

import java.sql.SQLException;
import java.util.*;

import obj.*;
import obj.staticObject.*;
import obj.annualTable.ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair;

/**
 * there is a '<code>list</code>' which contains an '<code>ArrayList</code>'
 * with type '<code>Pair</code>'.<br/>
 *  '<code>Pair</code>'  has a '<code>practiceBase</code>' and a '<code>students</code>'
 *  which is an '<code>ArrayList</code>' with type'<code>PracticBase</code>'<br/><br/>
 *  树形结构<br/>
 *  ROOT<br/>
 *  {Region................Region}<br/>
 *  {[PracticeBase...].....[PracticeBase...]}<br/>
 *  {[(Student......)].....[(Student.......)]}<br/>
 */
public class ListOfPracticeBaseAndMoney{
	static public class RegionPair implements Comparable<RegionPair>{
		private Region region;
		private List<PracticeBasePair> list=new ArrayList<PracticeBasePair>();
			public Region getRegion() {return this.region;}
			public int getSize() {return this.list.size();}
			public List<PracticeBasePair> getList(){return this.list;}
			public int getAllStudentsCount() {
				int res=0;
				for(PracticeBasePair p:list) res+=p.getSize();
				return res;
			}
		public RegionPair(Region region) {this.region=region;}
			
		static public class PracticeBasePair{
			private Region region;
			private PracticeBase practiceBase;
			private MoneyPB sum;
			private List<MoneyPB> moneys=new ArrayList<MoneyPB>();
				public int getSize(){return this.moneys.size();}
				public Region getRegion() {return this.region;}
				public PracticeBase getPracticeBase(){return this.practiceBase;}
				public MoneyPB getSum() {return sum;}
				public List<MoneyPB> getMoneys(){return this.moneys;}
			public PracticeBasePair(Region r,PracticeBase pb){this.region=r;this.practiceBase=pb;}
		}
		@Override
		public int compareTo(RegionPair o) {
			if(o==null) return 1;
			boolean hx=this.list.get(0).practiceBase.getHx();
			boolean hx2=o.list.get(0).practiceBase.getHx();
			if(hx && !hx2) return 1;
			if(!hx && hx2) return -1;
			if(hx && hx2)
				return this.region.compareTo(o.region);
			else
				return this.list.get(0).practiceBase.getProvince()
						.compareTo(o.list.get(0).practiceBase.getProvince());
		}	
	}
	private List<RegionPair> list=new ArrayList<RegionPair>();
		public List<RegionPair> getList(){return list;}
		public int getSize(){return list.size();}
	
	/**
	 * 不包含Region=null的子树
	 */
	public ListOfPracticeBaseAndMoney(int year) throws IllegalArgumentException, InstantiationException, SQLException{
		//已分配实习基地的
		List<Base[]> tmp=Base.list(
				new JoinParam(MoneyPB.class)
				.append(JoinParam.Type.InnerJoin,
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
		Collections.sort(this.list);
		for(RegionPair rp:this.list) {
			for(PracticeBasePair pair:rp.list) {
				MoneyPB sum=new MoneyPB();
				sum.setYear(year);
				sum.setRemark("sum");
				for(MoneyPB p:pair.moneys)
					sum.appendSum(p);
				pair.sum=sum;
			}
		}
	}

	public int[] indexOf(String practiceBaseName){
		int[] index=new int[]{-1,-1};
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(index[0]=0;index[0]<this.list.size();index[0]++){
			RegionPair rp=this.list.get(index[0]);
			for(index[1]=0;index[1]<rp.getList().size();index[1]++){
				PracticeBase pb=rp.getList().get(index[1]).practiceBase;
				if(pb!=null && pb.getName()!=null && pb.getName().equals(practiceBaseName))
					return index;
			}
		}
		return null;
	}
	public PracticeBasePair get(String practiceBaseName){
		if(practiceBaseName==null || practiceBaseName.isEmpty()) return null;
		for(RegionPair rp:this.list) if(rp.getRegion()!=null)
			for(PracticeBasePair p:rp.list){
				PracticeBase t=p.practiceBase;
				if(t!=null && t.getName()!=null && t.getName().equals(practiceBaseName))
					return p;
			}
		return null;
	}
	public PracticeBasePair get(PracticeBase pb){
		for(RegionPair rp:this.list) if(rp.getRegion()!=null)
			for(PracticeBasePair p:rp.list){
				PracticeBase t=p.practiceBase;
				if(pb==null && t==null) return p;
				if(pb!=null && t!=null && t.getName()!=null && t.getName().equals(pb.getName()))
					return p;
			}
		return null;
	}
	public void put(Region region,PracticeBase pb,MoneyPB money) throws IllegalArgumentException, InstantiationException, SQLException {
		if(region==null || pb==null)
			return;
		PracticeBasePair tmp=this.get(pb);
		if(tmp==null){//需要新增一个PracticeBasePair
			for(RegionPair rp:this.list) {
				if(rp.getRegion().getName()!=null && rp.getRegion().getName().equals(region.getName())) {
					rp.getList().add(tmp=new PracticeBasePair(region,pb));
					break;
				}
			}
			if(tmp==null) {
				//需要新增一个RegionPair
				RegionPair rp=new RegionPair(region);
				rp.getList().add(tmp=new PracticeBasePair(region,pb));
				this.list.add(rp);
			}
		}
		if(money!=null && !tmp.moneys.contains(money))
			tmp.moneys.add(money);
	}
}
