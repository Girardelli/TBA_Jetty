package be.tba.test;

import java.sql.SQLException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.Vector;

import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.ejb.invoice.session.InvoiceSqlAdapter;
import be.tba.servlets.session.WebSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseFixer
{
   final static Logger sLogger = LoggerFactory.getLogger(DatabaseFixer.class);

   WebSession mSession;
   InvoiceSqlAdapter mInvoiceSession;
   
   public DatabaseFixer()
   {
      try
      {
         mSession = new WebSession();
      }
      catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      mInvoiceSession = new InvoiceSqlAdapter();
   }
   
   
   public static void main(String args[]) throws Exception 
   {
      DatabaseFixer fixer = new DatabaseFixer();
      fixer.doIt(args);
   }

   
   private void doIt(String args[])
   {
      int i = 0;
      try
      {
         SortedSet<InvoiceEntityData> invoices = mInvoiceSession.getAllRowsSorted(mSession);
         Collection<InvoiceEntityData> dateFormattedEntries = new Vector<InvoiceEntityData>();
         
         String curDay = "";
         String curMonth = "";
         boolean isBadList = false;
         for (InvoiceEntityData entry : invoices) 
         {
            ++i;
            if (entry.getPayDate().length() > 10 && entry.getPayDate().charAt(2) == '-')
            {
               String nextCurDay = entry.getPayDate().substring(0, 2);
               String nextCurMonth = getMonthNr(entry.getPayDate());
               if (entry.getInvoiceDate().length() > 7)
               {
                  String invDay = entry.getInvoiceDate().substring(0, entry.getInvoiceDate().indexOf('/'));

                  String nextYear = entry.getPayDate().substring(7);
                  
                  if ((Integer.parseInt(nextCurDay) + Integer.parseInt(nextCurMonth)*30 + Integer.parseInt(nextYear)*365) <
                        (Integer.parseInt(invDay) + entry.getMonth()*30 + entry.getYear()*365))
                  {
                     // al zeker fout
                     sLogger.info("ASAP {}: inv: {} > payed: {}", entry.fintroId, entry.getInvoiceDate(), entry.getPayDate());
//                     changeDB(entry);
//                     continue;
                     isBadList = true;
                  }
               }
               else
               {
                  sLogger.info("Hoe kan dat ##### {}: inv: {} > payed: {}", entry.fintroId, entry.getInvoiceDate(), entry.getPayDate());
                                    
               }

               if (nextCurDay.equals(curDay) && !nextCurMonth.equals(curMonth))
               {
                  // prijs. deze reeks moet geconverteerd worden
                  isBadList = true;
               }
               curDay = nextCurDay;
               curMonth = nextCurMonth;
               dateFormattedEntries.add(entry);
            }
            else if (!dateFormattedEntries.isEmpty())
            {
               if (isBadList)
               {
                  // process the list
                  for (InvoiceEntityData badOne : dateFormattedEntries) 
                  {
                     changeDB(badOne);
                  }
                  isBadList = false;
               }
               dateFormattedEntries.clear();
               curDay = "";
               curMonth = "";
            }
            
            
            //sLogger.info("{} fintroId={}, paydate={}", ++i, entry.fintroId, entry.getPayDate());
         } 
         if (!dateFormattedEntries.isEmpty())
         {
            if (isBadList)
            {
               // process the list
               for (InvoiceEntityData badOne : dateFormattedEntries) 
               {
                  changeDB(badOne);
               }
            }
         }
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }
   
   private void changeDB(InvoiceEntityData badOne) throws Exception
   {
      sLogger.info("{}: {} --> {}/{}/{}", badOne.fintroId, badOne.getPayDate(), getMonthNr(badOne.getPayDate()), badOne.getPayDate().substring(0, 2), badOne.getPayDate().substring(7));
      badOne.setPayDate(String.format("%s/%s/%s", getMonthNr(badOne.getPayDate()), badOne.getPayDate().substring(0, 2), badOne.getPayDate().substring(7)));
      badOne.setValutaDate(badOne.getPayDate());
      mInvoiceSession.setPaymentDates(mSession, badOne);
      
   }
   
   // input: 05-Feb-2019
   // output: 02
   private String getMonthNr(String str) throws Exception
   {
      //sLogger.info("parse {}", str.substring(3, 6));
      switch (str.substring(3, 6))
      {
      
      case "Jan":
         return "01";
      case "Feb":
         return "02";
      case "Mar":
         return "03";
      case "Apr":
         return "04";
      case "May":
         return "05";
      case "Jun":
         return "06";
      case "Jul":
         return "07";
      case "Aug":
         return "08";
      case "Sep":
         return "09";
      case "Oct":
         return "10";
      case "Nov":
         return "11";
      case "Dec":
         return "12";
      default:
         throw new Exception("cannot parse " + str);
      }
   }
   

}
