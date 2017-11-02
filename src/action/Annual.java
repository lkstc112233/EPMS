package action;

import java.util.Calendar;

public final class Annual {

	static public String yearToken="year";
	static public int Year_Minimum=1990;
	
	public int year=0;
		public void setYear(int a){this.year=a;}
		public void setYear(String a){try{this.year=Integer.parseInt(a);}catch(NumberFormatException e){}}
		public int getYear(){return year;}
		public boolean checkYear(){return this.year>=Year_Minimum;}
	
	
	public Annual(){
		this.setupYear();
	}
	public Annual(int year){
		this.year=year;
	}
		
	public void setupYear(){
		Integer tmp=Manager.loadSession(Integer.class,Annual.yearToken);
		if(tmp==null || !this.checkYear())
			tmp=Calendar.getInstance().get(Calendar.YEAR);
		if(tmp!=null){
			this.setYear(tmp);
			Manager.saveSession(yearToken,Integer.valueOf(this.getYear()));
		}
	}
	
	@Override
	public String toString(){
		if(this.year<Annual.Year_Minimum)
			return "null("+this.year+")";
		return String.valueOf(this.year);
	}
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(o instanceof Annual)
			return this.year==((Annual)o).year;
		return false;
	}
	@Override
	public Annual clone(){
		return new Annual(this.year);
	}
}
