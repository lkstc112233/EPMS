package action;

import java.io.IOException;
import java.io.OutputStream;

import obj.annualTable.ListOfRegionAndPracticeBaseAndInnerPerson;
import obj.staticObject.PracticeBase;

public interface SpecialWordIO {
	/**
	 * 商洽函
	 */
	public abstract String createPracticeBaseConsultationLetter(
			int year,
			PracticeBase pb,
			String majorName,
			OutputStream stream)
					throws IOException;
	/**
	 * 督导任务书
	 */
	public abstract String createSupervisorMandate(
			int year,
			ListOfRegionAndPracticeBaseAndInnerPerson list,
			String supervisorId,
			OutputStream stream)
					throws IOException;
	
	
}
