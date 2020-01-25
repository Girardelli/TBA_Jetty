package be.tba.ejb.task.interfaces;

public class FileLocationData extends be.tba.util.data.AbstractData
{

   /**
    * 
    */
   private static final long serialVersionUID = 72598741679069278L;

   public static final int kInput = 1;
   public static final int kOutput = 2;
   
   public int id;
   public int workorderId;
   public int inOrOut;
   public int size;
   public String name;
   public String storagePath;
   
   public FileLocationData()
   {
      id = 0;
      workorderId = 0;
      inOrOut = kInput;
      size = 0;
      name = "";
      storagePath = "";
   }
   
   public FileLocationData(FileLocationData otherData) 
   {
      id = otherData.id;
      workorderId = otherData.workorderId;
      inOrOut = otherData.inOrOut;
      size = otherData.size;
      name = otherData.name;
      storagePath = otherData.storagePath;
   }

   @Override
   public String toValueString()
   {
      StringBuffer str = new StringBuffer();

      str.append("0,");
      str.append(workorderId);
      str.append(",");
      str.append(inOrOut);
      str.append(",");
      str.append(size);
      str.append(",'");
      str.append((this.name != null) ? escapeQuotes(this.name) : "");
      str.append("','");
      str.append((this.storagePath != null) ? escapeQuotes(this.storagePath) : "");
      str.append("'");
      return (str.toString());
   }

   @Override
   public String toNameValueString()
   {
      StringBuffer str = new StringBuffer();

      str.append("WorkorderId=");
      str.append(workorderId);
      str.append(",InOrOut=");
      str.append(inOrOut);
      str.append(",Size=");
      str.append(size);
      str.append(",Name='");
      str.append((this.name != null) ? escapeQuotes(this.name) : "");
      str.append("',StoragePath='");
      str.append((this.storagePath != null) ? this.storagePath : "");
      str.append("'");
      return (str.toString());
   }

   @Override
   public int getId()
   {
      // TODO Auto-generated method stub
      return id;
   }

}
