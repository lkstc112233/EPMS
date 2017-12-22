package action.function.student;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.staticSource.Major;

/**
 * 导出数字媒体设备规划
 */
public class ExportPlanMedia extends ActionSupport{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}
	
	private ListOfPracticeBaseAndStudents practiceBaseAndStudents;
	
	public ListOfPracticeBaseAndStudents getPracticeBaseAndStudents(){return this.practiceBaseAndStudents;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportPlanMedia(){
		super();
		this.practiceBaseAndStudents=Manager.loadSession(ListOfPracticeBaseAndStudents.class,SessionListKey);
	}

	private String jumpURL=Export.ActionName;
		public String getJumpURL() {return this.jumpURL;}
		public void setJumpURL(String a) {this.jumpURL=a;}

	@Override
	public String execute(){
		return Manager.tips("该项目不可用!","jump");
	}
	
	

	private boolean[][][] getMedia(Integer status) throws IllegalArgumentException, InstantiationException, SQLException {
		List<Major> majors=new ArrayList<Major>();
		majors.addAll(Base.list(Major.class));
		List<Major> tmp=new ArrayList<Major>();
		for(int i=0;i<majors.size();i++) {
			Major m=null;
			for(ListOfPracticeBaseAndStudents.RegionPair rp:this.practiceBaseAndStudents.getList()) {
				for(ListOfPracticeBaseAndStudents.RegionPair.PracticeBasePair pair:rp.getList()) {
					for(Student stu:pair.getStudents()) {
						if(majors.get(i).getName().equals(stu.getMajor())) {
							m=majors.get(i);
						}if(m!=null) break;
					}if(m!=null) break;
				}if(m!=null) break;
			}if(m!=null) tmp.add(m);
		}
		majors=tmp;
		boolean[][][] media=new boolean[majors.size()][][];
		for(int i=0;i<media.length;i++) {
			media[i]=new boolean[this.practiceBaseAndStudents.getList().size()][];
			for(int j=0;j<media[i].length;j++) {
				media[i][j]=new boolean[this.practiceBaseAndStudents.getList().get(i).getList().size()];
				for(int k=0;k<media[i][j].length;k++)
					media[i][j][k]=false;
			}
		}
		Map<String,Integer> majorsMap=new HashMap<String,Integer>();
		for(int i=0;i<majors.size();i++)
			majorsMap.put(majors.get(i).getName(),i);
		List<Plan> plans=Base.list(Plan.class,new Restraint(
				Field.getField(Plan.class,"year"),
				this.getAnnual().getYear()));
		for(Plan p:plans){
			int[] index=this.practiceBaseAndStudents.indexOf(p.getPracticeBase());
			if(index!=null && index.length>=2)
				media[majorsMap.get(p.getMajor())][index[0]][index[1]]=
				p.getMedia();
		}
		return media;
	}

	/*
	 * 下载模板
	 */
	private String downloadFileName;
		public void setDownloadFileName(String a){
			this.downloadFileName=a;
			try{this.downloadFileName=new String(a.getBytes("gb2312"), "iso8859-1");
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
				this.downloadFileName=a;
			}//*/
		}
		public String getDownloadFileName(){return this.downloadFileName;}
	private ByteArrayOutputStream downloadOutputStream=null;
	protected String downloadByIO(SpecialIO io,int year,ListOfPracticeBaseAndStudents list,boolean[][][] media,OutputStream stream) throws IOException{
		return io.createPlanMedia(year,list,media,stream);
	}
	public String download(){//下载模板
		System.out.println(">> ExportPlanDesign:download >");
		if(this.practiceBaseAndStudents==null)
			return Manager.tips("该项目未初始化!","jump");
		boolean[][][] media;
		final Integer status=null;
		try {
		media=getMedia(status);
		}catch(IllegalArgumentException | InstantiationException | SQLException e) {
			this.downloadOutputStream=null;
			return Manager.tips("无法读取媒体设备规划！",e,"jump");
		}
		System.out.println(">> ExportPlanDesign:download > create download file.");
		this.downloadOutputStream=new ByteArrayOutputStream();
		try{
			String fileName=this.downloadByIO((SpecialIO)Base.io(),
					this.getAnnual().getYear(),this.practiceBaseAndStudents,media,downloadOutputStream);
			this.setDownloadFileName(fileName);//设置下载文件名称
			this.downloadOutputStream.flush();
		}catch(IOException e){
			this.downloadOutputStream=null;
			return Manager.tips("服务器开小差去了，暂时无法下载！",e,"jump");
		}
		System.out.println(">> ExportPlanDesign:download <downloadAttachment");
		return "downloadAttachment";
	}
	public InputStream getDownloadAttachment(){//实际上获取的输出流，使用getter获取的downloadAttachment
		ByteArrayInputStream in=IOHelper.ByteArrayOutStream2InputStream(this.downloadOutputStream);
		try {
			this.downloadOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	/*	try {
			ServletActionContext.getResponse().setHeader("Content-Disposition","attachment;downloadFileName="+java.net.URLEncoder.encode(this.downloadFileName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}//*/
		return in;
	}
	


}
