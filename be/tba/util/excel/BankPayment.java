package be.tba.util.excel;

public class BankPayment
{
   public String id;
   public String payDate;
   public String valutaDate;
   public double amount;
   public String accountNrCustomer;
   public String details;

   public String toString()
   {
      return id + ";" + payDate + ";" + valutaDate + ";" + amount + ";" + accountNrCustomer + ";" + details;
   }
   
   static String normalizeAmount(String amount)
   {
      if (amount.contains(","))
      {
         // , notation: 1.644,39
         // check also for '.' thousands separators
         while (amount.contains("."))
         {
            amount = amount.replace(".", "");
         }
         amount = amount.replace(",", ".");
      }
      return amount;
   }
}
