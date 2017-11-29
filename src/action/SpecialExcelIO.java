package action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import obj.annualTable.Student;
import obj.staticObject.PracticeBase;

public interface SpecialExcelIO {
	
	
	
	/**
	 * 实习生名单
	 * @return 文件名称
	 */
	public abstract String createStudentList(
			int year,
			PracticeBase pb,
			List<Student> students,
			String majorName,
			OutputStream stream)
					throws IOException;
	
	/**
	 * 布局规划
	 */
	
	
	
}
