package be.tba.util.invoice;

public class SubcustomerCost
{
    private int mAccountId;
	private int mCalls = 0;
    private double mCallCost = 0.0;
    private int mTasks = 0;
    private double mTaskCost = 0.0;
    private String mName = "";
    //private String mFwdNr = "";

    public SubcustomerCost(int id)
    {
    	mAccountId = id;
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

    public int getAccountId()
    {
        return mAccountId;
    }

    public void setAccountId(int id)
    {
        this.mAccountId = id;
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

        return (this.mAccountId == bSubcustomerCost.mAccountId);
    }

}
