package be.tba.util.data;

public class FintroPayment
{
    public String id;
    public String payDate;
    public String valutaDate;
    public double amount;
    public String accountNrCustomer;
    public String details;
    
    public String toString()
    {
        return id + ";" + payDate + ";" + valutaDate + ";" + amount+ ";" + accountNrCustomer;
    }
}
