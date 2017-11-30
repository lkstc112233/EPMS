package action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import obj.annualTable.Student;
import obj.staticObject.PracticeBase;

public interface SpecialWordIO {
	/**
	 * 商洽函
	 */
	public abstract String createPracticeBaseConsultationLetter(
			int year,
			PracticeBase pb,
			List<Student> students,
			String majorName,
			OutputStream stream)
					throws IOException;
	
	
	
}
