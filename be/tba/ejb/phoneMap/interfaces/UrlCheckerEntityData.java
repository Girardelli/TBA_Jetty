package be.tba.ejb.phoneMap.interfaces;


public class UrlCheckerEntityData extends be.tba.util.data.AbstractData implements java.io.Serializable
{
   private static final long serialVersionUID = 1L;
   public int id;
   public long secondsOut;
   public int year;
   public int month;
   public int hickUps;
   
   public UrlCheckerEntityData()
   {
      id = 0;
      secondsOut = 0;
      year = 0;
      month = 0;
      hickUps = 0;
   }

   public UrlCheckerEntityData(int year, int month, long secondsOut, int hickUps)
   {
      id = 0;
      this.secondsOut = secondsOut;
      this.year = year;
      this.month = month;
      this.hickUps = hickUps;
   }

   public String toNameValueString()
   {
       StringBuffer str = new StringBuffer();
       str.append("SecondsOut=");
       str.append(this.secondsOut);
       str.append(",Year=");
       str.append(this.year);
       str.append(",Month=");
       str.append(this.month);
       str.append(",HickUps=");
       str.append(this.hickUps);
       return (str.toString());
   }

   public String toValueString()
   {
       StringBuffer str = new StringBuffer();

       str.append("'0',");
       str.append(this.secondsOut);
       str.append(",");
       str.append(this.year);
       str.append(",");
       str.append(this.month);
       str.append(",");
       str.append(this.hickUps);
       return (str.toString());
   }

   
   public boolean equals(Object pOther)
   {
       if (pOther instanceof UrlCheckerEntityData)
       {
          UrlCheckerEntityData lTest = (UrlCheckerEntityData) pOther;
           return this.id == lTest.id && this.year == lTest.year && this.month == lTest.month;
       }
       return false;
   }

   public int hashCode()
   {
       return 37 + (int) id + month + year;
   }

   @Override
   public int getId()
   {
      // TODO Auto-generated method stub
      return this.id;
   }

}
