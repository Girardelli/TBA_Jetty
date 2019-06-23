package be.tba.pbx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.*;

public class FazecastSerialReaderThread extends Thread implements SerialPortDataListener
{
    final static Logger sLogger = LoggerFactory.getLogger(FazecastSerialReaderThread.class);

    // Event mask & SerialPortEventListener interface

    // Note: The mask is an additive quantity, thus to set a mask on the
    // expectation of the arrival of Event Data (MASK_RXCHAR) and change the
    // status lines CTS (MASK_CTS), DSR (MASK_DSR) we just need to combine all
    // three masks.

    private static final String mVersion = "ver 1.4";

    private static final int SLEEP_TIME = 5000; // 5 seconden

    private static String mFileDir = "";

    private static String mFileScope = ""; // month, week, day

    private SerialPort mSerialPort = null;

    private byte[] mByteBuffer;

    private StringBuffer mReadStrBuf;

    private boolean mStopThread = false;

    private boolean mIsOpen = false;;

    public FazecastSerialReaderThread()
    {
        // super("myCallLogThread");
        mByteBuffer = new byte[2048]; // call record is expected to be 148
        // bytes long
        mReadStrBuf = new StringBuffer();
        mIsOpen = false;
    }

    public void run()
    {
        sLogger.info("FazecastSerialReaderThread.run()");
        sLogger.info("Find available serial port names:");

        SerialPort[] ports = SerialPort.getCommPorts();
        for (int i = 0; i < ports.length; i++)
        {
            sLogger.info("Port name {}: {}", i, ports[i].getDescriptivePortName());
            if (ports[i].getDescriptivePortName().indexOf("COM1") != -1)
            {
            	// this is the one
            	mSerialPort = ports[i];
            	sLogger.info("COM1 found!");
            }
        }
        if (ports.length == 0 || mSerialPort == null)
        {
            sLogger.error("No ports found");
            return;
        }
        mSerialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);// Set params.
        mSerialPort.openPort();// Open serial port
        mIsOpen = true;
        
        mSerialPort.addDataListener(this);
        sLogger.error("Listening...");
    }

	@Override
	public int getListeningEvents() 
	{
		// TODO Auto-generated method stub
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}

    public void serialEvent(SerialPortEvent event)
    {
        switch (event.getEventType())
        {
        case SerialPort.LISTENING_EVENT_DATA_RECEIVED:
        case SerialPort.LISTENING_EVENT_DATA_WRITTEN:
           sLogger.info("RS232: event received " + event.getEventType());
           break;

        case SerialPort.LISTENING_EVENT_DATA_AVAILABLE:
        	
            byte[] newData = new byte[mSerialPort.bytesAvailable()];
            mSerialPort.readBytes(newData, newData.length);
            mReadStrBuf.append(newData.toString());
            
            sLogger.info("RS232: " + newData.toString());
        	
            int eol = 0;
            while ((eol = mReadStrBuf.indexOf(System.getProperty("line.separator"))) >= 0)
            {
                eol += 2;
                String newCallStr = mReadStrBuf.substring(0, eol);
                Forum700CallRecord record = new Forum700CallRecord(newCallStr);
                if (record.isValid())
                {
                    // record.printRecord();
                    printToFile(record.getFileRecord());
                    writeToDb(record);
                }
                else
                {
                    sLogger.info("Invalid record");
                }
                if (mReadStrBuf.length() == ++eol)
                {
                    mReadStrBuf = new StringBuffer();
                    break;
                }
                else
                {
                    mReadStrBuf.delete(0, eol);
                }
            }
            break;
        }
    }

    public void destroy()
    {
        mSerialPort.removeDataListener();
        mSerialPort.closePort();
        mIsOpen = false;
        sLogger.info("CallLogThread destroyed!!");
    }

    public String getFileDir()
    {
        return mFileDir;
    }

    public void setFileDir(String dir)
    {
        mFileDir = dir;
    }

    public String getFileScope()
    {
        return mFileScope;
    }

    public void setFileScope(String scope)
    {
        mFileScope = scope;
    }

    public String getVersion()
    {
        return mVersion;
    }

    public void stopLogging()
    {
        mStopThread = true;
    }

    public int getSleepTime()
    {
        return SLEEP_TIME;
    }

    private void printToFile(String record)
    {
        String fileName = "";
        try
        {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            if (mFileScope.equals("month"))
            {
                fileName = new String(year + "-" + month + ".log");
            }
            else if (mFileScope.equals("week"))
            {
                int week = calendar.get(Calendar.WEEK_OF_YEAR);
                fileName = new String(year + "-" + week + ".log");
            }
            else
            // mFileScope.equals("day")
            {
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                fileName = new String(year + "-" + month + "-" + day + ".log");
            }

            // sLogger.info("Open file: dir={}; filename={}",mFileDir, fileName);

            File file = new File(mFileDir, fileName);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream fileStream = new FileOutputStream(file, true);
            fileStream.write(record.getBytes());
            fileStream.close();
        }
        catch (FileNotFoundException e)
        {
            sLogger.error("printToFile failed.\r\n{}/{} could not be created or does not exist", mFileDir, fileName, e);
        }
        catch (Exception e)
        {
            sLogger.error("printToFile failed", e);
        }
    }

    private void printDebugFile(int size)
    {
        String fileName = "";
        try
        {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;

            if (mFileScope.equals("month"))
            {
                fileName = new String(year + "-" + month + ".dbg");
            }
            else if (mFileScope.equals("week"))
            {
                int week = calendar.get(Calendar.WEEK_OF_YEAR);
                fileName = new String(year + "-" + week + ".dbg");
            }
            else
            // mFileScope.equals("day")
            {
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                fileName = new String("my" + year + "-" + month + "-" + day + ".dbg");
            }
            File file = new File(mFileDir, fileName);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream fileStream = new FileOutputStream(file, true);

            String tmp = new String("\r\nLen=" + size + " ");
            fileStream.write(tmp.getBytes());
            fileStream.write(mByteBuffer, 0, size);
            fileStream.close();
        }
        catch (FileNotFoundException e)
        {
            sLogger.error("printDebugFile failed.\r\n{}/{} could not be created or does not exist", mFileDir, fileName, e);
        }
        catch (Exception e)
        {
            sLogger.error("printDebugFile failed", e);
        }

    }

    protected void writeToDb(Forum700CallRecord record)
    {
    }

    protected void cleanDB()
    {
    }

    protected void checkMail()
    {
    }

}
