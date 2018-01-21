/*mRawCollection =
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.session.AccountSqlAdapter;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.data.MailTriggerData;

final public class AccountCache
{
    private AccountSqlAdapter mAccountAdapter = new AccountSqlAdapter();
    private Map<String, AccountEntityData> mFwdKeyList;
    private Collection<AccountEntityData> mRawCollection;
    private SortedMap<String, AccountEntityData> mNameSortedList; // without9000.
                                                                  // 9001 and
                                                                  // 9002
    private SortedMap<String, AccountEntityData> mNameSortedFullList;
    private Map<String, Collection<AccountEntityData>> mSubCustomersLists;
    private Map<String, AccountEntityData> mEmployeeLists;
    private Map<Integer, Collection<AccountEntityData>> mMailingGroups;
    private int mLastMailTime = 0;

    /*
     * class AccountNamesComparator implements Comparator { public int
     * compare(Object aFwdNr1, Object aFwdNr2) {
     *
     *
     *
     * AccountEntityData data1 = (AccountEntityData) aData1; AccountEntityData data2
     * = (AccountEntityData) aData2; if (data1.getFullName() != null &&
     * data2.getFullName() != null) return
     * data1.getFullName().compareToIgnoreCase(data1.getFullName()); return 0; }
     *
     * public boolean equals(Object aComparator) {
     *
     * return this.equals((AccountNamesComparator) aComparator); } }
     */

    private static AccountCache mInstance = null;

    public static AccountCache getInstance()
    {
        if (mInstance == null)
        {
            InitialContext ctx;
            Connection con = null;
            try
            {
                //ctx = new InitialContext();
                //DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/MySqlDS");
                //con = ds.getConnection();
                con = DriverManager.getConnection(Constants.MYSQL_URL);
                mInstance = new AccountCache();
                mInstance.update(con);
            }
/*            catch (NamingException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
*/            catch (SQLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            finally
            {
                if (con != null)
                {
                    try
                    {
                        con.close();
                    }
                    catch (SQLException ex)
                    {
                        // TODO Auto-generated catch block
                        System.out.println("FAILED update AccountCash");
                        System.out.println("SQLException: " + ex.getMessage());
                        System.out.println("SQLState: " + ex.getSQLState());
                        System.out.println("VendorError: " + ex.getErrorCode());
                        ex.printStackTrace();
                    }
                }

            }
        }
        return mInstance;
    }

    public static AccountCache getInstance(String mysqlURL)
    {
        if (mInstance == null)
        {
            Connection con = null;
            try
            {
                con = DriverManager.getConnection(mysqlURL);
                mInstance = new AccountCache();
                mInstance.update(con);
            }
            catch (SQLException ex)
            {
                // TODO Auto-generated catch block
                System.out.println("FAILED update AccountCash");
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                ex.printStackTrace();
            }
            finally
            {
                if (con != null)
                {
                    try
                    {
                        con.close();
                    }
                    catch (SQLException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

        }
        return mInstance;
    }

    private AccountCache()
    {
        mNameSortedList = Collections.synchronizedSortedMap(new TreeMap<String, AccountEntityData>());
        mNameSortedFullList = Collections.synchronizedSortedMap(new TreeMap<String, AccountEntityData>());
        mFwdKeyList = new HashMap<String, AccountEntityData>();
        mSubCustomersLists = new HashMap<String, Collection<AccountEntityData>>();
        mEmployeeLists = new HashMap<String, AccountEntityData>();
        mMailingGroups = new HashMap<Integer, Collection<AccountEntityData>>();
        mLastMailTime = 0;

    }

    public void update(Connection con)
    {
        mRawCollection = Collections.synchronizedCollection(mAccountAdapter.getAllRows(con));
        converToHashMap(mRawCollection);
        buildMailingGroups();
    }

    public AccountEntityData get(String fwdNumber)
    {
        return (AccountEntityData) mFwdKeyList.get(fwdNumber);
    }

    public Collection<AccountEntityData> getEmployeeList()
    {
        return mEmployeeLists.values();
    }

    public Collection<AccountEntityData> getSubCustomersList(String superCustomer)
    {
        return mSubCustomersLists.get(superCustomer);
    }

    public Collection<String> getSuperCustomersList()
    {
        return mSubCustomersLists.keySet();
    }

    public Collection<AccountEntityData> getCustomerList()
    {
        return mNameSortedList.values();
    }

    public Collection<AccountEntityData> getInvoiceCustomerList()
    {
        Vector<AccountEntityData> vInvoiceCustomers = new Vector<AccountEntityData>();

        for (Iterator<AccountEntityData> i = getCustomerList().iterator(); i.hasNext();)
        {
            AccountEntityData vEntry = i.next();
            if (vEntry.getNoInvoice())
                continue;
            vInvoiceCustomers.add(vEntry);
        }
        return vInvoiceCustomers;
    }

    public Collection<AccountEntityData> getAccountListWithoutTbaNrs()
    {
        return mNameSortedList.values();
    }

    private void converToHashMap(Collection<AccountEntityData> rawList)
    {
        mNameSortedList.clear();
        mFwdKeyList.clear();
        mNameSortedFullList.clear();
        mSubCustomersLists.clear();
        mEmployeeLists.clear();
        Vector<String> vSuperCustomers = new Vector<String>();
        AccountEntityData vEntry = null;

        for (Iterator<AccountEntityData> i = rawList.iterator(); i.hasNext();)
        {
            vEntry = i.next();
            if (vEntry.getRole().equals(AccountRole._vCustomer) || vEntry.getRole().equals(AccountRole._vSubCustomer))
            {
                // vMap.put(vEntry.getFwdNumber(), vEntry);
                mNameSortedList.put(vEntry.getFullName(), vEntry);
                mFwdKeyList.put(vEntry.getFwdNumber(), vEntry);
                mNameSortedFullList.put(vEntry.getFullName(), vEntry);
                if (vEntry.getHasSubCustomers())
                {
                    Collection<AccountEntityData> vSubCustomerList = mSubCustomersLists.get(vEntry.getFwdNumber());
                    if (vSubCustomerList == null)
                    {
                        vSubCustomerList = new Vector<AccountEntityData>();
                        mSubCustomersLists.put(vEntry.getFwdNumber(), vSubCustomerList);
                        // System.out.println("added sub klanten lijst voor " + vEntry.getFullName() +
                        // ". Super klanten lijst lengte=" + mSubCustomersLists.size());
                    }
                }

                if (vEntry.getRole().equals(AccountRole._vSubCustomer) && vEntry.getSuperCustomer().length() > 0)
                {
                    // add to mSubCustomersLists
                    Collection<AccountEntityData> vSubCustomerList = mSubCustomersLists.get(vEntry.getSuperCustomer());
                    if (vSubCustomerList == null)
                    {
                        vSubCustomerList = new Vector<AccountEntityData>();
                        mSubCustomersLists.put(vEntry.getSuperCustomer(), vSubCustomerList);
                        vSuperCustomers.add(vEntry.getSuperCustomer());
                        // System.out.println("created new super customer " +
                        // vEntry.getSuperCustomer());
                    }
                    vSubCustomerList.add(vEntry);
                    // System.out.println("added sub-klant onder " + vEntry.getSuperCustomer());
                    // System.out.println("subcustomer list voor " + vEntry.getSuperCustomer() + "
                    // is size " + vSubCustomerList.size());
                }
            }
            else if (vEntry.getRole().equals(AccountRole._vAdminstrator) || vEntry.getRole().equals(AccountRole._vEmployee))
            {
                // add to mEmployeeLists
                mEmployeeLists.put(vEntry.getFullName(), vEntry);
                mFwdKeyList.put(vEntry.getFwdNumber(), vEntry);
                // System.out.println("added employee " + vEntry.getFullName() + ", " +
                // vEntry.getId() + ", " + vEntry.getUserId());
            }
        }
        // set the HasSubCustomer flags
        Set<String> superCustomers = mSubCustomersLists.keySet();
        for (Iterator<String> i = superCustomers.iterator(); i.hasNext();)
        {
            String vSuperCustFwdNr = i.next();
            AccountEntityData vSuperCust = get(vSuperCustFwdNr);
            if (vSuperCust != null)
            {
                vSuperCust.setHasSubCustomers(true);
            }
        }

        // System.out.println("Before new mNameSortedList");
        // mNameSortedList = new TreeMap(new AccountNamesComparator());

        /*
         * Collection<AccountEntityData> list = getAccountListWithoutTbaNrs();
         * synchronized (list) { for (Iterator<AccountEntityData> vIter =
         * list.iterator(); vIter.hasNext();) { AccountEntityData vEntry =
         * (AccountEntityData) vIter.next(); //System.out.println(vEntry.getFullName());
         * } }
         */

        for (int i = 0; i < 3; ++i)
        {
            AccountEntityData vEntryCnst = new AccountEntityData();
            vEntryCnst.setFullName(Constants.NUMBER_BLOCK[i][3]);
            vEntryCnst.setFwdNumber(Constants.NUMBER_BLOCK[i][0]);
            vEntryCnst.setIs3W(false);
            vEntryCnst.setMailMinutes1((short) 0);
            vEntryCnst.setMailHour1((short) 0);
            vEntryCnst.setMailMinutes2((short) 0);
            vEntryCnst.setMailHour2((short) 0);
            vEntryCnst.setMailMinutes3((short) 0);
            vEntryCnst.setMailHour3((short) 0);
            mNameSortedFullList.put(Constants.NUMBER_BLOCK[i][3], vEntryCnst);
            mFwdKeyList.put(Constants.NUMBER_BLOCK[i][0], vEntryCnst);
        }
    }

    public Collection<String> getFreeNumbers()
    {
        try
        {
            Vector<String> vFreeNumbers = new Vector<String>();
            for (int i = 2; i < Constants.NUMBER_BLOCK.length; i++)
                vFreeNumbers.add(Constants.NUMBER_BLOCK[i][0]);
            for (Iterator<AccountEntityData> i = mRawCollection.iterator(); i.hasNext();)
            {
                vFreeNumbers.remove((i.next()).getFwdNumber());
            }
            return vFreeNumbers;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<String>();
    }

    public String idToFwdNr(int id)
    {
        for (Iterator<AccountEntityData> i = mRawCollection.iterator(); i.hasNext();)
        {
            AccountEntityData entry = i.next();
            // System.out.println("idToFwdNr: id=" + id + "entry.getId()=" +
            // entry.getId());
            if (id == entry.getId())
            {
                // System.out.println("idToFwdNr: found!! fwdnr=" +
                // entry.getFwdNumber());
                return entry.getFwdNumber();
            }
        }
        return null;
    }

    /**
     * @ejb:interface-method view-type="remote"
     */
    public Vector<MailTriggerData> getAccountTriggers()
    {
        try
        {
            Vector<MailTriggerData> vTriggerList = new Vector<MailTriggerData>();
            for (Iterator<AccountEntityData> i = mRawCollection.iterator(); i.hasNext();)
            {
                AccountEntityData vEntry = i.next();
                if (vEntry.getMailHour1() != 0 || vEntry.getMailMinutes1() != 0)
                    vTriggerList.add(new MailTriggerData(vEntry.getFwdNumber(), vEntry.getMailHour1(), vEntry.getMailMinutes1()));
                if (vEntry.getMailHour2() != 0 || vEntry.getMailMinutes2() != 0)
                    vTriggerList.add(new MailTriggerData(vEntry.getFwdNumber(), vEntry.getMailHour2(), vEntry.getMailMinutes2()));
                if (vEntry.getMailHour3() != 0 || vEntry.getMailMinutes3() != 0)
                    vTriggerList.add(new MailTriggerData(vEntry.getFwdNumber(), vEntry.getMailHour3(), vEntry.getMailMinutes3()));
            }
            return vTriggerList;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Vector<MailTriggerData>();
    }

    public boolean isMailEnabled(AccountEntityData entry)
    {
        boolean vRes = false;

        if ((entry.getMailHour1() > 0 && entry.getMailHour1() <= Constants.MAX_MAIL_HOUR) || (entry.getMailHour2() > 0 && entry.getMailHour2() <= Constants.MAX_MAIL_HOUR) || (entry.getMailHour3() > 0 && entry.getMailHour3() <= Constants.MAX_MAIL_HOUR))
            vRes = true;
        return vRes;
    }

    public Collection<AccountEntityData> getMailingGroup(Integer aNewKey)
    {
        System.out.println("getMailingGroup: last mail time=" + mLastMailTime + ", new mail time=" + aNewKey + ". group list size = " + mMailingGroups.size());
        if (mLastMailTime == 0)
        {
            mLastMailTime = aNewKey.intValue();
            return null;
        }
        Set<Integer> vMailGroupKeys = mMailingGroups.keySet();
        for (Iterator<Integer> i = vMailGroupKeys.iterator(); i.hasNext();)
        {
            Integer vKey = i.next();
            if (aNewKey.intValue() > vKey.intValue() && mLastMailTime <= vKey.intValue())
            {
                Collection<AccountEntityData> mailGroup = mMailingGroups.get(vKey);
                mLastMailTime = aNewKey.intValue();
                return mailGroup;
            }
        }
        System.out.println("getMailingGroup: no mail group <= " + mLastMailTime + " and > " + aNewKey);
        mLastMailTime = aNewKey.intValue();
        return null;
    }

    private void buildMailingGroups()
    {
        mMailingGroups.clear();
        for (Iterator<AccountEntityData> i = mRawCollection.iterator(); i.hasNext();)
        {
            AccountEntityData vEntry = (AccountEntityData) i.next();
            if (vEntry.getMailHour1() > 0 && vEntry.getMailHour1() <= Constants.MAX_MAIL_HOUR)
            {
                Integer vKey = new Integer(vEntry.getMailHour1() * 60 + vEntry.getMailMinutes1());
                Collection<AccountEntityData> vMailGroup = mMailingGroups.get(vKey);
                if (vMailGroup == null)
                {
                    vMailGroup = new Vector<AccountEntityData>();
                    mMailingGroups.put(vKey, vMailGroup);
                }
                vMailGroup.add(vEntry);
            }
            if (vEntry.getMailHour2() > 0 && vEntry.getMailHour2() <= Constants.MAX_MAIL_HOUR)
            {
                Integer vKey = new Integer(vEntry.getMailHour2() * 60 + vEntry.getMailMinutes2());
                Collection<AccountEntityData> vMailGroup = mMailingGroups.get(vKey);
                if (vMailGroup == null)
                {
                    vMailGroup = new Vector<AccountEntityData>();
                    mMailingGroups.put(vKey, vMailGroup);
                }
                vMailGroup.add(vEntry);
            }
            if (vEntry.getMailHour3() > 0 && vEntry.getMailHour3() <= Constants.MAX_MAIL_HOUR)
            {
                Integer vKey = new Integer(vEntry.getMailHour3() * 60 + vEntry.getMailMinutes3());
                Collection<AccountEntityData> vMailGroup = mMailingGroups.get(vKey);
                if (vMailGroup == null)
                {
                    vMailGroup = new Vector<AccountEntityData>();
                    mMailingGroups.put(vKey, vMailGroup);
                }
                vMailGroup.add(vEntry);
            }
        }
        System.out.println(mMailingGroups.size() + " mailing groups");
        // Set vMailGroupKeys = mMailingGroups.keySet();
        // for (Iterator i = vMailGroupKeys.iterator(); i.hasNext();)
        // {
        // Integer vKey = (Integer) i.next();
        // if (mLastMailTime > vKey.intValue())
        // {
        // System.out.println("Remove " + vKey + " from mailinggroups");
        // mMailingGroups.remove(vKey);
        // if (mMailingGroups.size() == 0)
        // {
        // buildMailingGroups();
        // break;
        // }
        // }
        // }
        // System.out.println(mMailingGroups.size() +
        // " mailing groups after cleanup");

        Set<Integer> vMailGroupKeys = mMailingGroups.keySet();
        for (Iterator<Integer> i = vMailGroupKeys.iterator(); i.hasNext();)
        {
            Integer vKey = (Integer) i.next();
            Collection<AccountEntityData> vMailGroup = mMailingGroups.get(vKey);
            // System.out.println("Mail group for key " + vKey + " has " + vMailGroup.size()
            // + " entries:");
            for (Iterator<AccountEntityData> j = vMailGroup.iterator(); j.hasNext();)
            {
                AccountEntityData vAccount = (AccountEntityData) j.next();
                // System.out.println("\t" + vAccount.getFullName());
            }
        }
    }
}
