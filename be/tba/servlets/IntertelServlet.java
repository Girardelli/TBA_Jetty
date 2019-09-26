package be.tba.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.tba.ejb.pbx.session.CallRecordSqlAdapter;
import be.tba.servlets.helper.IntertelCallManager;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.Constants;
import be.tba.util.data.IntertelCallData;


public class IntertelServlet extends HttpServlet
{
	//final static Logger sLogger = LoggerFactory.getLogger(IntertelServlet.class);
	
	private WebSession mSession;
	private IntertelCallManager mIntertelCallManager;
	private CallRecordSqlAdapter mCallRecordSqlAdapter;
	
	public IntertelServlet()
	{
		try 
		{
			System.out.println("IntertelServlet started");
	    	
			mCallRecordSqlAdapter = new CallRecordSqlAdapter();
			mSession = new WebSession(Constants.MYSQL_URL);
			mIntertelCallManager = IntertelCallManager.getInstance();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
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
		//System.out.println("Intertel servlet doPost");
    	writeParmsToFile(req);
		String phase = req.getParameter("origin");
		//sLogger.info("Intertel servlet doPost");
    	System.out.println("Intertel servlet doPost: " + phase);
    	IntertelCallData data;
    	
    	if (mSession == null || mSession.getConnection() == null)
    	{
    		try 
    		{
				mSession = new WebSession(Constants.MYSQL_URL);
			} 
    		catch (Exception e) 
    		{
    			System.out.println("no connection available and could not be recovered");
    			e.printStackTrace();
    			return;
    		}
    	}
    	
    	String calledNr =  req.getParameter("phone_number_to");
    	String callingNr =  req.getParameter("phone_number_from");
    	String intertelCallId =  req.getParameter("call_id");
    	String inOut = req.getParameter("inout");
    	
    	if (calledNr == null || callingNr == null || intertelCallId == null || phase == null || inOut == null)
    	{
    		System.out.println("Intertel servlet: calledNr:" + calledNr + " callingNr:" + callingNr + " intertelCallId:" + intertelCallId + " phase:" + phase + " inOut:" + inOut);
    		return;
    	}
    	boolean isIncoming =  (inOut.equals("IN"));
    	long timestamp = Long.parseLong(req.getParameter("timestamp"));
    	
    	//WebSession session;
//		try 
//		{
//			mSession = new WebSession(Constants.MYSQL_URL);
//		} 
//		catch (SQLException e) 
//		{
//			// TODO Auto-generated catch block
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//			return;
//		}
		data = mIntertelCallManager.get(intertelCallId);
		switch (phase)
    	{
    	case "start":
            // Check the record and add it if it is a valid one.
    		data = new IntertelCallData(isIncoming, calledNr, callingNr, intertelCallId, timestamp, phase);
    		mIntertelCallManager.newCall(data);
    		data.setDbRecordId(mCallRecordSqlAdapter.addIntertelCall(mSession, data));
    		break;
    		
    	case "answer":
    		if (data != null)
    		{
        		data.setTsAnswer(timestamp);
        		data.setCurrentPhase(phase);
        		data.setCurrentPhase(req.getParameter("answeredby"));
        		mCallRecordSqlAdapter.setTsAnswer(mSession, data);
        		System.out.println(data.intertelCallId + "-answered");

    		}
    		break;
    		
    	case "transfer":
    		if (data != null)
    		{
    			// transfer called party answers
    			IntertelCallData transferOutCall = mIntertelCallManager.getransferCall(intertelCallId, calledNr, IntertelCallData.kTbaNr);
    			transferOutCall.setIsTransfer();
    			data.setTsTransfer(timestamp); 
    			data.setCurrentPhase(phase);
    			mCallRecordSqlAdapter.setTransfer(mSession, data, transferOutCall);
    			System.out.println(data.intertelCallId + "-transfered");

    		}
    		break;
    		
    	case "end":
    		if (data != null) 
    		{
        		data.setTsEnd(timestamp);
        		mCallRecordSqlAdapter.setTsEnd(mSession, data);
        		data.setCurrentPhase(phase);
    			data.isEndDone = true;
    			System.out.println(data.intertelCallId + "-end");

    		}
    		break;
    		
    	case "summary":
    		if (data != null) 
    		{
        		data.isSummaryDone = true;
        		if (!data.isIncoming)
    			{	
        			data.setCallingNr(req.getParameter("viaDID"));
            		mCallRecordSqlAdapter.setCallingNr(mSession, data);
            		System.out.println(data.intertelCallId + "-summary");

    			}
    		}
    		break;

    	default:
    		System.out.println("Intertel servlet doPost: unknown 'origin'=" + phase);
        	        	
    	}
		if (data == null)
		{
			System.out.println("INtertel call cannot be removed: data=null");
			return;
		}
		else if (data.isEndDone && data.isSummaryDone)
		{
    		mIntertelCallManager.removeCall(intertelCallId);
			writeToFile(data);
			System.out.println(data.intertelCallId + "-finalize with write to log");
		}
    }


    private void writeParmsToFile(HttpServletRequest req) throws IOException
    {
        File file = new File(Constants.INTERTELL_CALLLOG_PATH, "parms.txt");
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
    
    private void writeToFile(IntertelCallData data) throws IOException
    {
        File file = new File(Constants.INTERTELL_CALLLOG_PATH, "calls.txt");
        StringBuffer strBuf = new StringBuffer();
        if (!file.exists())
        {
            file.createNewFile();
        	strBuf.append("datum    uur            van -->         naar duur   (info)\r\n");
        }
        FileOutputStream fileStream = new FileOutputStream(file, true);
        
        Calendar vToday = Calendar.getInstance();
        strBuf.append(String.format("%02d/%02d/%02d %02d:%02d %12s --> %12s %s %s\r\n", vToday.get(Calendar.DAY_OF_MONTH), vToday.get(Calendar.MONTH) + 1, vToday.get(Calendar.YEAR) - 2000, vToday.get(Calendar.HOUR_OF_DAY), vToday.get(Calendar.MINUTE), data.callingNr, data.calledNr, (data.tsAnswer != 0) ? Long.toString(data.tsEnd-data.tsAnswer) : "gemist", data.isTransferOutCall ? "(doorgeschakeld)" : ""));
        fileStream.write(strBuf.toString().getBytes());
        fileStream.close();
    }    
    
}
