package be.tba.pbx;

import java.io.Serializable;

// Example record (old format)
//
// 12345 12345 123 123 12345678 12345 12345678 1234 12 123456 12345678901234567890123456 1 12345 1234567890 1234567890123456 1234567890123456 123 1 123456789 12345678 1234
// A20   A20   <-- N02 29/07/03 11:26 00:00:00    0 ST        0474963268................ M 00:06 0.00       ................ Centrale........       A20       A20      N002
//
// ***** A20   --> N01 29/07/03 11:27 00:00:00    0 ST I      014409001................. M 00:00 0.00       ................ Centrale........       ********* A20      N001
//
// Example record (new format from 2.1)
//
// 12345 12345 123 123 12345678 12345 12345678 1234 12 123456 12345678901234567890123456 1 12345 1234567890 1234567890123456 1234567890123456 123 1 123456789 123456789 1234
// G#511 G#511 <-- N01 05/10/04 19:45 00:00:00    0 ST        0473949777................ M 00:04 0.00       ................ TEST 018........       G#511     G#511     N001

// A22   A22   <-- N02 18/10/04 20:30 00:00:00    0 ST        0473949777................ M 00:05 0.00       ................ PRIVE 22........       A22       A22      N002

public final class Forum700CallRecord implements Serializable
{
   private static final int mInitialUserLen = 5;

   private static final int mChargedUserLen = 5;

   private static final int mTypeLen = 3;

   private static final int mLineLen = 3;

   private static final int mDateLen = 8;

   private static final int mTimeLen = 5;

   private static final int mDurationLen = 8;

   private static final int mTaxesLen = 4; // maybe 4, was 5

   private static final int mServLen = 2;

   private static final int mFacilitiesLen = 6;

   private static final int mExtCorrNumberLen = 26;

   private static final int mModeLen = 1;

   private static final int mRingLen = 5;

   private static final int mCostLen = 10;

   private static final int mBusinCodeLen = 16;

   private static final int mSubscriberNameLen = 16;

   private static final int mNodeLen = 3;

   private static final int mOpIdLen = 1;

   private static final int mInitialUser2Len = 9;

   private static final int mChargedUser2Len = 8; // maybe 8, was 9

   private static final int mLine4Len = 4;

   public String mInitialUser;

   public String mChargedUser;

   public String mType;

   public String mLine;

   public String mDate;

   public String mTime;

   public String mDuration;

   public String mTaxes;

   public String mServ;

   public String mFacilities;

   public String mExtCorrNumber;

   public String mMode;

   public String mRing;

   public String mCost;

   public String mBusinCode;

   public String mSubscriberName;

   public String mNode;

   public String mOpId;

   public String mInitialUser2;

   public String mChargedUser2;

   public String mLine4;

   boolean mIsLonger = false;

   boolean mIsValid = false;

   static boolean isStarted = false;

   static final int recordLen = 173;

   static final char separator = ',';

