package action;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import obj.*;
import obj.annualTable.*;
import obj.annualTable.list.List_Region_PracticeBaseRegionLeaderSuperviseSupervisors;
import obj.annualTable.list.List_Region_PracticeBaseRegion_Student;
import obj.annualTable.list.PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors;
import obj.staticObject.*;
import obj.staticSource.Major;
import obj.restraint.BaseRestraint;

public class POI implements SQLIO, SpecialIO{

	static public int LockMaxTry=10;
	static public long LockTryWait=200;
	static public String path;
	static {
        System.out.println("++ POI:static > 开始加载POI模板文件");
		try {
			path=(POI.class.getClassLoader().getResource("").toURI()).getPath();
			path=path.substring(0,path.lastIndexOf("/"));
			path=path.substring(0,path.lastIndexOf("/"));
			path=path.substring(0,path.lastIndexOf("/"));
			path=path+"/model/Document/";
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	static public final SpecialExcelIO excel=new POIExcel();
	static public final SpecialWordIO word=new POIWord();
	

	static public String loadTextFile(String fileName) throws IOException{
		try(FileInputStream in=new FileInputStream(POI.path+fileName);){
			FileChannel channel=in.getChannel();
			FileLock lock=null;
			for(int i=0;i<POI.LockMaxTry;i++) try{
				lock=channel.tryLock(0L,Long.MAX_VALUE,true);
				if(lock!=null) break;
				try{Thread.sleep(POI.LockTryWait);//共享锁
				}catch(InterruptedException e){}
		//	}catch(OverlappingFileLockException e) {
			}catch(Exception e) {
				e.printStackTrace();
				break;
			}
			if(lock==null)
				throw new IOException(fileName+"文件被占用，无法读取!");
			StringBuilder sb=new StringBuilder();
			try(InputStreamReader bin=new InputStreamReader(in,"UTF-8");
					BufferedReader br=new BufferedReader(bin);
					){
				String tmp=null;
				while((tmp=br.readLine())!=null) {
					sb.append(tmp);
					sb.append("\n");
				}
			}catch(IOException e){
				throw e;
			}finally{
				if(lock.isValid())
					lock.release();
			}
			return sb.toString();
		}
	}
	
	@Override
	public String getModelExcel(Class<? extends Base> clazz, Collection<Field> displayFields, OutputStream out)
			throws IOException {
		return ((SQLIO)excel).getModelExcel(clazz, displayFields, out);
	}
	@Override
	public <T extends Base> List<T> readExcel(Class<T> clazz, Collection<Field> displayFields, InputStream in,
			List<String> error, BaseRestraint restraint) throws IOException, EncryptedDocumentException,
			InvalidFormatException, InstantiationException, IllegalAccessException {
		return ((SQLIO)excel).readExcel(clazz, displayFields, in, error, restraint);
	}

	@Override
	public String createStudentList(int year, PracticeBase pb,Major major,
			OutputStream stream) throws IOException {
		return excel.createStudentList(year, pb, major, stream);
	}
	@Override
	public String createStudentInsuranceList(int year, List_Region_PracticeBaseRegion_Student list, OutputStream stream) throws IOException {
		return excel.createStudentInsuranceList(year, list, stream);
	}
	@Override
	public String createPracticeBaseConsultationLetter(int year, PracticeBase pb,
			String majorName, OutputStream stream) throws IOException {
		return word.createPracticeBaseConsultationLetter(year, pb, majorName, stream);
	}
	@Override
	public String createPlanDesign(int year, List_Region_PracticeBaseRegion_Student list,Boolean status, OutputStream stream)
			throws IOException {
		return excel.createPlanDesign(year, list,status, stream);
	}
	@Override
	public String createPlanMedia(int year, List_Region_PracticeBaseRegion_Student list, boolean[][][] media,
			OutputStream stream) throws IOException {
		return excel.createPlanMedia(year, list, media, stream);
	}
	@Override
	public String createTeacherList(int year,Boolean status, OutputStream stream) throws IOException {
		return excel.createTeacherList(year,status, stream);
	}
	@Override
	public String createSuperviseList(int year,List_Region_PracticeBaseRegionLeaderSuperviseSupervisors list, OutputStream stream) throws IOException {
		return excel.createSuperviseList(year, list, stream);
	}
	@Override
	public String createSupervisorMandate(int year,InnerPerson supervisor,PracticeBaseWithRegionWithLeaderWithSuperviseWithSupervisors pblss,int superviseIndex,
			OutputStream stream) throws IOException {
		return word.createSupervisorMandate(year, supervisor, pblss, superviseIndex, stream);
	}

	@Override
	public String createPracticeBaseInfomation(int year, PracticeBase pb, OutputStream stream) throws IOException {
		return word.createPracticeBaseInfomation(year, pb, stream);
	}

	@Override
	public String createPracticeBaseMoney(int year,ListOfPracticeBaseAndMoney.RegionPair.PracticeBasePair pair, OutputStream stream) throws IOException {
		return word.createPracticeBaseMoney(year,pair,stream);
	}


	
	
	
	
}
