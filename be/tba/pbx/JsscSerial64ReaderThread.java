package be.tba.pbx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

public class JsscSerial64ReaderThread extends Thread implements SerialPortEventListener
{
	final static Logger sLogger = LoggerFactory.getLogger(JsscSerial64ReaderThread.class);
    
	// Event mask & SerialPortEventListener interface

	// Note: The mask is an additive quantity, thus to set a mask on the
	// expectation of the arrival of Event Data (MASK_RXCHAR) and change the
	// status lines CTS (MASK_CTS), DSR (MASK_DSR) we just need to combine all
	// three masks.

	private static final String mVersion = "ver 1.4";

	private static final int SLEEP_TIME = 5000; // 5 seconden

	private static String mFileDir = "";

	private static String mFileScope = ""; // month, week, day

	private SerialPort mSerialPort;

	private byte[] mByteBuffer;

	private StringBuffer mReadStrBuf;

	private boolean mStopThread = false;

	private boolean mIsOpen = false;;

	public JsscSerial64ReaderThread()
	{
		// super("myCallLogThread");
		mByteBuffer = new byte[2048]; // call record is expected to be 148
		// bytes long
		mReadStrBuf = new StringBuffer();
		mIsOpen = false;
	}

	public void run()
	{
		sLogger.info("jsscSerial64ReaderThread.run()");
		sLogger.info("Find available serial port names:");

		String[] portNames = SerialPortList.getPortNames();
		for (int i = 0; i < portNames.length; i++)
		{
			sLogger.info("Port name {}: {}", i, portNames[i]);
		}
		if (portNames.length == 0)
		{
			sLogger.error("No ports found");
			return;
		}

		try
		{
			mSerialPort = new SerialPort(portNames[0]);
			mSerialPort.openPort();// Open serial port
			mIsOpen = true;
			mSerialPort.setParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);// Set
																												// params.

			mSerialPort.addEventListener(this);
			sLogger.info("jsscSerial64ReaderThread listenig for events on {}", portNames[0]);

		} catch (SerialPortException ex)
		{
			sLogger.error("Cannot open port", ex);
		}
	}

	public void serialEvent(SerialPortEvent event)
	{
		switch (event.getEventType())
		{
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.ERR:
		case SerialPortEvent.RING:
		case SerialPortEvent.RLSD:
		case SerialPortEvent.RXFLAG:
		case SerialPortEvent.TXEMPTY:
			break;

		case SerialPortEvent.RXCHAR:
			try
			{
				if (event.getEventValue() > 0)
				{// Check bytes count in the input buffer
					// Read data, if 10 bytes available
					String newBytes = mSerialPort.readString(event.getEventValue());
					sLogger.info("received:{}", newBytes);
					mReadStrBuf.append(newBytes);

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
						if (mReadStrBuf.length() == ++eol)
						{
							mReadStrBuf = new StringBuffer();
							break;
						} else
						{
							mReadStrBuf.delete(0, eol);
						}
					}

				}
			} catch (Exception e)
			{
				sLogger.error("Cannot process event", e);
				return;
			}
			break;
		}
	}

	public void destroy()
	{
		try
		{
			mSerialPort.removeEventListener();
			mSerialPort.closePort();
			mIsOpen = false;
			sLogger.info("CallLogThread destroyed!!");
		} catch (SerialPortException ex)
		{
			sLogger.error("Destroy failed", ex);
		}
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
			} else if (mFileScope.equals("week"))
			{
				int week = calendar.get(Calendar.WEEK_OF_YEAR);
				fileName = new String(year + "-" + week + ".log");
			} else
			// mFileScope.equals("day")
			{
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				fileName = new String(year + "-" + month + "-" + day + ".log");
			}

			sLogger.info("Open file: dir={}; filename={}",mFileDir, fileName);

			File file = new File(mFileDir, fileName);
			if (!file.exists())
			{
				file.createNewFile();
			}
			FileOutputStream fileStream = new FileOutputStream(file, true);
			fileStream.write(record.getBytes());
			fileStream.close();
		} catch (FileNotFoundException e)
		{
			sLogger.error("printToFile failed.\r\n{}/{} could not be created or does not exist", mFileDir, fileName, e);
		} catch (Exception e)
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
			} else if (mFileScope.equals("week"))
			{
				int week = calendar.get(Calendar.WEEK_OF_YEAR);
				fileName = new String(year + "-" + week + ".dbg");
			} else
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
		} catch (FileNotFoundException e)
		{
			sLogger.error("printDebugFile failed.\r\n{}/{} could not be created or does not exist", mFileDir, fileName, e);
		} catch (Exception e)
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
