package action.login;

import java.util.Calendar;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class AnnualAction extends ActionSupport{
	private static final long serialVersionUID = 3249236302198381169L;
	
	static public String yearToken="year";
	static public int Year_Minimum=1990;
	
	public int year=0;

	public int getYear() {return year;}
	public void setYear(int year) {
		this.year = year;
	}
	public void setYear(String s) {
		if(s==null) return;
		try{
			year=Integer.parseInt(s);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return;
		}
	}

	public boolean executive=false;
	public boolean isExecutive() {return executive;}
	public void setExecutive(boolean executive) {this.executive = executive;}
	
	
	public AnnualAction(){
		super();
		Object obj=ActionContext.getContext().getSession().get(yearToken);
		if(obj!=null)
			this.setYear((Integer)obj);
	}
	public final void setupYear(){
		if(this.getYear()<Year_Minimum)
			this.setYear(Calendar.getInstance().get(Calendar.YEAR));
		ActionContext.getContext().getSession().put(yearToken,
				Integer.valueOf(this.getYear())
				);
	}
	
	
	public String display(){
		System.out.println(">> "+this.getClass().getSimpleName()+":display >");
		System.out.println(">> "+this.getClass().getSimpleName()+":display <NONE");
		return NONE;
	}


	
	
}
