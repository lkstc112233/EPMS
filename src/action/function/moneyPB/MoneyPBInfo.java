package action.function.moneyPB;

import action.Manager;
import obj.*;
import obj.annualTable.MoneyPB;

public class MoneyPBInfo extends action.TableOperationAction{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	

	private String practiceBaseName;
		public void setPracticeBaseName(String a) {
			this.practiceBaseName=a;
			Manager.saveSession(SessionPracticeBaseNameKey,this.practiceBaseName);
		}
		public String getPracticeBaseName() {return this.practiceBaseName;}
		
	static private final String SessionPracticeBaseNameKey="MoneyPB_PracticeBaseName";

	public MoneyPBInfo(){
		super();
		this.practiceBaseName=Manager.loadSession(String.class,SessionPracticeBaseNameKey);
	}


	@Override
	protected Search createSearch() throws Exception {
		JoinParam param=new JoinParam(MoneyPB.class);
		return new Search(param,new obj.restraint.HardRestraint(param,0,
				new Restraint.Part[] {
						new Restraint.Part(Field.getField(MoneyPB.class,"year"),Restraint.Type.Equal,this.annual.year),
						new Restraint.Part(Field.getField(MoneyPB.class,"practiceBase"),Restraint.Type.Equal,this.practiceBaseName)
				}));
	}


}
