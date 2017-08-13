package persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Initialization implements ServletContextListener{

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("程序退出...");
    }
    
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("程序初始化...");
        
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
		}//*/
    }
}
