package action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import obj.annualTable.Student;
import obj.staticObject.PracticeBase;

public interface SpecialIO {
	
	
	
	/**
	 * 实习生名单
	 */
	public abstract void createStudentList(
			PracticeBase pb,
			List<Student> students,
			OutputStream stream)
					throws IOException;
	/**
	 * 商洽函
	 */
	public abstract void createPracticeBaseFile(
			PracticeBase pb,
			List<Student> students,
			OutputStream stream)
					throws IOException;
	
	/**
	 * 布局规划
	 */
	
	
	
}
