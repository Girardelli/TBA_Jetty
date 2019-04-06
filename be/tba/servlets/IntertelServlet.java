package be.tba.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.helper.IntertelCallManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.data.IntertelCallData;


public class IntertelServlet extends HttpServlet
{
	final static Logger sLogger = LoggerFactory.getLogger(IntertelServlet.class);
	
	private WebSession mSession;
	private IntertelCallManager mIntertelCallManager;
	private CallRecordSqlAdapter mCallRecordSqlAdapter;
	
	public IntertelServlet()
	{
		try 
		{
			mCallRecordSqlAdapter = new CallRecordSqlAdapter();
			mSession = new WebSession(Constants.MYSQL_URL);
			mIntertelCallManager = IntertelCallManager.getInstance();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			sLogger.error(e.getMessage());
			e.printStackTrace();
		}
	}
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -671358136484886007L;
	
    /**
     * 
     * C:\java\curl\curl-7.64.0-win64-mingw\bin\curl -d "param1=value1&param2=value2" -X  POST http://localhost:8080/tba/intertel
     *
     * C:\java\curl\curl-7.64.0-win64-mingw\bin\curl -d "param1=value1&param2=value2" -X  POST https://thebusinessassistant.be/tba/intertel
     * 
     */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
    	sLogger.info("Intertel servlet doPost");
    	System.out.println("Intertel servlet doPost");
    	IntertelCallData data;
    	
    	if (mSession.getConnection() == null)
    	{
    		try 
    		{
				mSession = new WebSession(Constants.MYSQL_URL);
			} 
    		catch (Exception e) 
    		{
    			sLogger.error("no connection available and could not be recovered");
    			e.printStackTrace();
    			return;
    		}
    	}
    	
    	String calledNr =  req.getParameter("phone_number_to");
    	String callingNr =  req.getParameter("phone_number_from");
    	String intertelCallStr =  req.getParameter("knummer");
    	String inOut = req.getParameter("inout");
    	String phase = req.getParameter("origin");
    	if (calledNr == null || callingNr == null || intertelCallStr == null || phase == null || inOut == null)
    	{
    		sLogger.info("Intertel servlet: calledNr:" + calledNr + " callingNr:" + callingNr + " intertelCallStr:" + intertelCallStr + " phase:" + phase + " inOut:" + inOut);
    		return;
    	}
    	boolean isIncoming =  (inOut.equals("IN"));
    	long timestamp = Long.parseLong(req.getParameter("timestamp"));
    	int intertelCallId = Integer.parseInt(intertelCallStr);
    	
    	WebSession session;
		try 
		{
			session = new WebSession(Constants.MYSQL_URL);
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			sLogger.error(e.getMessage());
			e.printStackTrace();
			return;
		}
		data = mIntertelCallManager.get(intertelCallId);
		data.setCurrentPhase(phase);
		switch (phase)
    	{
    	case "start":
            // Check the record and add it if it is a valid one.
    		if (!isIncoming && data != null )
    		{
    			// call is transfered
    			IntertelCallData transferData = new IntertelCallData(isIncoming, calledNr, "014409000", intertelCallId, timestamp, true, phase);
    			data.setTransferData(transferData);
    			transferData.setTransferData(data);
    			transferData.setDbRecordId(mCallRecordSqlAdapter.addIntertelCall(session, transferData));
    		}
    		else
    		{
        		data = new IntertelCallData(isIncoming, calledNr, callingNr, intertelCallId, timestamp, false, phase);
        		mIntertelCallManager.newCall(data);
        		data.setDbRecordId(mCallRecordSqlAdapter.addIntertelCall(session, data));
    		}
    		break;
    		
    	case "answer":
    		if (!isIncoming && data.transferData != null )
			{
    			// calling party connects to transfer called party
    			data.transferData.setTsEnd(timestamp); 
    			mCallRecordSqlAdapter.setTsEnd(session, data.transferData);
			}
    		else
    		{
        		data.setTsAnswer(timestamp);
        		mCallRecordSqlAdapter.setTsAnswer(session, data);
         	}
    		break;
    		
    	case "transfer":
    		if (!isIncoming && data.transferData != null )
			{
    			// transfer called party answers
    			data.transferData.setTsAnswer(timestamp); 
    			mCallRecordSqlAdapter.setTsAnswer(session, data.transferData);
			}
    		break;
    		
    	case "end":
    		data.setTsEnd(timestamp);
    		mCallRecordSqlAdapter.setTsEnd(session, data);
    		mIntertelCallManager.removeCall(intertelCallId);
    		break;
    		
    	case "summary":
    		break;

    	default:
    		sLogger.info("Intertel servlet doPost: unknown 'origin'=" + phase);
        	        	
    	}
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
