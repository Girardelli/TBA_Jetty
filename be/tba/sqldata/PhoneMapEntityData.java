package be.tba.sqldata;

public class PhoneMapEntityData extends be.tba.sqldata.AbstractData implements java.io.Serializable
{
   private static final long serialVersionUID = 1L;
   public int id;
   public String phoneId;
   public String userId;

   public PhoneMapEntityData()
   {
      id = 0;
      phoneId = "";
      userId = "";
   }

   public String toNameValueString()
   {
      StringBuilder str = new StringBuilder();
      str.append("phoneId='");
      str.append((this.phoneId != null) ? this.phoneId : "");
      str.append("',userId='");
      str.append((this.userId != null) ? this.userId : "");
      str.append("'");
      return (str.toString());
   }

   public String toValueString()
   {
      StringBuilder str = new StringBuilder();

      str.append("'0','");
      str.append((this.phoneId != null) ? this.phoneId : "");
      str.append("','");
      str.append((this.userId != null) ? this.userId : "");
      str.append("'");
      return (str.toString());
   }

   public boolean equals(Object pOther)
   {
      if (pOther instanceof PhoneMapEntityData)
      {
         PhoneMapEntityData lTest = (PhoneMapEntityData) pOther;
         boolean lEquals = true;

         lEquals = lEquals && this.id == lTest.id;
         if (this.phoneId == null)
         {
            lEquals = lEquals && (lTest.phoneId == null);
         }
         else
         {
            lEquals = lEquals && this.phoneId.equals(lTest.phoneId);
         }
         return lEquals;
      }
      return false;
   }

   public int hashCode()
   {
      int result = 17;

      result = 37 * result + (int) id;
      result = 37 * result + ((this.phoneId != null) ? this.phoneId.hashCode() : 0);
      return result;
   }

   @Override
   public int getId()
   {
      // TODO Auto-generated method stub
      return this.id;
   }

}
