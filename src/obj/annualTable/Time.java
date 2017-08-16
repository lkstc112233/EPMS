package obj.annualTable;

import java.sql.SQLException;
import java.sql.Timestamp;

import obj.*;

@SQLTable("Time")
public class Time extends AnnualTable{
	
	@SQLField(isKey=true)
	private String project;
	@SQLField
	private Timestamp time;
	
	public String getProject() {return project;}
	public void setProject(String project) {this.project = project;}
	public Timestamp getTime() {return time;}
	public void setTime(Timestamp time) {this.time = time;}
	public void setTime(String s){
		this.time=Timestamp.valueOf(s);
	}
	
	public Time() throws SQLException {
		super();
	}
	
}