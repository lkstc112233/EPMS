package action.jwc;

import com.opensymphony.xwork2.ActionSupport;

public class AnnualAction extends ActionSupport{
	private static final long serialVersionUID = 3249236302198381169L;
	
	public int year;

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
	
	
	
	public String display(){
		System.out.println(">> "+this.getClass().getSimpleName()+":display >");
		System.out.println(">> "+this.getClass().getSimpleName()+":display <NONE");
		return SUCCESS;
	}


	
	
}
