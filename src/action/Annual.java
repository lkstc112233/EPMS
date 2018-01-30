package action;

import java.util.Calendar;

import obj.Field;

public final class Annual {

	static public int Year_Minimum=1990;
	
	public int year=0;
		public void setYear(int a){
			this.year=a;
			if(this.checkYear())
				Manager.setYear(this.getYear());
		}
		public void setYear(String a){
			this.year=Field.s2i(a,0);
			if(this.checkYear())
				Manager.setYear(this.getYear());
		}
		public int getYear(){return year;}
		public boolean checkYear(int year){return year>=Year_Minimum;}
		public boolean checkYear(){return this.checkYear(this.year);}
	
	
	public Annual(){
		this.setupYear();
	}
	public Annual(int year){
		this.year=year;
	}
		
	public void setupYear(){
		Integer tmp=Manager.getYear();
		if(tmp==null || !this.checkYear(tmp))
			tmp=Calendar.getInstance().get(Calendar.YEAR);
		if(tmp!=null){
			this.setYear(tmp);
			Manager.setYear(this.getYear());
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
