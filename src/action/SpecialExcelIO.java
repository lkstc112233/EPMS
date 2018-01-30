package action;

import java.io.IOException;
import java.io.OutputStream;

import obj.annualTable.list.List_Region_PracticeBaseRegionLeaderSuperviseSupervisors;
import obj.annualTable.list.List_Region_PracticeBaseRegion_Student;
import obj.staticObject.PracticeBase;
import obj.staticSource.Major;

public interface SpecialExcelIO {
	
	
	
	/**
	 * 实习生名单
	 * @return 文件名称
	 */
	public abstract String createStudentList(
			int year,
			PracticeBase pb,
			Major major,
			OutputStream stream)
					throws IOException;

	/**
	 * 保单 
	 */
	public abstract String createStudentInsuranceList(
			int year, 
			List_Region_PracticeBaseRegion_Student list,
			OutputStream stream)
					throws IOException;

	/**
	 * 布局规划
	 */
	public abstract String createPlanDesign(
			int year,
			List_Region_PracticeBaseRegion_Student list,
			Boolean status,
			OutputStream stream)
					throws IOException;

	/**
	 * 数字媒体设备规划
	 */
	public abstract String createPlanMedia(
			int year,
			List_Region_PracticeBaseRegion_Student list,
			boolean[][][] media,
			OutputStream stream)
					throws IOException;
	
	/**
	 * 指导教师列表
	 */
	public abstract String createTeacherList(
			int year,
			Boolean status,
			OutputStream stream)
					throws IOException;

	/**
	 * 督导任务表
	 */
	public abstract String createSuperviseList(
			int year,
			List_Region_PracticeBaseRegionLeaderSuperviseSupervisors list,
			OutputStream stream)
					throws IOException;
	
	
	
	
}
