package action.function.student;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import action.*;
import obj.*;
import obj.annualTable.*;
import obj.annualTable.list.Leaf;
import obj.annualTable.list.List_Region_PracticeBaseRegion_Student;
import obj.annualTable.list.Node;
import obj.annualTable.list.PracticeBaseWithRegion;
import obj.staticSource.Major;

/**
 * 导出数字媒体设备规划
 */
public class ExportPlanMedia extends Action{
	private static final long serialVersionUID = 3677055466118899859L;

	private action.Annual annual=new action.Annual();
	public action.Annual getAnnual(){return this.annual;}

	private List_Region_PracticeBaseRegion_Student list;
	
	public List_Region_PracticeBaseRegion_Student getList(){return this.list;}
	

	static public final String SessionListKey=Export.SessionListKey; 
	
	public ExportPlanMedia(){
		super();
		this.list=Manager.loadSession(List_Region_PracticeBaseRegion_Student.class,SessionListKey);
	}

	@Override
	public String execute(){
		return this.returnWithTips(NONE,"该项目不可用!");
	}
	
	

	private boolean[][][] getMedia() throws IllegalArgumentException, InstantiationException, SQLException {
		List<Major> majors=new ArrayList<Major>();
		majors.addAll(Base.list(Major.class));
		List<Major> tmp=new ArrayList<Major>();
		for(int i=0;i<majors.size();i++) {
			Major m=null;
			for(Node<Region,Leaf<PracticeBaseWithRegion,Student>> rp:this.list.getList()) {
				for(Leaf<PracticeBaseWithRegion,Student> pair:rp.getList()) {
					for(Student stu:pair.getList()) {
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
			media[i]=new boolean[this.list.getList().size()][];
			for(int j=0;j<media[i].length;j++) {
				media[i][j]=new boolean[this.list.getList().get(j).getList().size()];
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
			int[] index=this.list.indexOf(p.getPracticeBase());
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
	protected String downloadByIO(SpecialIO io,int year,List_Region_PracticeBaseRegion_Student list,boolean[][][] media,OutputStream stream) throws IOException{
		return io.createPlanMedia(year,list,media,stream);
	}
	public String download(){//下载模板
		System.out.println(">> ExportPlanDesign:download >");
		if(this.list==null)
			return this.returnWithTips(NONE,"该项目未初始化!");
		boolean[][][] media;
		try {
			media=getMedia();
		}catch(IllegalArgumentException | InstantiationException | SQLException e) {
			this.downloadOutputStream=null;
			return this.returnWithTips(NONE,"无法读取媒体设备规划！",e);
		}
		System.out.println(">> ExportPlanDesign:download > create download file.");
		this.downloadOutputStream=new ByteArrayOutputStream();
		try{
			String fileName=this.downloadByIO((SpecialIO)Base.io(),
					this.getAnnual().getYear(),this.list,media,downloadOutputStream);
			this.setDownloadFileName(fileName);//设置下载文件名称
			this.downloadOutputStream.flush();
		}catch(IOException e){
			this.downloadOutputStream=null;
			return this.returnWithTips(NONE,"服务器开小差去了，暂时无法下载！",e);
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
