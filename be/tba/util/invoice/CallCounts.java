package be.tba.util.invoice;

class CallCounts
{
   int InCalls = 0;
   int OutCalls = 0;
   int SmsCalls = 0;
   int FwdCalls = 0;
   int AgendaCalls = 0;
   int FaxCalls = 0;
   int LongCalls = 0;
   int LongCallSec = 0;
   int LongFwdCalls = 0;
   int LongFwdCallSec = 0;
   int Level3Calls = 0;
   int Level2Calls = 0;
   int Level1Calls = 0;
   int TotalCalls = 0;
   long LastInvoiceItem = 0L; // The new invoice entity must have a stop time
                              // value equal to the last call or task of that
                              // invoice
   /*
    * public CallCounts() { init(); }
    * 
    * public int getInCalls() { return InCalls; }
    * 
    * 
    * public void setInCalls(int inCalls) { InCalls = inCalls; }
    * 
    * 
    * public int getOutCalls() { return OutCalls; }
    * 
    * 
    * public void setOutCalls(int outCalls) { OutCalls = outCalls; }
    * 
    * 
    * public int getSmsCalls() { return SmsCalls; }
    * 
    * 
    * public void setSmsCalls(int smsCalls) { SmsCalls = smsCalls; }
    * 
    * 
    * public int getFwdCalls() { return FwdCalls; }
    * 
    * 
    * public void setFwdCalls(int fwdCalls) { FwdCalls = fwdCalls; }
    * 
    * 
    * public int getAgendaCalls() { return AgendaCalls; }
    * 
    * 
    * public void setAgendaCalls(int agendaCalls) { AgendaCalls = agendaCalls; }
    * 
    * 
    * public int getFaxCalls() { return FaxCalls; }
    * 
    * 
    * public void setFaxCalls(int faxCalls) { FaxCalls = faxCalls; }
    * 
    * 
    * public int getLongCalls() { return LongCalls; }
    * 
    * 
    * public void setLongCalls(int longCalls) { LongCalls = longCalls; }
    * 
    * 
    * public int getLongCallSec() { return LongCallSec; }
    * 
    * 
    * public void setLongCallSec(int longCallSec) { LongCallSec = longCallSec; }
    * 
    * 
    * public int getLongFwdCalls() { return LongFwdCalls; }
    * 
    * 
    * public void setLongFwdCalls(int longFwdCalls) { LongFwdCalls = longFwdCalls;
    * }
    * 
    * 
    * public int getLongFwdCallSec() { return LongFwdCallSec; }
    * 
    * 
    * public void setLongFwdCallSec(int longFwdCallSec) { LongFwdCallSec =
    * longFwdCallSec; }
    * 
    * 
    * public int getLevel3Calls() { return Level3Calls; }
    * 
    * 
    * public void setLevel3Calls(int level3Calls) { Level3Calls = level3Calls; }
    * 
    * 
    * public int getLevel2Calls() { return Level2Calls; }
    * 
    * 
    * public void setLevel2Calls(int level2Calls) { Level2Calls = level2Calls; }
    * 
    * 
    * public int getLevel1Calls() { return Level1Calls; }
    * 
    * 
    * public void setLevel1Calls(int level1Calls) { Level1Calls = level1Calls; }
    * 
    * 
    * public int getTotalCalls() { return TotalCalls; }
    * 
    * 
    * public void setTotalCalls(int totalCalls) { TotalCalls = totalCalls; }
    * 
    * 
    * public long getLastInvoiceItem() { return LastInvoiceItem; }
    * 
    * 
    * public void setLastInvoiceItem(long lastInvoiceItem) { LastInvoiceItem =
    * lastInvoiceItem; }
    * 
    * 
    * private void init() { InCalls = 0; OutCalls = 0; SmsCalls = 0; FwdCalls = 0;
    * AgendaCalls = 0; FaxCalls = 0; LongCalls = 0; LongCallSec = 0; LongFwdCalls =
    * 0; LongFwdCallSec = 0; Level3Calls = 0; Level2Calls = 0; Level1Calls = 0;
    * TotalCalls = 0; LastInvoiceItem = 0; }
    */
}
