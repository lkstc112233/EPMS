package obj.staticSource;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import action.Manager;
import obj.*;
import obj.annualTable.Time;

@SQLTable("ACCESS")
public class ACCESS extends ListableBase{
	
	@SQLField(needSorted=true)
	private int id;
	@SQLField(isKey=true)
	private String project;
	@SQLField
	private Timestamp time1;
	@SQLField
	private Timestamp time2;
	@SQLField
	private Boolean 学生;
	@SQLField
	private Boolean 教学院长;
	@SQLField
	private Boolean 教务员;
	@SQLField
	private Boolean 教师;
	@SQLField
	private Boolean 教务处;
	@SQLField
	private Boolean 领导;
	
	
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id=id;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public Timestamp getTime1() {return time1;}
	public void setTime1(Timestamp time) {this.time1 = time;
	Manager.RegularPeriod(time1,time2);}
	public void setTime1(String s){
		this.time1=Timestamp.valueOf(s);
		Manager.RegularPeriod(time1,time2);
	}
	public Timestamp getTime2() {return time2;}
	public void setTime2(Timestamp time) {this.time2 = time;
	Manager.RegularPeriod(time1,time2);}
	public void setTime2(String s){
		this.time2=Timestamp.valueOf(s);
		Manager.RegularPeriod(time1,time2);
	}
	public Boolean is学生() {
		return 学生;
	}
	public void set学生(Boolean 学生) {
		this.学生 = 学生;
	}
	public Boolean is教学院长() {
		return 教学院长;
	}
	public void set教学院长(Boolean 教学院长) {
		this.教学院长 = 教学院长;
	}
	public Boolean is教务员() {
		return 教务员;
	}
	public void set教务员(Boolean 教务员) {
		this.教务员 = 教务员;
	}
	public Boolean is教师() {
		return 教师;
	}
	public void set教师(Boolean 教师) {
		this.教师 = 教师;
	}
	public Boolean is教务处() {
		return 教务处;
	}
	public void set教务处(Boolean 教务处) {
		this.教务处 = 教务处;
	}
	public Boolean is领导() {
		return 领导;
	}
	public void set领导(Boolean 领导) {
		this.领导 = 领导;
	}



	public ACCESS() throws SQLException {
		super();
	}
	
	static public ACCESS getFromTime(Time t) throws SQLException, IllegalArgumentException, IllegalAccessException{
		if(t==null) return null;
		ACCESS a=new ACCESS();
		a.setId(t.getId());
		a.setProject(t.getProject());
		a.load();
		a.setTime1(t.getTime1());
		a.setTime2(t.getTime2());
		return a;
	}
	
	
	