   public Forum700CallRecord(String aRecord)
   {
      super();
      if (aRecord.length() < recordLen)
      {
         mIsValid = false;
      }
      else
      {
         // String convert = new String(aRecord);
         char[] charArr = aRecord.toCharArray();
         int offset = 1;
         mInitialUser = String.copyValueOf(charArr, offset++, mInitialUserLen);
         mInitialUser = mInitialUser.trim();
         offset += mInitialUserLen;
         mChargedUser = String.copyValueOf(charArr, offset++, mChargedUserLen);
         mChargedUser = mChargedUser.trim();
         offset += mChargedUserLen;
         mType = String.copyValueOf(charArr, offset++, mTypeLen);
         mType = mType.trim();
         offset += mTypeLen;
         mLine = String.copyValueOf(charArr, offset++, mLineLen);
         mLine = mLine.trim();
         offset += mLineLen;
         mDate = String.copyValueOf(charArr, offset++, mDateLen);
         mDate = mDate.trim();
         offset += mDateLen;
         mTime = String.copyValueOf(charArr, offset++, mTimeLen);
         offset += mTimeLen;
         mTime = mTime.trim();
         mDuration = String.copyValueOf(charArr, offset++, mDurationLen);
         offset += mDurationLen;
         mTaxes = String.copyValueOf(charArr, offset++, mTaxesLen);
         mTaxes = mTaxes.trim();
         offset += mTaxesLen;
         mServ = String.copyValueOf(charArr, offset++, mServLen);
         mServ = mServ.trim();
         offset += mServLen;
         mFacilities = String.copyValueOf(charArr, offset++, mFacilitiesLen);
         mFacilities = mFacilities.trim();
         offset += mFacilitiesLen;
         mExtCorrNumber = String.copyValueOf(charArr, offset++, mExtCorrNumberLen);
         mExtCorrNumber = mExtCorrNumber.trim();
         offset += mExtCorrNumberLen;
         mMode = String.copyValueOf(charArr, offset++, mModeLen);
         mMode = mMode.trim();
         offset += mModeLen;
         mRing = String.copyValueOf(charArr, offset++, mRingLen);
         mRing = mRing.trim();
         offset += mRingLen;
         mCost = String.copyValueOf(charArr, offset++, mCostLen);
         mCost = mCost.trim();
         offset += mCostLen;
         mBusinCode = String.copyValueOf(charArr, offset++, mBusinCodeLen);
         mBusinCode = mBusinCode.trim();
         offset += mBusinCodeLen;
         mSubscriberName = String.copyValueOf(charArr, offset++, mSubscriberNameLen);
         mSubscriberName = mSubscriberName.trim();
         offset += mSubscriberNameLen;
         mNode = String.copyValueOf(charArr, offset++, mNodeLen);
         mNode = mNode.trim();
         offset += mNodeLen;
         mOpId = String.copyValueOf(charArr, offset++, mOpIdLen);
         mOpId = mOpId.trim();
         offset += mOpIdLen;
         mInitialUser2 = String.copyValueOf(charArr, offset++, mInitialUser2Len);
         mInitialUser2 = mInitialUser2.trim();
         offset += mInitialUser2Len;
         mChargedUser2 = String.copyValueOf(charArr, offset++, mChargedUser2Len);
         mChargedUser2 = mChargedUser2.trim();
         offset += mChargedUser2Len;
         offset++;
         if (charArr[offset] == ' ')
         {
            offset++; // this time mChargedUser2Len = 9
            mIsLonger = true;
         }
         mLine4 = String.copyValueOf(charArr, offset, mLine4Len);
         mLine4 = mLine4.trim();

         mInitialUser = stripNonAlphanum(mInitialUser);
         mChargedUser = stripNonAlphanum(mChargedUser);
         mInitialUser2 = stripNonAlphanum(mInitialUser2);
         mChargedUser2 = stripNonAlphanum(mChargedUser2);

         mExtCorrNumber = stripDots(mExtCorrNumber);
         mBusinCode = stripDots(mBusinCode);
         mSubscriberName = stripDots(mSubscriberName);
         if ((mType.equals("<--") || mType.equals("-->")))// &&
         // !mDuration.startsWith("00:00:00"))
         {
            mIsValid = true;
         }
         else
            System.out.println("Invalid record detected.");
      }
   }

   public boolean isIncomingCall()
   {
      return mType.equals("<--");
   }

   public boolean isLonger()
   {
      return mIsLonger;
   }

   static public int getRecordLen()
   {
      return recordLen;
   }

   public void resetHeader()
   {
      isStarted = false;
   }

   public boolean isValid()
   {
      return mIsValid;
   }

   public void printRecord()
   {
      if (!isStarted)
      {
         System.out.println("User  Office Dir Corespondent              Line Date     Time   Cost Duration Code Name");
      }
      isStarted = true;
      System.out.print(mInitialUser + " ");
      System.out.print(mChargedUser + "  ");
      System.out.print(mType + " ");
      System.out.print(mExtCorrNumber + " ");
      System.out.print(mLine + "  ");
      System.out.print(mDate + " ");
      System.out.print(mTime + " ");
      System.out.print(mDuration + " ");
      System.out.print(mCost + " ");
      System.out.print(mBusinCode + " ");
      System.out.println(mSubscriberName + " ");
   }

   public String getFileRecord()
   {
      return new String(mInitialUser + separator + mChargedUser + separator + mType + separator + mExtCorrNumber + separator + mLine + separator + mDate + separator + mTime + separator +
      // System.out.print(mDuration + " ");
            mCost + separator + mDuration + separator + mBusinCode + separator + mSubscriberName + "\r\n");
   }

   private String stripDots(String toStrip)
   {
      int vLastIndex = toStrip.length();
      if (vLastIndex > 0)
      {
         do
         {
            --vLastIndex;
         } while (vLastIndex >= 0 && (toStrip.charAt(vLastIndex) == '.'));
         if (vLastIndex >= 0)
            return toStrip.substring(0, ++vLastIndex);
         else
            return "";
      }
      return toStrip;
   }

   private String stripNonAlphanum(String nrToStrip)
   {
      int vLastIndex = nrToStrip.length();
      if (vLastIndex > 0)
      {
         do
         {
            --vLastIndex;
         } while (vLastIndex >= 0 && Character.isDigit(nrToStrip.charAt(vLastIndex)));
         if (vLastIndex >= 0)
         {
            return nrToStrip.substring(vLastIndex + 1);
         }
      }
      return nrToStrip;
   }

}
