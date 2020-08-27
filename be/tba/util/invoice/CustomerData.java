package be.tba.util.invoice;

public class CustomerData
{
    private int mId = 0;
    private String mTAV = "";
    private String mName = "";
    private String mAddress1 = "";
    private String mAddress2 = "";
    private String mBtwNr = "";
    private int mTaskHourRate = 0;

    public CustomerData()
    {

    }

    public int getId()
    {
        return mId;
    }

    public void setId(int id)
    {
        this.mId = id;
    }

   public String getTAV()
    {
        return mTAV;
    }

    public void setTAV(String tav)
    {
        this.mTAV = tav;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public String getAddress1()
    {
        return mAddress1;
    }

    public void setAddress1(String address)
    {
        this.mAddress1 = address;
    }

    public String getAddress2()
    {
        return mAddress2;
    }

    public void setAddress2(String address)
    {
        this.mAddress2 = address;
    }

    public String getBtwNr()
    {
        return mBtwNr;
    }

    public void setBtwNr(String btwNr)
    {
        this.mBtwNr = btwNr;
        //log.info("CustomerData.setBtwNr(" + btwNr + ")");
    }

    public int getTaskHourRate()
    {
        return mTaskHourRate;
    }

    public void setTaskHourRate(int taskHourRate)
    {
        this.mTaskHourRate = taskHourRate;
    }

}