	static public Map<String,String> map=new HashMap<String,String>();
	static{
		map.put("导入免费师范生数据","drmfsfssj");
		map.put("《信息统计工作的通知》","xxtjgzdtz");
		map.put("信息录入（包括回乡意向）","xxlrbkhxyx");
		map.put("联系实习基地，确认实习生人数","lxsxjdqrsxsrs");
		map.put("确定实习人数布局规划","qdsxrsbjgh");
		map.put("安排督导任务学科规划","apddrwxkgh");
		map.put("安排总领队学科规划","apzldxkgh");
		map.put("《教育实习工作安排的通知》","jysxgzapdtz");
		map.put("落实领导小组","lsldxz");
		map.put("安排各学生实习基地","apgxssxjd");
		map.put("推荐学生大组长人选","tjxsdzcrx");
		map.put("安排各学生指导教师","apgxszdjs");
		map.put("落实总领队老师","lszldls");
		map.put("落实督导教师","lsddjs");
		map.put("申报教育实习课程","sbjysxkc");
		map.put("安排数字媒体设备发放规划","apszmtsbffgh");
		map.put("《数字媒体设备协调使用的通知》","szmtsbxdsydtz");
		map.put("落实数字媒体设备培训培训人员","lsszmtsbpxpxry");
		map.put("数字媒体设备培训借教室","szmtsbpxjjs");
		map.put("《数字媒体设备培训的通知》","szmtsbpxdtz");
		map.put("《征文、摄影和DV大赛的通知》","zwsyhdsdtz");
		map.put("教育实习手册","jysxsc");
		map.put("制作并发送商洽函","zzbfssqh");
		map.put("教育实习工作指南","jysxgzzn");
		map.put("制作督导任务书","zzddrws");
		map.put("制作学校银行汇款确认单","zzxxyxhkqrd");
		map.put("制作教育实习部署会PPT、准备会议材料","zzjysxbshzbhycl");
		map.put("教育实习部署会借教室","jysxbshjjs");
		map.put("教育实习部署会","jysxbsh");
		map.put("邮件通知外地总领队入校前期工作","yjtzwdzldrxqqgz");
		map.put("总领队联系实习基地确认入校时间地点","zldlxsxjdqrrxsjdd");
		map.put("协助总领队大区动员会借教室","xzzlddqdyhjjs");
		map.put("总领队召开大区动员会","zldzkdqdyh");
		map.put("入校时间地点（部院系、学生大组长）","rxsjddbyxxsdzc");
		map.put("制作投保确认单","zztbqrd");
		map.put("邮件通知回生源地教育实习入校督导老师提前取机票购买确认单","yjtzhsydjysxrxddlstqqjpgmqrd");
		map.put("邮件通知北京及周边地区总领队入校前期工作","yjtzbjjzbdqzldrxqqgz");
		map.put("邮件通知督导老师确认财务信息和实习人数","yjtzddlsqrcwxxhsxrs");
		map.put("督导教师入校督导","ddjsrxdd");
		map.put("确定实习基地财务信息和实习人数","qdsxjdcwxxhsxrs");
		map.put("制作各实习基地经费明细表","zzgsxjdjfmxb");
		map.put("发送基地经费给总领队，要求核对","fsjdjfgzldyqhd");
		map.put("各部院系经费明细表","gbyxjfmxb");
		map.put("邮件通知中期督导老师领取基地经费表、带回经费收据、经办人签字","yjtzzqddlslqjdjfbdhjfsjjbrqz");
		map.put("督导教师中期巡视","ddjszqxs");
		map.put("备份基地经费收据、经办人签字","bfjdjfsjjbrqz");
		map.put("部院系经费（部院系），请核对","byxjfbyxqhd");
		map.put("部院系经费报送财务老师","byxjfbscwls");
		map.put("现金实习基地：填写送经费督导教师信息","xjsxjdtxsjfddjsxx");
		map.put("现金实习基地：备份劳务表照片","xjsxjdbflwbzp");
		map.put("现金实习基地：制作财务表报送财务老师","xjsxjdzzcwbbscwls");
		map.put("电汇实习基地：制作财务表报送财务老师","dhsxjdzzcwbbscwls");
		map.put("电汇实习基地：备份财务汇款照片","dhsxjdbfcwhkzp");
		map.put("发邮件告知电汇支付经费的实习基地","fyjgzdhzfjfdsxjd");
		map.put("督导教师返校督导","ddjsfxdd");
		map.put("督导教师费用报销","ddjsfybx");
		map.put("《教育实习总结工作的通知》","jysxzjgzdtz");
		map.put("特殊情况调整申请","tsqkdzsq");
		map.put("学生提交征文、摄影和DV大赛作品","xstjzwsyhdszp");
		map.put("《优秀教育实习工作者评选的通知》","yxjysxgzzpxdtz");
		map.put("报送优秀实习生","bsyxsxs");
		map.put("部院系工作总结","byxgzzj");
		map.put("DV大赛材料初筛","dsclcs");
		map.put("DV大赛专家评选","dszjpx");
		map.put("经费决算表（包含督导次数）","jfjsbbhddcs");
		map.put("《优秀教育实习工作者名单》","yxjysxgzzmd");
		map.put("《征文、摄影和DV大赛获奖名单》","zwsyhdshjmd");

	}
}