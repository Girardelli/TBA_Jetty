package be.tba.pbx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.CommPortOwnershipListener;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallLogThread extends Thread implements SerialPortEventListener, CommPortOwnershipListener
{
   final static Logger sLogger = LoggerFactory.getLogger(CallLogThread.class);
   private static final String mVersion = "ver 1.4";

   private static final int SLEEP_TIME = 5000; // 5 seconden

   private static String mFileDir = "";

   private static String mFileScope; // month, week, day

   private static CommPortIdentifier mPortId;

   private static Enumeration mPortList;

   private InputStream mInputStream;

   private SerialPort mSerialPort;

   private byte[] mByteBuffer;

   private StringBuffer mReadStrBuf;

   private boolean mStopThread = false;

   private boolean mIsOpen = false;;

   public CallLogThread()
   {
      // super("myCallLogThread");
      mByteBuffer = new byte[2048]; // call record is expected to be 148
      // bytes long
      mReadStrBuf = new StringBuffer();
      mIsOpen = false;
   }

   public void run()
   {
      sLogger.info("CallLogThread.run()");
      mPortList = CommPortIdentifier.getPortIdentifiers();
      sLogger.info("CommPortIdentifier retrieved");
	  int i = 0;
	  while (mPortList.hasMoreElements())
      {
		  i++;
		  sLogger.info("Com port found");
         mPortId = (CommPortIdentifier) mPortList.nextElement();
         sLogger.info("port name: " + mPortId.getName());
         if (mPortId.getPortType() == CommPortIdentifier.PORT_SERIAL)
         {
            try
            {
               if (mPortId.getName().equals("COM1"))
               // if (mPortId.getName().equals("/dev/term/a"))
               {
                  try
                  {
                     mSerialPort = (SerialPort) mPortId.open("CallLogThread", 2000);
                     mIsOpen = true;
                  }
                  catch (PortInUseException e)
                  {
                	  sLogger.error("portId.open failed. Owned by {}", mPortId.getCurrentOwner());
                     throw e;
                  }
                  try
                  {
                     mInputStream = mSerialPort.getInputStream();
                  }
                  catch (IOException e)
                  {
                	  sLogger.error("mSerialPort.getInputStream() failed");
                     throw e;
                  }
                  try
                  {
                     mSerialPort.addEventListener(this);
                  }
                  catch (TooManyListenersException e)
                  {
                	  sLogger.error("mSerialPort.addEventListener() failed");
                     throw e;
                  }
                  mSerialPort.notifyOnDataAvailable(true);
                  try
                  {
                     mSerialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                  }
                  catch (UnsupportedCommOperationException e)
                  {
                	  sLogger.error("mSerialPort.setSerialPortParams() failed");
                     throw e;
                  }

                  // Add ownership listener to allow ownership event
                  // handling.
                  mPortId.addPortOwnershipListener(this);

                  sLogger.info("CallLogThread running");

                  // for (;;) Thread.sleep(10000);

                  /*
                   * while (!mStopThread) { if (i++ > 100) { i = 0; cleanDB(); }
                   * Thread.sleep(SLEEP_TIME); } mSerialPort.close(); mIsOpen =
                   * false; System.out.println("CallLogThread stopped!!");
                   */
               }
               /*
                * catch(InterruptedException e) { e.printStackTrace(); }
                */
            }
            catch (Exception e)
            {
            	sLogger.error("Can not open serial port", e);
            }

         }
		 else
		 {
			 sLogger.error("port not of type COM");
		 }
      }
	  sLogger.info("processed " + i + "ports");
   }

   public void serialEvent(SerialPortEvent event)
   {
      switch (event.getEventType())
      {
      case SerialPortEvent.BI:
      case SerialPortEvent.OE:
      case SerialPortEvent.FE:
      case SerialPortEvent.PE:
      case SerialPortEvent.CD:
      case SerialPortEvent.CTS:
      case SerialPortEvent.DSR:
      case SerialPortEvent.RI:
      case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
         break;

      case SerialPortEvent.DATA_AVAILABLE:
         try
         {
            int numBytes = 0;
            while (mInputStream.available() > 0)
            {
               numBytes += mInputStream.read(mByteBuffer);
            }
            // for debug
            // printDebugFile(numBytes);
            String convert = new String(mByteBuffer, 0, numBytes);
            mReadStrBuf.append(convert);

            int eol = 0;
            while ((eol = mReadStrBuf.indexOf(System.getProperty("line.separator"))) >= 0)
            {
               eol += 2;
               convert = mReadStrBuf.substring(0, eol);
               Forum700CallRecord record = new Forum700CallRecord(convert);
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
               }
               else
               {
                  mReadStrBuf.delete(0, eol);
               }
            }
         }
         catch (Exception e)
         {
        	 sLogger.error("Incomming event can not be processed.", e);
            return;
         }
         break;
      }
   }

   public void ownershipChange(int type)
   {
      switch (type)
      {
      case CommPortOwnershipListener.PORT_OWNED:
      case CommPortOwnershipListener.PORT_UNOWNED:
         break;

      case CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED:
         mSerialPort.removeEventListener();
         mSerialPort.close();
         mIsOpen = false;
         sLogger.info("Ownership passed further to requster.");
         break;
      }
   }

   public void destroy()
   {
      mSerialPort.removeEventListener();
      mSerialPort.close();
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
      try
      {
         Calendar calendar = Calendar.getInstance();

         int year = calendar.get(Calendar.YEAR);
         int month = calendar.get(Calendar.MONTH) + 1;
         String fileName;
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

         sLogger.info("Open file: dir={}; filename={}", mFileDir, fileName);

         File file = new File(mFileDir, fileName);
         if (!file.exists())
            file.createNewFile();
         FileOutputStream fileStream = new FileOutputStream(file, true);
         fileStream.write(record.getBytes());
         fileStream.close();
      }
      catch (Exception e)
      {
    	  sLogger.error("printToFile failed", e);
         return;
      }
   }

   private void printDebugFile(int size)
   {
      try
      {
         Calendar calendar = Calendar.getInstance();

         int year = calendar.get(Calendar.YEAR);
         int month = calendar.get(Calendar.MONTH) + 1;
         String fileName;
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
            file.createNewFile();
         FileOutputStream fileStream = new FileOutputStream(file, true);

         String tmp = new String("\r\nLen=" + size + " ");
         fileStream.write(tmp.getBytes());
         fileStream.write(mByteBuffer, 0, size);
         fileStream.close();
      }
      catch (Exception e)
      {
    	  sLogger.error("printDebugFile failed", e);
          return;

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
