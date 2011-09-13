package web;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;
 
public class AppServletContextListener implements ServletContextListener{
	static long n = 1000L;
	static int tpSize = 5; //dimensione iniziale
	static int tpMaxSize = 5; //dimensione massima
	static boolean flagTP = false;
	public static ThreadPoolExecutor tpe;
	/*se si usa LinkedBlockingQueue<Runnable>() che non è limitata non 
	si raggiungerà mai la dimensione massima del threadpool, ma si manterrà
	la corePoolSize iniziale
	*/
	
        @Override
        public void contextDestroyed(ServletContextEvent arg0) {
                System.out.println("ServletContextListener destroyed");
        }
 
        @Override
        public void contextInitialized(ServletContextEvent arg0) {
                System.out.println("ServletContextListener started"); 
                tpe = new ThreadPoolExecutor(tpSize, tpMaxSize, 50000L, TimeUnit.MILLISECONDS,
   					 new LinkedBlockingQueue<Runnable>(20));
    			tpe.prestartCoreThread();
    			System.out.println("-----------------------------------------------------------");
    			System.out.println("INIZIALIZZATO THREADPOOL");
    			System.out.println("-----------------------------------------------------------");
    			
        }
        
        public static void executeThread(Runnable runn){
        	tpe.execute(runn);
        	System.out.println("-----------------------------------------------------------");
			System.out.println("thread sottomesso al threadpool");
			System.out.println("-----------------------------------------------------------");
			
    }
        
}