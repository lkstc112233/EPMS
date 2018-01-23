package obj.annualTable.list;

import obj.annualTable.MoneyPB;
import obj.annualTable.Region;
import obj.staticObject.PracticeBase;

public class PracticeBaseWithRegionWithMoneyPB extends Pair<PracticeBaseWithRegion,MoneyPB>{

	public PracticeBaseWithRegionWithMoneyPB(PracticeBase pb,Region region,MoneyPB money){
		super(new PracticeBaseWithRegion(pb,region),money);
	}

	@SuppressWarnings("deprecation")
	public PracticeBase getPracticeBase() {return super.getKey().getKey();}
	@SuppressWarnings("deprecation")
	public Region getRegion() {return super.getKey().getValue();}
	@SuppressWarnings("deprecation")
//	public MoneyPB getMoney() {return super.getValue();}
	public MoneyPB getSum() {return super.getValue();}
	
	
	private int numberOfStudent,numberOfStudentSYY;
	
	public int getNumberOfStudent() {return this.numberOfStudent;}
	public int getNumberOfStudentSYY() {return this.numberOfStudentSYY;}
	
	@SuppressWarnings("deprecation")
	protected void setSum(MoneyPB sum,int student,int studentSYY) {
		this.setValue(sum);
		this.numberOfStudent=student;
		this.numberOfStudentSYY=studentSYY;
	}
	
}
