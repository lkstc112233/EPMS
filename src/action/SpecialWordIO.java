package action;

import java.io.IOException;
import java.io.OutputStream;

import obj.annualTable.list.PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors;
import obj.annualTable.list.PracticeBaseWithRegionWithMoneyPB;
import obj.staticObject.*;

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
			PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors pblss,
			int superviseIndex,
			OutputStream stream)
					throws IOException;
	
	/**
	 * 实习基地信息
	 */
	public abstract String createPracticeBaseInfomation(
			int year,
			PracticeBase pb,
			OutputStream stream
			)
					throws IOException;
	/**
	 * 实习基地经费
	 * @param studentNum 
	 * @param sum 
	 * @param enterPracticeBaseTime 
	 */
	public abstract String createPracticeBaseMoney(
			int year,
			PracticeBaseWithRegionWithMoneyPB pbrm,
			OutputStream stream
			)throws IOException;
	
}
