package be.tba.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.util.constants.Constants;


public class IntertelServlet extends HttpServlet
{
	final static Logger sLogger = LoggerFactory.getLogger(IntertelServlet.class);
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -671358136484886007L;
	
    /**
     * 
     * C:\java\curl\curl-7.64.0-win64-mingw\bin\curl -d "param1=value1&param2=value2" -X  POST http://localhost:8080/TheBusinessAssistant/Intertel
     *
     * C:\java\curl\curl-7.64.0-win64-mingw\bin\curl -d "param1=value1&param2=value2" -X  POST https://thebusinessassistant.be/TheBusinessAssistant/intertel
     * 
     */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
    	sLogger.info("Intertel servlet doPost");
    	System.out.println("Intertel servlet doPost");
    	//sLogger.info("Open file: dir={}; filename={}", mFileDir, fileName);
    	writeToFile(req);
    	
    	
    }


    private void writeToFile(HttpServletRequest req) throws IOException
    {
        File file = new File(Constants.INTERTELL_CALLLOG_PATH, "calls.txt");
        if (!file.exists())
        {
            file.createNewFile();
        }
        FileOutputStream fileStream = new FileOutputStream(file, true);
        
        Enumeration<String> dataNames = req.getParameterNames();
        StringBuffer strBuf = new StringBuffer();
        
        while (dataNames.hasMoreElements()) 
        {
          String parm = (String) dataNames.nextElement();
          strBuf.append(parm + ": " + req.getParameter(parm) + "\r\n");
        }
        strBuf.append("---------------------------------------\r\n");
        fileStream.write(strBuf.toString().getBytes());
        fileStream.close();
    }    
    
    
}
