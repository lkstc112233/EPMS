package persistence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DB {
	
	static String iniFileName="persistence/sql.ini";
	
	static{
	    Properties props = new Properties();
	    try {
			String path = (DB.class.getClassLoader().getResource("").toURI()).getPath();
			iniFileName=path+iniFileName;
			props.load(new FileInputStream(iniFileName));
		} catch (IOException | URISyntaxException e){
			e.printStackTrace();
			System.err.println(iniFileName+"文件读取失败！");
			props=null;
		}
	    if(props!=null){
		    String name=props.getProperty("name");
		    String driver = props.getProperty("driver");
		    try{
		        Class.forName(driver);
		        System.out.println("成功加载"+name+"驱动程序");
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
		props.load(new FileInputStream(iniFileName));
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
				System.err.println(DB.iniFileName+"文件未找到！");
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(DB.iniFileName+"文件打开失败！");
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("数据库连接失败！");
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
	
	
	
	
	
	
}
