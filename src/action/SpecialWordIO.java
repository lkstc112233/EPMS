package action;

import java.io.IOException;
import java.io.OutputStream;

import obj.annualTable.ListOfRegionAndPracticeBaseAndInnerPerson.RegionPair.PracticeBasePair;
import obj.staticObject.InnerPerson;
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
			InnerPerson supervisor,
			PracticeBasePair pair,
			int superviseIndex,
			OutputStream stream)
					throws IOException;
	
	
}
