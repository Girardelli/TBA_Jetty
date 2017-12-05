package be.tba.util.invoice;

public class SubcustomerCost
{
    private int mCalls = 0;
    private double mCallCost = 0.0;
    private int mTasks = 0;
    private double mTaskCost = 0.0;
    private String mName = "";
    private String mFwdNr = "";

    public SubcustomerCost(String fwdNr)
    {
        mFwdNr = fwdNr;
    }

    public int getCalls()
    {
        return mCalls;
    }

    public void setCalls(int calls)
    {
        this.mCalls = calls;
    }

    public double getCallCost()
    {
        return mCallCost;
    }

    public void setCallCost(double callCost)
    {
        this.mCallCost = callCost;
    }

    public int getTasks()
    {
        return mTasks;
    }

    public void setTasks(int tasks)
    {
        this.mTasks = tasks;
    }

    public double getTaskCost()
    {
        return mTaskCost;
    }

    public void setTaskCost(double taskCost)
    {
        this.mTaskCost = taskCost;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public String getFwdNr()
    {
        return mFwdNr;
    }

    public void setFwdNr(String fwdNr)
    {
        this.mFwdNr = fwdNr;
    }

    @Override
    public boolean equals(Object b)
    {
        if (b == null)
            return false;
        if (b == this)
            return true;
        if (!(b instanceof SubcustomerCost))
            return false;
        SubcustomerCost bSubcustomerCost = (SubcustomerCost) b;

        return this.mFwdNr.equals(bSubcustomerCost.mFwdNr);
    }

}
