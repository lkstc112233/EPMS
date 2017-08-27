package persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

import obj.annualTable.Time;
import obj.staticSource.ACCESS;

public final class DB {
	
	static String path;
	static String iniFileName="persistence/sql.ini";
	static String setupTimeTableFileName="persistence/setupTimeTable.sql";
	
	static{
	    Properties props = new Properties();
        System.out.println("++ DB:static > 开始加载SQL驱动程序");
	    try {
			path=(DB.class.getClassLoader().getResource("").toURI()).getPath();
			props.load(new FileInputStream(path+iniFileName));
		} catch (IOException | URISyntaxException e){
			e.printStackTrace();
			System.err.println("++ DB:static > 文件读取失败！"+path+iniFileName);
			props=null;
		}
	    if(props!=null){
		    String name=props.getProperty("name");
		    String driver = props.getProperty("driver");
		    try{
		        Class.forName(driver);
		        System.out.println("++ DB:static > 成功加载"+name+"驱动程序");
		    } catch (ClassNotFoundException e){
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
	    }
	}
	
	private DB(int x){
	}
	private DB() throws SQLException, FileNotFoundException, IOException{
	    Properties props = new Properties();
		props.load(new FileInputStream(path+iniFileName));
		String url=props.getProperty("url");
		String user=props.getProperty("user");
		String pass=props.getProperty("password");
		connection=DriverManager.getConnection(url,user,pass);
	}
	
	
	
	static private DB instance=null;
	static public DB getInstance(){
		if(instance==null){
	        try {
				DB.init();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.err.println("++ DB:getInstance > 文件未找到！"+path+iniFileName);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("++ DB:getInstance > 文件打开失败！"+path+iniFileName);
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("++ DB:getInstance > 数据库连接失败！");
			}
		}
		return instance;
	}
	static public DB db(){return getInstance();}
	static public Connection con(){return getInstance().connection;}
	static protected void init() throws FileNotFoundException, SQLException, IOException{
		DB.instance=new DB();
	}
	

	private Connection connection = null;
	public Connection getConnection(){
		return connection;
	}
	
//	static private final String TimeTableYear="__YEAR__";
	static synchronized public void setupTimeTable(int year) throws SQLException{
		System.out.println("++ DB:setupTimeTable > year="+year);
		List<ACCESS> accessList=ACCESS.list(ACCESS.class);
		System.out.println("++ DB:setupTimeTable > list:[");
		for(ACCESS a:accessList)
			System.out.println(a.toString());
		System.out.println("++ DB:setupTimeTable > list:]");
		List<Time> timeList=new ArrayList<Time>();
		for(ACCESS a:accessList){
			Time t=Time.getFromACCESS(year,a);
			if(t!=null)
				timeList.add(t);
		}
		int index=0;
		try{
			System.out.println("++ DB:setupTimeTable > create(insert) to table(Time):");
			for(;index<timeList.size();index++){
				Time t=timeList.get(index);
				System.out.print("++ DB:setupTimeTable > insert["+index+"]"+t.toString());
				t.create();
				System.out.println("success");
			}
			System.out.println("++ DB:setupTimeTable > create(insert) END");
		}catch(SQLException | IllegalArgumentException | IllegalAccessException e){
			System.out.println("fail");
			e.printStackTrace();
			for(int i=0;i<index;i++){
				Time t=timeList.get(i);
				System.out.print("++ DB:setupTimeTable > delete["+i+"]"+t.toString());
				try {
					t.delete();
					System.out.println("success");
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					System.out.println("fail");
					e1.printStackTrace();
				}
			}
			System.out.println("++ DB:setupTimeTable > delete END");
		}
	/*
		try(FileInputStream fin=new FileInputStream(path+setupTimeTableFileName);
				Scanner in=new Scanner(fin);
				Statement st=DB.con().createStatement();
				){
			String sql="";
			while(in.hasNext()){
				sql+=in.nextLine()+"\r\n";
				if(sql.contains(";")){
					sql=sql.replaceAll(TimeTableYear,String.valueOf(year));
					System.out.println("++ DB:setupTimeTable > execute: "+sql);
					st.execute(sql);
					sql="";
				}
			}
			if(!sql.isEmpty()){
				sql.replaceAll(TimeTableYear,String.valueOf(year));
				System.out.println("++ DB:setupTimeTable > execute: "+sql);
				st.execute(sql);
			}
			System.out.println("++ DB:setupTimeTable > execute: SUCCESS!");
		}catch(IOException e){
			e.printStackTrace();
			System.err.println(path+iniFileName+"文件读取失败！");
		}
		//*/
		System.out.println("++ DB:setupTimeTable <");
	}
	
	
	
	
}
