/*mRawUnarchivedCollection =
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.session;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import be.tba.ejb.account.interfaces.AccountEntityData;
import be.tba.ejb.account.session.AccountSqlAdapter;
import be.tba.ejb.invoice.interfaces.InvoiceEntityData;
import be.tba.ejb.pbx.interfaces.CallRecordEntityData;
import be.tba.ejb.task.interfaces.TaskEntityData;
import be.tba.servlets.session.WebSession;
import be.tba.util.constants.AccountRole;
import be.tba.util.constants.Constants;
import be.tba.util.data.MailTriggerData;

final public class AccountCache
{
   private AccountSqlAdapter mAccountAdapter = new AccountSqlAdapter();
   private Map<String, AccountEntityData> mFwdKeyList;
   private Map<Integer, AccountEntityData> mIdKeyList;
   private Collection<AccountEntityData> mRawUnarchivedCollection;
   private Collection<AccountEntityData> mArchivedCollection;
   // private SortedMap<String, AccountEntityData> mNameSortedList; // without9000,
   // 9001
   private SortedSet<AccountEntityData> mNameSortedList; // without9000, 9001
   private SortedSet<AccountEntityData> mNameSortedFullList;
   private SortedSet<AccountEntityData> mCallCustomerSortedList;
   private Map<Integer, Collection<AccountEntityData>> mSubCustomersLists;
   private Map<String, AccountEntityData> mEmployeeLists;
   private Map<Integer, Collection<AccountEntityData>> mMailingGroups;
   private Map<String, Collection<Integer>> mBankAccountNr2AccountIdsMap;
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
         mInstance = new AccountCache();
         mInstance.update();
      }
      return mInstance;
   }

   private AccountCache()
   {
      mRawUnarchivedCollection = new Vector<AccountEntityData>();
      mArchivedCollection = new Vector<AccountEntityData>();
      mNameSortedList = Collections.synchronizedSortedSet(new TreeSet<AccountEntityData>());
      mNameSortedFullList = Collections.synchronizedSortedSet(new TreeSet<AccountEntityData>());
      mFwdKeyList = new HashMap<String, AccountEntityData>();
      mIdKeyList = new HashMap<Integer, AccountEntityData>();
      mCallCustomerSortedList = Collections.synchronizedSortedSet(new TreeSet<AccountEntityData>());
      mSubCustomersLists = new HashMap<Integer, Collection<AccountEntityData>>();
      mEmployeeLists = new HashMap<String, AccountEntityData>();
      mMailingGroups = new HashMap<Integer, Collection<AccountEntityData>>();
      mBankAccountNr2AccountIdsMap = new HashMap<String, Collection<Integer>>();
      mLastMailTime = 0;

   }

   public void update(WebSession session)
   {
      Collection<AccountEntityData> fullList = Collections.synchronizedCollection(mAccountAdapter.getAllRows(session));
      converToHashMap(fullList);
      buildMailingGroups();
   }

   public void update()
   {
      WebSession session = null;
      try
      {
         session = new WebSession();
         update(session);
      } catch (SQLException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } finally
      {
         if (session != null && session.getConnection() != null)
         {
            try
            {
               session.getConnection().close();
            } catch (SQLException ex)
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

   // this function must be removed in herfst van 2020 wanneer alle records een
   // accountId zullen hebben.
   // dan moet overal de get(int) function worden gebruikt.
   public AccountEntityData get(CallRecordEntityData record)
   {
      if (record.getAccountId() > 0) // yves: to be changed in .getId() only in de herfst van 2020
      {
         return get(record.getAccountId());
      } else
      {
         return get(record.getFwdNr());
      }
   }

   public AccountEntityData get(InvoiceEntityData invoice)
   {
      if (invoice.getAccountID() > 0) // yves: to be changed in .getId() only in de herfst van 2020
      {
         return get(invoice.getAccountID());
      } else
      {
         return get(invoice.getAccountFwdNr());
      }
   }

   public AccountEntityData get(TaskEntityData task)
   {
      if (task.getAccountId() > 0) // yves: to be changed in .getId() only in de herfst van 2020
      {
         return get(task.getAccountId());
      } else
      {
         return get(task.getFwdNr());
      }
   }

   public AccountEntityData get(int id)
   {
      return mIdKeyList.get(Integer.valueOf(id));
   }

   public AccountEntityData get(String fwdNumber)
   {
      return (AccountEntityData) mFwdKeyList.get(fwdNumber);
   }

   public Collection<AccountEntityData> getEmployeeList()
   {
      return mEmployeeLists.values();
   }

   public Collection<AccountEntityData> getSubCustomersList(int superCustomerId)
   {
      return mSubCustomersLists.get(superCustomerId);
   }

   public Collection<Integer> getSuperCustomersList()
   {
      return mSubCustomersLists.keySet();
   }

   public Collection<AccountEntityData> getCustomerList()
   {
      return mNameSortedList;
   }

   public Collection<AccountEntityData> getInvoiceCustomerList()
   {
      Vector<AccountEntityData> vInvoiceCustomers = new Vector<AccountEntityData>();

      for (Iterator<AccountEntityData> i = getCustomerList().iterator(); i.hasNext();)
      {
         AccountEntityData vEntry = i.next();
         if (vEntry.getNoInvoice() || vEntry.getIsArchived())
            continue;
         vInvoiceCustomers.add(vEntry);
      }
      return vInvoiceCustomers;
   }

   public Collection<AccountEntityData> getAccountListWithoutTbaNrs()
   {
      return mNameSortedList;
   }

   public Collection<AccountEntityData> getCallCustomerList()
   {
      return mCallCustomerSortedList;
   }

   public Collection<Integer> getAllIds()
   {
      return mIdKeyList.keySet();
   }

   private void converToHashMap(Collection<AccountEntityData> rawList)
   {
      System.out.println("AccountCache::converToHashMap()");
      mRawUnarchivedCollection.clear();
      mArchivedCollection.clear();
      mCallCustomerSortedList.clear();
      mNameSortedList.clear();
      mFwdKeyList.clear();
      mIdKeyList.clear();
      mNameSortedFullList.clear();
      mSubCustomersLists.clear();
      mEmployeeLists.clear();
      Vector<String> vSuperCustomers = new Vector<String>();
      AccountEntityData vEntry = null;
      // int y =0;

      for (Iterator<AccountEntityData> i = rawList.iterator(); i.hasNext();)
      {
         vEntry = i.next();
         // System.out.println("check " + y++ + ": " + vEntry.getFwdNumber());
         mIdKeyList.put(Integer.valueOf(vEntry.getId()), vEntry);
         // System.out.println("mIdKeyList " + ++y + " (size=" +mIdKeyList.size() + "):
         // added " + vEntry.getFwdNumber() + ", with ID=" + vEntry.getId());
         if (!vEntry.getIsArchived())
         {
            mRawUnarchivedCollection.add(vEntry);
            if (vEntry.getRole().equals(AccountRole._vCustomer) || vEntry.getRole().equals(AccountRole._vSubCustomer))
            {
               if (vEntry.getFwdNumber().matches("[0-9]+"))
               {
                  mCallCustomerSortedList.add(vEntry);
               }

               // vMap.put(vEntry.getFwdNumber(), vEntry);
               mNameSortedList.add(vEntry);
               // System.out.println("add to mNameSortedList: " + vEntry.getFwdNumber() + (out
               // ? " Pass" : " Failed"));
               mFwdKeyList.put(vEntry.getFwdNumber(), vEntry);
               mNameSortedFullList.add(vEntry);
               if (vEntry.getHasSubCustomers())
               {
                  Collection<AccountEntityData> vSubCustomerList = mSubCustomersLists.get(vEntry.getId());
                  if (vSubCustomerList == null)
                  {
                     vSubCustomerList = new Vector<AccountEntityData>();
                     mSubCustomersLists.put(vEntry.getId(), vSubCustomerList);
                     // System.out.println(++y +" added first lege sub klanten lijst voor " +
                     // vEntry.getFullName() + ", ID=" + vEntry.getId());
                  }
               }

               if (vEntry.getRole().equals(AccountRole._vSubCustomer) && vEntry.getSuperCustomer().length() > 0)
               {
                  // add to mSubCustomersLists
                  Collection<AccountEntityData> vSubCustomerList = mSubCustomersLists.get(vEntry.getSuperCustomerId());
                  if (vSubCustomerList == null)
                  {
                     vSubCustomerList = new Vector<AccountEntityData>();
                     mSubCustomersLists.put(vEntry.getSuperCustomerId(), vSubCustomerList);
                     vSuperCustomers.add(vEntry.getSuperCustomer());
                     // System.out.println(++y +"added first sub klanten lijst voor " +
                     // vEntry.getSuperCustomer() + ", ID=" + vEntry.getSuperCustomerId());
                  }
                  vSubCustomerList.add(vEntry);
                  // System.out.println(" add sub customer " + vEntry.getFullName() + " to list
                  // for id=" + vEntry.getSuperCustomerId());
               }
            } else if (vEntry.getRole().equals(AccountRole._vAdminstrator) || vEntry.getRole().equals(AccountRole._vEmployee))
            {
               // add to mEmployeeLists
               mFwdKeyList.put(vEntry.getFwdNumber(), vEntry);
               mEmployeeLists.put(vEntry.getFwdNumber(), vEntry);
               // System.out.println("added employee " + vEntry.getFullName() + ", " +
               // vEntry.getId() + ", " + vEntry.getUserId());
            }
         }
         else
         {
            mArchivedCollection.add(vEntry);
         }
         // fill in the AccountNrList
         if (vEntry.getAccountNr() != null && !vEntry.getAccountNr().isEmpty())
         {
            String bankAccountNrs = vEntry.getAccountNr();
            StringTokenizer vTokenizer = new StringTokenizer(bankAccountNrs, ",");

            while (vTokenizer.hasMoreTokens())
            {
               String bankAccountNr = vTokenizer.nextToken();
               if (mBankAccountNr2AccountIdsMap.containsKey(bankAccountNr))
               {
                  Collection<Integer> ids = mBankAccountNr2AccountIdsMap.get(bankAccountNr);
                  ids.add(Integer.valueOf(vEntry.getId()));
               } else
               {
                  Collection<Integer> ids = new Vector<Integer>();
                  ids.add(vEntry.getId());
                  mBankAccountNr2AccountIdsMap.put(bankAccountNr, ids);
               }

               // System.out.println("matching FWD (" + vEntry.getFwdNumber() + ") nr to
               // account Number: " + accountNr);
            }
         }
      }
//        y=0;
      // set the HasSubCustomer flags
      Set<Integer> superCustomers = mSubCustomersLists.keySet();
      for (Iterator<Integer> i = superCustomers.iterator(); i.hasNext();)
      {
         Integer vSuperCustId = i.next();
         AccountEntityData vSuperCust = get(vSuperCustId);
//        	System.out.println(++y + ": " + vSuperCustId + " returns " + (vSuperCust == null ? "null": vSuperCust.getFwdNumber()));
         if (vSuperCust != null)
         {
            vSuperCust.setHasSubCustomers(true);
            // also add subcustomers of supers that are call customers
            if (vSuperCust.getFwdNumber().matches("[0-9]+"))
            {
               Collection<AccountEntityData> subcustomerList = getSubCustomersList(vSuperCustId);
               for (Iterator<AccountEntityData> iter = subcustomerList.iterator(); iter.hasNext();)
               {
                  AccountEntityData subcustomer = iter.next();
                  mCallCustomerSortedList.add(subcustomer);
               }
            }
         } else
         {
            System.out.println("######## strange error for id=" + vSuperCustId);
            continue;
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

      for (int i = 0; i < 2; ++i)
      {
         AccountEntityData vEntryCnst = new AccountEntityData();
         vEntryCnst.setFullName(Constants.NUMBER_BLOCK[i][3]);
         vEntryCnst.setFwdNumber(Constants.NUMBER_BLOCK[i][0]);
         vEntryCnst.setMailMinutes1((short) 0);
         vEntryCnst.setMailHour1((short) 0);
         vEntryCnst.setMailMinutes2((short) 0);
         vEntryCnst.setMailHour2((short) 0);
         vEntryCnst.setMailMinutes3((short) 0);
         vEntryCnst.setMailHour3((short) 0);
         mNameSortedFullList.add(vEntryCnst);
         mFwdKeyList.put(Constants.NUMBER_BLOCK[i][0], vEntryCnst);
      }
   }

   public Collection<String> getFreeNumbers()
   {
      try
      {
         Vector<String> vFreeNumbers = new Vector<String>();
         for (int i = 1; i < Constants.NUMBER_BLOCK.length; i++)
            vFreeNumbers.add(Constants.NUMBER_BLOCK[i][0]);
         for (Iterator<AccountEntityData> i = mRawUnarchivedCollection.iterator(); i.hasNext();)
         {
            vFreeNumbers.remove((i.next()).getFwdNumber());
         }
         return vFreeNumbers;
      } catch (Exception e)
      {
         e.printStackTrace();
      }
      return new Vector<String>();
   }

   public String idToFwdNr(int id)
   {
      for (Iterator<AccountEntityData> i = mRawUnarchivedCollection.iterator(); i.hasNext();)
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
   public Collection<MailTriggerData> getAccountTriggers()
   {
      try
      {
         Vector<MailTriggerData> vTriggerList = new Vector<MailTriggerData>();
         for (Iterator<AccountEntityData> i = mRawUnarchivedCollection.iterator(); i.hasNext();)
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
      } catch (Exception e)
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
      mLastMailTime = aNewKey.intValue();
      return null;
   }

   public Collection<Integer> getAccountIdsForBankAccountNr(String bankAccountNr)
   {
      if (bankAccountNr != null && !bankAccountNr.isEmpty())
      {
         if (mBankAccountNr2AccountIdsMap.containsKey(bankAccountNr))
         {
            return mBankAccountNr2AccountIdsMap.get(bankAccountNr);
         }
      }
      return new Vector<Integer>();
   }

   public Collection<AccountEntityData> getArchivedList()
   {
      return mArchivedCollection;
   }
   
   private void buildMailingGroups()
   {
      mMailingGroups.clear();
      for (Iterator<AccountEntityData> i = mRawUnarchivedCollection.iterator(); i.hasNext();)
      {
         AccountEntityData vEntry = (AccountEntityData) i.next();
         if (vEntry.getMailHour1() > 0 && vEntry.getMailHour1() <= Constants.MAX_MAIL_HOUR)
         {
            Integer vKey = Integer.valueOf(vEntry.getMailHour1() * 60 + vEntry.getMailMinutes1());
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
            Integer vKey = Integer.valueOf(vEntry.getMailHour2() * 60 + vEntry.getMailMinutes2());
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
            Integer vKey = Integer.valueOf(vEntry.getMailHour3() * 60 + vEntry.getMailMinutes3());
            Collection<AccountEntityData> vMailGroup = mMailingGroups.get(vKey);
            if (vMailGroup == null)
            {
               vMailGroup = new Vector<AccountEntityData>();
               mMailingGroups.put(vKey, vMailGroup);
            }
            vMailGroup.add(vEntry);
         }
      }

      /*
       * print the mailing groups
       * 
       * 
       * Set<Integer> vMailGroupKeys = mMailingGroups.keySet(); for (Iterator<Integer>
       * i = vMailGroupKeys.iterator(); i.hasNext();) { Integer vKey = (Integer)
       * i.next(); Collection<AccountEntityData> vMailGroup =
       * mMailingGroups.get(vKey); System.out.println("Mail group for key " + vKey +
       * " has " + vMailGroup.size() + " entries:"); for (Iterator<AccountEntityData>
       * j = vMailGroup.iterator(); j.hasNext();) { AccountEntityData vAccount =
       * (AccountEntityData) j.next(); System.out.println("\t" +
       * vAccount.getFullName()); } }
       */
   }
}
