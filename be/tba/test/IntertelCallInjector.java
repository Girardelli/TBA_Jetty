package be.tba.test;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

//import be.tba.util.timer.Session;

public class IntertelCallInjector 
{
   private static Logger log = LoggerFactory.getLogger(IntertelCallInjector.class);
 
   
   static String[][] kCallSessions = { {
		// answered call ended by caller
	"phone_number_to=3214402105&inout=IN&origin=start&phone_number_from=32473949777&knummer=190313&hash=891f40283a842b5e3c22e259e062290eb9a619a42fae6a1691d3b2590feeb379&call_id=**id**&timestamp=**ts**",
	"phone_number_to=3214402105&inout=IN&origin=answer&phone_number_from=32473949777&knummer=190313&answerby=aoelsaiyiiteysvnchbe794&hash=f3f1cd2772904371e497aacface994059433768b93c1d7fdfac077c6c1cbee80&call_id=**id**&timestamp=**ts**",
	"phone_number_to=3214402105&inout=IN&origin=end&phone_number_from=32473949777&knummer=190313&hash=02c5d915f06c05208f6733e8bb9997fd4e19b9cc1ae51d1003d5c54d323c56be&call_id=**id**&timestamp=**ts**",
	"call_start_date=2019-03-30&call_end_time=10:41:19&answerby=aoelsaiyiiteysvnchbe794&origin=summary&call_time=8&knummer=190313&call_id=**id**&phone_number_to=3214402105&inout=IN&call_answer_time=10:41:11&call_answer_date=2019-03-30&answeredby_name=TEST&phone_number_from=32473949777&call_end_date=2019-03-30&call_start_time=10:41:03&hash=93a5f47e6b90437ec7615866adb6162a3d0b570d428c38ff0b4d1ed0bcc1f358&timestamp=**ts**&status=answered",
	""
	},{
		//
	"phone_number_to=3214402109&inout=IN&origin=start&phone_number_from=32474986745&knummer=190313&hash=2093ba925afa355fa494923e67e5feb7a3b4315b81ef62c2ab46506ad3644604&call_id=**id**&timestamp=**ts**",
	"phone_number_to=3214402109&inout=IN&origin=answer&phone_number_from=32474986745&knummer=190313&answerby=aoelsaiyiiteysvnchbe794&hash=4cf2181743bb30ad12e60370786ee51bd4530738b61a50ef14d1d41ebfdcdf99&call_id=**id**&timestamp=**ts**",
	"viaDID=3214402109&call_start_date=2019-03-30&call_end_time=10:40:04&answerby=aoelsaiyiiteysvnchbe794&origin=summary&call_time=18&knummer=190313&call_id=**id**&phone_number_to=3214402109&inout=IN&call_answer_time=10:39:46&call_answer_date=2019-03-30&answeredby_name=TEST&phone_number_from=32474986745&call_end_date=2019-03-30&call_start_time=10:39:31&hash=bb99e4da2874a0ac2334324bc245bead93679666eb7cbcc17221a359535dfdd2&timestamp=**ts**&status=answered",
	"phone_number_to=3214402109&inout=IN&origin=end&phone_number_from=32474986745&knummer=190313&hash=8a313e735d829ae4569e6340cee8c2a32cc1f50eebe446a220e263a65f731e4c&call_id=**id**&timestamp=**ts**",
	""
	},{
		// missed call
	"phone_number_to=3214402108&inout=IN&origin=start&phone_number_from=322345768&knummer=190313&hash=9f0bbaa720b0529297733af3151265e994080dd10df498a44435b4b997c6d3ac&call_id=**id**&timestamp=**ts**",
	"viaDID=3214402108&call_start_date=2019-03-30&call_end_time=10:28:14&origin=summary&call_time=0&knummer=190313&call_id=**id**&phone_number_to=3214402108&inout=IN&call_answer_time=0&call_answer_date=0&phone_number_from=322345768&call_end_date=2019-03-30&call_start_time=10:28:00&hash=aa0a543468e6f0072e3d2f25367e7088f9ad7fede80409cc939f69234a5e606f&timestamp=**ts**&status=unanswered",
	"phone_number_to=3214402108&inout=IN&origin=end&phone_number_from=322345768&knummer=190313&hash=f9d84d2f26d019525d43b36908d3b77f71da33e54424bb70432f4e16d4344aad&call_id=**id**&timestamp=**ts**",
	""
	} };
	
	
	private static class CallTestThread extends Thread
	{
		String[] mPosts;
		String mIdPrefix;
		static final private int kCycles = 1;
		static final private int kEventPeriodMin = 2000; // 10 seconds
		static final private int kEventPeriodMax = 5000;
      
		public CallTestThread(String[] posts, String idPrefix)
		{
			mPosts = posts;
			mIdPrefix = idPrefix;
		}
		
		public void run()
		{
			for (int i = 0; i < kCycles; ++i)
			{
				String callId = mIdPrefix + "-" + i + "-" + UUID.randomUUID().toString();
				int y = 0;
				while (!mPosts[y].isBlank())
				{
					try 
					{
						Thread.sleep(ThreadLocalRandom.current().nextLong(kEventPeriodMin, kEventPeriodMax));
					} catch (InterruptedException e) 
					{
						// TODO Auto-generated catch block
						log.error(e.getMessage(), e);
					}
					
					try {
					   long tsNow = System.currentTimeMillis() / 1000l;
						String cmd = "cmd.exe /c C:\\java\\curl\\curl-7.64.0-win64-mingw\\bin\\curl --trace-ascii C:\\Yves\\curl\\curl-" + callId + y + ".log -d \"" + mPosts[y] + "\" -X  POST http://localhost:8080/tba/intertel";
						cmd = cmd.replace("**id**", callId);
						cmd = cmd.replace("**ts**", Long.toString(tsNow));
                  
						Process proc = Runtime.getRuntime().exec(cmd);
						int ind = cmd.indexOf("origin=");
						String phase = cmd.substring(ind + 7, ind + 13);
						log.info(mIdPrefix + "-" + i + "-" + phase);
						int exitCode = proc.waitFor();
			            if (exitCode != 0) 
			            {
			                throw new IOException(phase + ": Command exited with " + exitCode);
			            }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error(e.getMessage(), e);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						log.error(e.getMessage(), e);
					}
					++y;
				}
			}
		}
		
	}

	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
	   
        Thread t1 = new Thread(new CallTestThread(kCallSessions[0], "1111"));
        t1.start();

       Thread t2 = new Thread(new CallTestThread(kCallSessions[1], "2222"));
        t2.start();
		
        Thread t3 = new Thread(new CallTestThread(kCallSessions[2], "3333"));
        t3.start();
        
		
	}


}
