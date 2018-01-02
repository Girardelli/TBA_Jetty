/*
 * TheBusinessAssistant b.v.b.a
 *
 */
package be.tba.util.constants;

/**
 * Base Data Container for all other Value Objects
 *
 * @author Yves Willems
 * @version $Revision: 1.0 $
 */
public class Constants
{
    // Administrator-test account strings.
    public static final String MASTER_LOGIN_NAME = "esosrv";

    public static final String MASTER_LOGIN_PASWORD = "";

    public static final String NANCY_EMAIL = "nancy.olyslaegers@theBusinessAssistant.be";

    // Account Role strings.
    public static final String EJB_ACC_ROLE_CUSTOMER = "klant";

    public static final String EJB_ACC_ROLE_ADMIN = "admin";

    // HTML page names
    public static final String TBA_URL_BASE = "http://lille.thebusinessAssistant.be:8080/TheBusinessAssistant/";

    public static final String MYSQL_URL = "jdbc:mysql://localhost/tbadb?user=root&password=gzb625$";
    // public static final String INVOICE_HEAD_TMPL =
    // "C:\\jboss-4.0.2\\projects\\TBA\\templates\\invoice-head-template.htm";
    public static final String INVOICE_HEAD_TMPL = "C:\\jetty\\templates\\invoiceTemplate.pdf";

    public static final String INVOICE_DETAIL_TMPL = "C:\\jetty\\templates\\invoice-detail-template.htm";

    public static final String INVOICE_FOOTER_TMPL = "C:\\jetty\\templates\\invoice-footer-template.htm";

    public static final String INVOICE_DIR = "C:\\TheBusinessAssistant\\TheBusinessAssistant\\facturen\\";

    public static final String ADMIN_LOGIN_HTML = "/TheBusinessAssistant/admin.html";

    public static final String BAD_LOGIN_HTML = "/badlogin.html";

    public static final String LOGIN_HTML = "/TheBusinessAssistant/login.html";

    public static final String SERVLET_LOGIN_HTML = "/login.html";

    public static final String HOME_HTML = "/index.html";

    public static final String FORM_SUBMIT_SUCCESS = "/formok.html";

    // JSP page names
    public static final String ADMIN_FAIL_JSP = "/admin/adminfail.jsp";

    public static final String PROTECT_FAIL_JSP = "/protect/protectfail.jsp";

    // public static final String BAD_ADMIN_HTML =
    // "\\TheBusinessAssistant\\badadmin.html";
    public static final String ADD_ACCOUNT_JSP = "/admin/addAccount.jsp";

    public static final String ADD_EMPLOYEE_JSP = "/admin/addEmployee.jsp";

    public static final String ADMIN_EMPLOYEE_COST_JSP = "/admin/employeecost.jsp";

    public static final String ADMIN_ACCOUNT_JSP = "/admin/adminaccount.jsp";

    public static final String ADMIN_EMPLOYEE_JSP = "/admin/adminemployee.jsp";

    public static final String ADMIN_LOGIN = "/admin.html";

    public static final String SHOW_ERROR_JSP = "/admin/showMailFail.jsp";

    public static final String UPDATE_ACCOUNT_JSP = "/admin/updateAccount.jsp";

    public static final String SELECT_SUBCUSTOMER_JSP = "/admin/selectSubCustomer.jsp";

    public static final String UPDATE_RECORD_JSP = "/admin/updateRecord.jsp";

    public static final String ADD_RECORD_JSP = "/admin/addrecord.jsp";

    public static final String ADMIN_CALLS_JSP = "/admin/admincalls.jsp";

    public static final String INVOICE_JSP = "/admin/invoice.jsp";

    public static final String ADMIN_SEARCH_JSP = "/admin/search.jsp";

    public static final String ADMIN_NOTLOGGEDCALLS_JSP = "/admin/notloggedcalls.jsp";

    public static final String ADMIN_TASK_JSP = "/admin/admintasks.jsp";

    public static final String ADD_TASK_JSP = "/admin/addtask.jsp";

    public static final String ADD_INVOICE_JSP = "/admin/addinvoice.jsp";

    public static final String UPDATE_TASK_JSP = "/admin/updatetask.jsp";

    public static final String ANNOUNCEMENT_JSP = "/admin/announcement.jsp";

    public static final String ARE_YOU_SURE_JSP = "/admin/areyousure.jsp";

    public static final String NEW_CALL_JSP = "/admin/newcall.jsp";

    public static final String ADMIN_INVOICE_JSP = "/admin/admininvoice.jsp";

    public static final String OPEN_INVOICE_JSP = "/admin/openinvoice.jsp";

    public static final String LOGIN_HOME_JSP = "/protect/prothome.jsp";

    public static final String REGISTER_JSP = "/protect/register.jsp";

    public static final String LOGIN_ERROR_JSP = "/loginerror.jsp";

    public static final String CLIENT_CALLS_JSP = "/protect/clientcalls.jsp";

    public static final String CLIENT_SHOW_REC_JSP = "/protect/showrecord.jsp";

    public static final String CLIENT_PREF_JSP = "/protect/updatepref.jsp";

    public static final String CLIENT_SEARCH_JSP = "/protect/searchcust.jsp";

    public static final String CLIENT_SHOW_TASKS_JSP = "/protect/clienttasks.jsp";

    public static final String DICTAAT_FORM_JSP = "/dictaatform.jsp";

    // session attribute constants
    // error responses
    public static final String NO_ERROR = "OK";

    public static final String ADD_ACCOUNT_FAILED = "NOK";

    public static final String NO_VALUE = "NOVALUE";

    public static final String ACCOUNT_EXISTS = "Account already exists";

    // attribute names
    public static final String SRV_ACTION = "_act";

    public static final String ACCOUNT_SELECT = "_sel";

    public static final String ACCOUNT_PASSWORD = "_pwd";

    public static final String ACCOUNT_PASSWORD2 = "_pwd2";

    public static final String ACCOUNT_FULLNAME = "_fn";

    public static final String ACCOUNT_FORWARD_NUMBER = "_fwnr";

    public static final String ACCOUNT_USERID = "_uid";

    public static final String ACCOUNT_ROLE = "_role";

    public static final String ACCOUNT_REGCODE = "_rgcd";

    public static final String ACCOUNT_ID = "_aid";

    public static final String ACCOUNT_EMAIL = "_aem";

    public static final String ACCOUNT_INVOICE_EMAIL = "_aiem";

    public static final String ACCOUNT_GSM = "_agsm";

    public static final String ACCOUNT_AUTO_RELEASE = "_aare";

    public static final String ACCOUNT_MAIL_ON1 = "_amon1";

    public static final String ACCOUNT_MAIL_UUR1 = "_auu1";

    public static final String ACCOUNT_MAIL_MINUTEN1 = "_ami1";

    public static final String ACCOUNT_MAIL_ON2 = "_amon2";

    public static final String ACCOUNT_MAIL_UUR2 = "_auu2";

    public static final String ACCOUNT_MAIL_MINUTEN2 = "_ami2";

    public static final String ACCOUNT_MAIL_ON3 = "_amon3";

    public static final String ACCOUNT_MAIL_UUR3 = "_auu3";

    public static final String ACCOUNT_MAIL_MINUTEN3 = "_ami3";

    public static final String ACCOUNT_INVOICE_TYPE = "_aind";

    public static final String ACCOUNT_3W_CUSTOMER = "_3W";

    public static final String ACCOUNT_3W_PERSON_ID = "_3Wpid";

    public static final String ACCOUNT_3W_COMPANY_ID = "_3Wcid";

    public static final String ACCOUNT_FAC_STD_IN_CALL = "_fcsic";

    public static final String ACCOUNT_FAC_STD_OUT_CALL = "_fcsoc";

    public static final String ACCOUNT_FAC_FAX_CALL = "_fcfax";

    public static final String ACCOUNT_FAC_OUT_LEVEL1 = "_fcl1";

    public static final String ACCOUNT_FAC_OUT_LEVEL2 = "_fcl2";

    public static final String ACCOUNT_FAC_OUT_LEVEL3 = "_fcl3";

    public static final String ACCOUNT_FAC_AGENDA_FIXED = "_fcafx"; // fixed fee
    // on all
    // calls
    // (yes or
    // no)

    public static final String ACCOUNT_FAC_AGENDA_CALL = "_fcpac"; // price of
    // a agenda
    // call (can
    // also be
    // the price
    // for fixed
    // fee)

    public static final String ACCOUNT_FAC_AGENDA_UNIT = "_fcaun"; // unit of
    // the
    // agenda
    // price (%
    // or euro)

    public static final String ACCOUNT_FAC_LONG = "_flng";

    public static final String ACCOUNT_FAC_LONG_FWD = "_flngf";

    public static final String ACCOUNT_FAC_SMS = "_fcsms";

    public static final String ACCOUNT_FAC_CALL_FORWARD = "_fccfw";

    public static final String ACCOUNT_FAC_TBL_MIN_CALL_I = "_fcmcI";
    public static final String ACCOUNT_FAC_TBL_START_COST_I = "_fcscI";
    public static final String ACCOUNT_FAC_TBL_EXTRA_COST_I = "_fcecI";
    public static final String ACCOUNT_FAC_TBL_MIN_CALL_II = "_fcmcII";
    public static final String ACCOUNT_FAC_TBL_START_COST_II = "_fcscII";
    public static final String ACCOUNT_FAC_TBL_EXTRA_COST_II = "_fcecII";
    public static final String ACCOUNT_FAC_TBL_MIN_CALL_III = "_fcmcIII";
    public static final String ACCOUNT_FAC_TBL_START_COST_III = "_fcscIII";
    public static final String ACCOUNT_FAC_TBL_EXTRA_COST_III = "_fcecIII";
    public static final String ACCOUNT_FAC_TBL_MIN_CALL_IV = "_fcmcIV";
    public static final String ACCOUNT_FAC_TBL_START_COST_IV = "_fcscIV";
    public static final String ACCOUNT_FAC_TBL_EXTRA_COST_IV = "_fcecIV";

    public static final String ACCOUNT_TASK_HOUR_RATE = "_fthr";

    public static final String ACCOUNT_COMPANY_NAME = "_fcyn";

    public static final String ACCOUNT_ATT_TO_NAME = "_fatn";

    public static final String ACCOUNT_STREET = "_fstr";

    public static final String ACCOUNT_CITY = "_fcty";

    public static final String ACCOUNT_BTW_NUMBER = "_fbtw";

    public static final String ACCOUNT_NO_INVOICE = "_fniv";

    public static final String ACCOUNT_NO_BTW = "_fnbtw";

    public static final String ACCOUNT_COUNT_ALL_LONG_CALLS = "_fnlc";

    public static final String ACCOUNT_COUNT_LONG_FWD_CALLS = "_fnfc";

    public static final String ACCOUNT_SUPER_CUSTOMER = "_fscu";

    public static final String ACCOUNT_SUB_CUSTOMER = "_fsuc";

    public static final String ACCOUNT_HAS_SUB_CUSTOMERS = "_fhsc";

    public static final String ACCOUNT_NO_EMPTY_MAILS = "_nem";

    public static final String ACCOUNT_TEXT_MAIL = "_atm";

    public static final String ACCOUNT_IS_MAIL_INVOICE = "_ami";

    public static final String TASK_ID = "_tid";

    public static final String TASK_FORWARD_NUMBER = "_tfwnr";

    public static final String TASK_DATE = "_tdate";

    public static final String TASK_TIMESTAMP = "_ttmst";

    public static final String TASK_DESCRIPTION = "_tdscr";

    public static final String TASK_IS_FIXED_PRICE = "_tisfx";

    public static final String TASK_IS_RECURING = "_tirec";

    public static final String TASK_FIXED_PRICE = "_tfxpr";

    public static final String TASK_TIME_SPEND = "_ttmsp";

    public static final String TASK_IS_PAYED = "_tispa";

    public static final String ERROR_VECTOR = "errv";

    public static final String INVOICE_AMONTH = "_iam";

    public static final String INVOICE_CUSTOMER = "_icu";

    public static final String DONE_BY_EMPL = "_dbe";

    // invoice
    public static final String INVOICE_MONTH = "_imo";

    public static final String INVOICE_YEAR = "_iye";

    public static final String INVOICE_ID = "_iid";

    public static final String INVOICE_CUST_REF = "_icid";

    public static final String INVOICE_PAYDATE = "_ipd";

    public static final String NO_CUSTOMER = "_ncu";

    // filter
    public static final String ACCOUNT_FILTER_CUSTOMER = "_fcus";

    public static final String ACCOUNT_FILTER_CALL_STATE = "_fcst";

    public static final String ACCOUNT_FILTER_CALL_DIR = "_fdir";

    public static final String RECORD_ID = "_rid";

    public static final String RECORD_NUMBER = "_rnb";

    public static final String RECORD_CALLER_NAME = "_cna";

    public static final String RECORD_TEMP_CALLER = "_tca";

    public static final String RECORD_SHORT_TEXT = "_stx";

    public static final String RECORD_LONG_TEXT = "_ltx";

    public static final String RECORD_3W_CUSTOMER_ID = "_3Wrid";

    public static final String RECORD_3W_CALL = "_3Wrcl";

    public static final String RECORD_TIME = "_rti";

    public static final String RECORD_DATE = "_rdt";

    public static final String RECORD_DIR = "_rdi";

    public static final String RECORD_DIR_IN = "_rdin";

    public static final String RECORD_DIR_OUT = "_rdout";

    public static final String RECORD_SMS = "_rsms";

    public static final String RECORD_FORWARD = "_rfwd";

    public static final String RECORD_IMPORTANT = "_rimp";

    public static final String RECORD_AGENDA = "_ragnd";

    public static final String RECORD_FAX = "_rfax";

    public static final String RECORD_INVOICE_LEVEL = "_ril";

    public static final String RECORD_SEARCH_STR = "_rss";

    public static final String RECORD_TO_DELETE = "_rtd";

    public static final String RECORD_UPDATED = "_rud";

    public static final String ACCOUNT_TO_DELETE = "_atd";

    public static final String TASK_TO_DELETE = "_ttd";

    public static final String NEW_RECORD_KEY = "_nrk";

    public static final String INVOICE_TO_FREEZE = "_itf";
    public static final String INVOICE_TO_SAVE = "_its";
    public static final String INVOICE_TO_DELETE = "_itd";
    public static final String INVOICE_TO_SETPAYED = "_isp";

    // attribute names
    public static final String SESSION_OBJ = "_sobj";

    public static final String SESSION_ID = "_sid";

    public static final String ERROR_TXT = "_errt";

    public static final String NEXT_PAGE = "_npg";

    public static final String PREVIOUS_PAGE = "_ppg";
    
    public static final String ACCOUNT_FWDNR = "_afwd";

    public static final String NEW_ACCOUNT_FWDNR = "_nafwd";

    public static final String TBA_HOME = "home";

    // AdminDispatchServlet actions
    public static final String GOTO_RECORD_ADMIN = "GOTO_RECORD_ADMIN";

    public static final String RECORD_DELETE = "RECORD_DELETE";

    public static final String RECORD_UPDATE = "RECORD_UPDATE";

    public static final String GOTO_ADD_RECORD = "GOTO_ADD_RECORD";

    public static final String RECORD_SHOW_PREV = "RECORD_SHOW_PREV";

    public static final String RECORD_SHOW_NEXT = "RECORD_SHOW_NEXT";

    public static final String RECORD_SHOW_PREV_10 = "RECORD_SHOW_PREV_10";

    public static final String RECORD_SHOW_NEXT_10 = "RECORD_SHOW_NEXT_10";

    public static final String SEARCH_SHOW_PREV = "SEARCH_SHOW_PREV";

    public static final String SEARCH_SHOW_NEXT = "SEARCH_SHOW_NEXT";

    public static final String SAVE_RECORD = "SAVE_RECORD";

    public static final String GOTO_ACCOUNT_ADD = "GOTO_ACCOUNT_ADD";

    public static final String GOTO_EMPLOYEE_ADD = "GOTO_EMPLOYEE_ADD";

    public static final String ACCOUNT_DELETE = "ACCOUNT_DELETE";

    public static final String ACCOUNT_UPDATE = "ACCOUNT_UPDATE";

    public static final String ACCOUNT_ADD = "ACCOUNT_ADD";

    public static final String GOTO_ACCOUNT_ADMIN = "GOTO_ACCOUNT_ADMIN";

    public static final String GOTO_ACCOUNT_DELETE = "GOTO_ACCOUNT_DELETE";

    public static final String GOTO_SAVE_ACCOUNT = "GOTO_SAVE_ACCOUNT";

    public static final String SAVE_ACCOUNT = "SAVE_ACCOUNT";

    public static final String ADMIN_HOME = "ADMIN_HOME";

    public static final String ADMIN_LOG_OFF = "ADMIN_LOG_OFF";

    public static final String ACCOUNT_DEREG = "ACCOUNT_DEREG";

    public static final String SAVE_MAN_RECORD = "SAVE_MAN_RECORD";

    public static final String UPDATE_PREFS = "UPDATE_PREFS";

    public static final String SAVE_PREFS = "SAVE_PREFS";

    public static final String GOTO_INVOICE = "GOTO_INVOICE";

    public static final String GENERATE_INVOICE = "GENERATE_INVOICE";

    public static final String SAVE_INVOICE = "SAVE_INVOICE";

    public static final String SAVE_PAYDATE = "SAVE_PAYDATE";

    public static final String GENERATE_ALL_INVOICES = "GENERATE_ALL_INVOICES";

    public static final String GENERATE_ALL_INVOICEDOCS = "GENERATE_ALL_INVOICEDOCS";

    public static final String GOTO_RECORD_SEARCH = "GOTO_RECORD_SEARCH";

    public static final String TASK_DELETE = "TASK_DELETE";

    public static final String TASK_UPDATE = "TASK_UPDATE";

    public static final String TASK_ADD = "TASK_ADD";

    public static final String TASK_SHOW_PREV = "TASK_SHOW_PREV";

    public static final String TASK_SHOW_NEXT = "TASK_SHOW_NEXT";

    public static final String EMPLCOST_SHOW_PREV = "EMPLCOST_SHOW_PREV";

    public static final String EMPLCOST_SHOW_NEXT = "EMPLCOST_SHOW_NEXT";

    public static final String GOTO_TASK_ADMIN = "GOTO_TASK_ADMIN";

    public static final String GOTO_TASK_ADD = "GOTO_TASK_ADD";

    public static final String GOTO_NOTLOGGED_CALLS = "GOTO_NOTLOGGED_CALLS";

    public static final String SAVE_TASK = "SAVE_TASK";

    public static final String MAIL_CUSTOMER = "MAIL_CUSTOMER";

    public static final String GET_OPEN_CALLS = "GET_OPEN_CALLS";

    public static final String REFRESH_OPEN_CALLS = "REFRESH_OPEN_CALLS";

    public static final String SAVE_NEW_CALL = "SAVE_NEW_CALL";
    
    public static final String SAVE_NEW_SUBCUSTOMER = "SAVE_NEW_SUBCUSTOMER";

    public static final String REMOVE_OPEN_CALL = "REMOVE_OPEN_CALL";

    public static final String GOTO_INVOICE_ADMIN = "GOTO_INVOICE_ADMIN";

    public static final String GOTO_OPEN_INVOICE = "GOTO_OPEN_INVOICE";

    public static final String GOTO_INVOICE_ADD = "GOTO_INVOICE_ADD";

    public static final String INVOICE_ADD = "INVOICE_ADD";

    public static final String GOTO_EMPLOYEE_ADMIN = "GOTO_EMPLOYEE_ADMIN";

    public static final String GOTO_EMPLOYEE_COST = "GOTO_EMPLOYEE_COST";

    public static final String INVOICE_FREEZE = "INVOICE_FREEZE";
    public static final String INVOICE_MAIL = "INVOICE_MAIL";
    public static final String INVOICE_DELETE = "INVOICE_DELETE";
    public static final String INVOICE_SETPAYED = "INVOICE_SETPAYED";

    public static final String INVOICE_DETAIL = "INVOICE_DETAIL";

    public static final String MAIL_IT = "MAIL_IT";

    public static final String SHOW_MAIL_ERROR = "SHOW_MAIL_ERROR";

    // LoginServlet actions
    public static final String ACTION_LOGIN = "ACTION_LOGIN";

    public static final String ACTION_REGISTER = "ACTION_REGISTER";

    public static final String ACTION_FIRST_REGISTER = "ACTION_FIRST_REGISTER";

    public static final String ACTION_DICTAAT_FORM = "ACTION_DICTAAT_FORM";

    public static final String ACTION_TELEMARKETING_FORM = "ACTION_TELEMARKETING_FORM";

    // CustomerDispatchServlet
    public static final String ACTION_LOGOFF = "ACTION_LOGOFF";

    public static final String ACTION_SHOW_CALLS = "ACTION_SHOW_CALLS";

    public static final String ACTION_REFRESH_CALLS = "ACTION_REFRESH_CALLS";

    public static final String ACTION_GOTO_SEARCH_PAGE = "ACTION_GOTO_SEARCH_PAGE";

    public static final String ACTION_SEARCH_CALLS = "ACTION_SEARCH_CALLS";

    public static final String ACTION_SHOW_TASKS = "ACTION_SHOW_TASKS";

    // attribute values
    public static final String ACCOUNT_FILTER_ALL = "_all";

    public static final String ACCOUNT_FILTER_UNFINISHED = "_unf";

    public static final String ACCOUNT_FILTER_FINISHED = "_fin";

    public static final String ACCOUNT_FILTER_IN = "_f_in";

    public static final String ACCOUNT_FILTER_OUT = "_f_out";

    public static final String YES = "_y";

    public static final String NO = "_n";

    public static final String NONE = "_none";

    public static final String AGENDA_NO = "_pna";
    public static final String AGENDA_STANDARD = "_pst";
    public static final String AGENDA_PERC_PER_CALL = "_ppc";
    public static final String AGENDA_PERC_ALL_CALL = "_pac";
    public static final String AGENDA_EURO_PER_CALL = "_epc";
    public static final String AGENDA_EURO_ALL_CALL = "_eac";

    public static final String RECORD_LEVEL1 = "_rl1";
    public static final String RECORD_LEVEL2 = "_rl2";
    public static final String RECORD_LEVEL3 = "_rl3";

    // jsp-perms attribute
    public static final String FORM_NAME = "_fnm";

    public static final String FORM_COMPANY = "_fcmp";

    public static final String FORM_EMAIL = "_fem";

    public static final String FORM_TEL = "_ftel";

    public static final String FORM_FAX = "_ffax";

    public static final String FORM_TEXT = "_ftxt";

    public static final String FORM_DICTAAT_TECH = "_fdt";

    public static final String FORM_DIC_BRAND = "_fbr";

    public static final String FORM_DURATION = "_fdu";

    public static final String FORM_LANGUAGE = "_fla";

    // Forward number block (must be replaced by a XML file)
    // forward number assigned to customer, internal number, nacht number

    public static final String[][] NUMBER_BLOCK = {
            // { "409000", "20", "120", "TheBusinessAssistant", "800" },
            { "409000", "20", "550", "TheBusinessAssistant", "800" }, { "409001", "128", "128", "Fax", "128" },
            // G808 G808 <-- N05 03/08/10 20:32 00:00:00 0 ST 0473949777................ M
            // 00:03 0.00 ................ Dr Van Eyck..... G808 G808 N005
            { "409002", "22", "802", "9002", "802" }, { "409003", "23", "803", "9003", "803" }, { "409004", "24", "804", "9004", "804" }, { "409005", "25", "805", "9005", "805" }, { "409006", "26", "806", "9006", "806" }, { "409007", "27", "807", "9007", "807" }, { "409008", "28", "808", "9008", "808" }, { "409009", "29", "809", "9009", "809" }, { "409011", "31", "811", "9011", "811" }, { "409012", "32", "812", "9012", "812" }, { "409013", "33", "813", "9013", "813" }, { "409014", "34", "814", "9014", "814" }, { "409015", "35", "815", "9015", "815" }, { "409016", "36", "816", "9016", "816" }, { "409017", "37", "817", "9017", "817" }, { "409018", "38", "818", "9018", "818" }, { "409019", "39", "819", "9019", "819" },
            // G856 G856 <-- N07 03/08/10 20:31 00:00:00 0 ST 0473949777................ M
            // 00:02 0.00 ................ Rombaut Riet.... G856 G856 N007
            { "473050", "850", "850", "3050", "850" }, { "473051", "851", "851", "3051", "851" }, { "473052", "852", "852", "3052", "852" }, { "473053", "853", "853", "3053", "853" }, { "473054", "854", "854", "3054", "854" }, { "473055", "855", "855", "3055", "855" }, { "473056", "856", "856", "3056", "856" }, { "473057", "857", "857", "3057", "857" }, { "473058", "858", "858", "3058", "858" }, { "473059", "859", "859", "3059", "859" },
            // G104 G104 <-- N08 03/08/10 20:30 00:00:00 0 ST 0473949777................ M
            // 00:02 0.00 ................ Geerts.......... G104 G104 N008
            { "402100", "100", "100", "2100", "100" }, { "402101", "101", "101", "2101", "101" }, { "402102", "102", "102", "2102", "102" }, { "402103", "103", "103", "2103", "103" }, { "402104", "104", "104", "2104", "104" }, { "402105", "105", "105", "2105", "105" }, { "402106", "106", "106", "2106", "106" }, { "402107", "107", "107", "2107", "107" }, { "402108", "108", "108", "2108", "108" }, { "402109", "109", "109", "2109", "109" },
            // G8295 G8295 <-- N06 03/08/10 20:28 00:00:00 0 ST 0473949777................ M
            // 00:02 0.00 ................ Raeymaeckers.... G8295 G8295 N006
            { "408290", "8290", "8290", "8290", "8290" }, { "408291", "8291", "8291", "8291", "8291" }, { "408292", "8292", "8292", "8292", "8292" }, { "408293", "8293", "8293", "8293", "8293" }, { "408294", "8294", "8294", "8294", "8294" }, { "408295", "8295", "8295", "8295", "8295" }, { "408296", "8296", "8296", "8296", "8296" }, { "408297", "8297", "8297", "8297", "8297" }, { "408298", "8298", "8298", "8298", "8298" }, { "408299", "8299", "8299", "8299", "8299" },
            // A1803 A1803 <-- N06 03/08/10 20:04 00:00:00 0 ST 0473949777................ M
            // 00:03 0.00 ................ Katrien Van Dael A1803 A1803 N006
            { "401800", "1800", "1800", "1800", "1800" }, { "401801", "1801", "1801", "1801", "1801" }, { "401802", "1802", "1802", "1802", "1802" }, { "401803", "1803", "1803", "1803", "1803" }, { "401804", "1804", "1804", "1804", "1804" }, { "401805", "1805", "1805", "1805", "1805" }, { "401806", "1806", "1806", "1806", "1806" }, { "401807", "1807", "1807", "1807", "1807" }, { "401808", "1808", "1808", "1808", "1808" }, { "401809", "1809", "1809", "1809", "1809" },
            //
            { "445050", "124", "124", "5050", "124" }, { "445051", "125", "125", "5051", "125" }, { "445052", "126", "126", "5052", "126" }, { "445053", "127", "127", "5053", "127" }, { "445054", "150", "150", "5054", "150" }, { "445055", "151", "151", "5055", "151" }, { "445056", "132", "132", "5056", "132" }, { "445057", "133", "133", "5057", "133" }, { "445058", "134", "134", "5058", "134" }, { "445059", "135", "135", "5059", "135" },

            { "noCall_1", "0", "0", "0", "0" }, // van hier af enkel klanten met geen oproepen
            { "noCall_2", "1", "1", "1", "1" }, { "noCall_3", "2", "2", "2", "2" }, { "noCall_4", "3", "3", "3", "3" }, { "noCall_5", "4", "4", "4", "4" }, { "noCall_6", "5", "5", "5", "5" }, { "noCall_7", "6", "6", "6", "6" }, { "noCall_8", "7", "7", "7", "7" }, { "noCall_9", "8", "8", "8", "8" }, { "noCall_10", "9", "9", "9", "9" }, { "noCall_11", "9", "9", "9", "9" }, { "noCall_12", "9", "9", "9", "9" }, { "noCall_13", "9", "9", "9", "9" }, { "noCall_14", "9", "9", "9", "9" }, { "noCall_15", "9", "9", "9", "9" }, { "noCall_16", "9", "9", "9", "9" }, { "noCall_17", "9", "9", "9", "9" }, { "noCall_18", "9", "9", "9", "9" }, { "noCall_19", "9", "9", "9", "9" }, { "noCall_20", "9", "9", "9", "9" }, { "noCall_21", "9", "9", "9", "9" }, { "noCall_22", "9", "9", "9", "9" }, { "noCall_23", "9", "9", "9", "9" }, { "noCall_24", "9", "9", "9", "9" }, { "noCall_25", "9", "9", "9", "9" }, { "noCall_26", "9", "9", "9", "9" }, { "noCall_27", "9", "9", "9", "9" }, { "noCall_28", "9", "9", "9", "9" }, { "noCall_29", "9", "9", "9", "9" }, { "noCall_30", "9", "9", "9", "9" }, { "noCall_31", "9", "9", "9", "9" }, { "noCall_32", "9", "9", "9", "9" }, { "noCall_33", "9", "9", "9", "9" }, { "noCall_34", "9", "9", "9", "9" }, { "noCall_35", "9", "9", "9", "9" }, { "noCall_36", "9", "9", "9", "9" }, { "noCall_37", "9", "9", "9", "9" }, { "noCall_38", "9", "9", "9", "9" }, { "noCall_39", "9", "9", "9", "9" }, { "noCall_40", "9", "9", "9", "9" },

            { "subKlant_1", "0", "0", "0", "0" }, // van hier af enkel klanten met geen oproepen
            { "subKlant_2", "1", "1", "1", "1" }, { "subKlant_3", "2", "2", "2", "2" }, { "subKlant_4", "3", "3", "3", "3" }, { "subKlant_5", "4", "4", "4", "4" }, { "subKlant_6", "5", "5", "5", "5" }, { "subKlant_7", "6", "6", "6", "6" }, { "subKlant_8", "7", "7", "7", "7" }, { "subKlant_9", "8", "8", "8", "8" }, { "subKlant_10", "9", "9", "9", "9" }, { "subKlant_11", "9", "9", "9", "9" }, { "subKlant_12", "9", "9", "9", "9" }, { "subKlant_13", "9", "9", "9", "9" }, { "subKlant_14", "9", "9", "9", "9" }, { "subKlant_15", "9", "9", "9", "9" }, { "subKlant_16", "9", "9", "9", "9" }, { "subKlant_17", "9", "9", "9", "9" }, { "subKlant_18", "9", "9", "9", "9" }, { "subKlant_19", "9", "9", "9", "9" }, { "subKlant_20", "9", "9", "9", "9" }, { "subKlant_21", "9", "9", "9", "9" }, { "subKlant_22", "9", "9", "9", "9" }, { "subKlant_23", "9", "9", "9", "9" }, { "subKlant_24", "9", "9", "9", "9" }, { "subKlant_25", "9", "9", "9", "9" }, { "subKlant_26", "9", "9", "9", "9" }, { "subKlant_27", "9", "9", "9", "9" }, { "subKlant_28", "9", "9", "9", "9" }, { "subKlant_29", "9", "9", "9", "9" }, { "subKlant_30", "9", "9", "9", "9" }, { "subKlant_31", "9", "9", "9", "9" }, { "subKlant_32", "9", "9", "9", "9" }, { "subKlant_33", "9", "9", "9", "9" }, { "subKlant_34", "9", "9", "9", "9" }, { "subKlant_35", "9", "9", "9", "9" }, { "subKlant_36", "9", "9", "9", "9" }, { "subKlant_37", "9", "9", "9", "9" }, { "subKlant_38", "9", "9", "9", "9" }, { "subKlant_39", "9", "9", "9", "9" }, { "subKlant_40", "9", "9", "9", "9" }, { "subKlant_41", "9", "9", "9", "9" }, { "subKlant_42", "9", "9", "9", "9" }, { "subKlant_43", "9", "9", "9", "9" }, { "subKlant_44", "9", "9", "9", "9" }, { "subKlant_45", "9", "9", "9", "9" }, { "subKlant_46", "9", "9", "9", "9" }, { "subKlant_47", "9", "9", "9", "9" }, { "subKlant_48", "9", "9", "9", "9" }, { "subKlant_49", "9", "9", "9", "9" }, { "subKlant_50", "9", "9", "9", "9" }, { "subKlant_51", "9", "9", "9", "9" }, { "subKlant_52", "9", "9", "9", "9" },
            { "subKlant_53", "9", "9", "9", "9" }, { "subKlant_54", "9", "9", "9", "9" }, { "subKlant_55", "9", "9", "9", "9" }, { "subKlant_56", "9", "9", "9", "9" }, { "subKlant_57", "9", "9", "9", "9" }, { "subKlant_58", "9", "9", "9", "9" }, { "subKlant_59", "9", "9", "9", "9" }, { "subKlant_60", "9", "9", "9", "9" }, { "subKlant_61", "9", "9", "9", "9" }, { "subKlant_62", "9", "9", "9", "9" }, { "subKlant_63", "9", "9", "9", "9" }, { "subKlant_64", "9", "9", "9", "9" }, { "subKlant_65", "9", "9", "9", "9" }, { "subKlant_66", "9", "9", "9", "9" }, { "subKlant_67", "9", "9", "9", "9" }, { "subKlant_68", "9", "9", "9", "9" }, { "subKlant_69", "9", "9", "9", "9" }, { "subKlant_70", "9", "9", "9", "9" }, { "subKlant_71", "9", "9", "9", "9" }, { "subKlant_72", "9", "9", "9", "9" }, { "subKlant_73", "9", "9", "9", "9" }, { "subKlant_74", "9", "9", "9", "9" }, { "subKlant_75", "9", "9", "9", "9" }, { "subKlant_76", "9", "9", "9", "9" }, { "subKlant_77", "9", "9", "9", "9" }, { "subKlant_78", "9", "9", "9", "9" }, { "subKlant_79", "9", "9", "9", "9" }, { "subKlant_80", "9", "9", "9", "9" }, { "subKlant_81", "9", "9", "9", "9" }, { "subKlant_82", "9", "9", "9", "9" }, { "subKlant_83", "9", "9", "9", "9" }, { "subKlant_84", "9", "9", "9", "9" }, { "subKlant_85", "9", "9", "9", "9" }, { "subKlant_86", "9", "9", "9", "9" }, { "subKlant_87", "9", "9", "9", "9" }, { "subKlant_88", "9", "9", "9", "9" }, { "subKlant_89", "9", "9", "9", "9" }, { "subKlant_90", "9", "9", "9", "9" }, { "subKlant_91", "9", "9", "9", "9" }, { "subKlant_92", "9", "9", "9", "9" }, { "subKlant_93", "9", "9", "9", "9" }, { "subKlant_94", "9", "9", "9", "9" }, { "subKlant_95", "9", "9", "9", "9" }, { "subKlant_96", "9", "9", "9", "9" }, { "subKlant_97", "9", "9", "9", "9" }, { "subKlant_98", "9", "9", "9", "9" }, { "subKlant_99", "9", "9", "9", "9" }, { "subKlant_100", "9", "9", "9", "9" }, { "subKlant_101", "9", "9", "9", "9" }, { "subKlant_102", "9", "9", "9", "9" },
            { "subKlant_103", "9", "9", "9", "9" }, { "subKlant_104", "9", "9", "9", "9" }, { "subKlant_105", "9", "9", "9", "9" }, { "subKlant_106", "9", "9", "9", "9" }, { "subKlant_107", "9", "9", "9", "9" }, { "subKlant_108", "9", "9", "9", "9" }, { "subKlant_109", "9", "9", "9", "9" }, { "subKlant_110", "9", "9", "9", "9" }, { "subKlant_111", "9", "9", "9", "9" }, { "subKlant_112", "9", "9", "9", "9" }, { "subKlant_113", "9", "9", "9", "9" }, { "subKlant_114", "9", "9", "9", "9" }, { "subKlant_115", "9", "9", "9", "9" }, { "subKlant_116", "9", "9", "9", "9" }, { "subKlant_117", "9", "9", "9", "9" }, { "subKlant_118", "9", "9", "9", "9" }, { "subKlant_119", "9", "9", "9", "9" }, { "subKlant_120", "9", "9", "9", "9" }, { "subKlant_121", "9", "9", "9", "9" }, { "subKlant_122", "9", "9", "9", "9" }, { "subKlant_123", "9", "9", "9", "9" }, { "subKlant_124", "9", "9", "9", "9" }, { "subKlant_125", "9", "9", "9", "9" }, { "subKlant_126", "9", "9", "9", "9" }, { "subKlant_127", "9", "9", "9", "9" }, { "subKlant_128", "9", "9", "9", "9" }, { "subKlant_129", "9", "9", "9", "9" }, { "subKlant_130", "9", "9", "9", "9" }, { "subKlant_131", "9", "9", "9", "9" }, { "subKlant_132", "9", "9", "9", "9" }, { "subKlant_133", "9", "9", "9", "9" }, { "subKlant_134", "9", "9", "9", "9" }, { "subKlant_135", "9", "9", "9", "9" }, { "subKlant_136", "9", "9", "9", "9" }, { "subKlant_137", "9", "9", "9", "9" }, { "subKlant_138", "9", "9", "9", "9" }, { "subKlant_139", "9", "9", "9", "9" }, { "subKlant_140", "9", "9", "9", "9" }, { "subKlant_141", "9", "9", "9", "9" }, { "subKlant_142", "9", "9", "9", "9" }, { "subKlant_143", "9", "9", "9", "9" }, { "subKlant_144", "9", "9", "9", "9" }, { "subKlant_145", "9", "9", "9", "9" }, { "subKlant_146", "9", "9", "9", "9" }, { "subKlant_147", "9", "9", "9", "9" }, { "subKlant_148", "9", "9", "9", "9" }, { "subKlant_149", "9", "9", "9", "9" }, { "subKlant_150", "9", "9", "9", "9" }, { "subKlant_151", "9", "9", "9", "9" },
            { "subKlant_152", "9", "9", "9", "9" }, { "subKlant_153", "9", "9", "9", "9" }, { "subKlant_154", "9", "9", "9", "9" }, { "subKlant_155", "9", "9", "9", "9" }, { "subKlant_156", "9", "9", "9", "9" }, { "subKlant_157", "9", "9", "9", "9" }, { "subKlant_158", "9", "9", "9", "9" }, { "subKlant_159", "9", "9", "9", "9" }, { "subKlant_160", "9", "9", "9", "9" }, { "subKlant_161", "9", "9", "9", "9" }, { "subKlant_162", "9", "9", "9", "9" }, { "subKlant_163", "9", "9", "9", "9" }, { "subKlant_164", "9", "9", "9", "9" }, { "subKlant_165", "9", "9", "9", "9" }, { "subKlant_166", "9", "9", "9", "9" }, { "subKlant_167", "9", "9", "9", "9" }, { "subKlant_168", "9", "9", "9", "9" }, { "subKlant_169", "9", "9", "9", "9" }, { "subKlant_170", "9", "9", "9", "9" }, { "subKlant_171", "9", "9", "9", "9" }, { "subKlant_172", "9", "9", "9", "9" }, { "subKlant_173", "9", "9", "9", "9" }, { "subKlant_174", "9", "9", "9", "9" }, { "subKlant_175", "9", "9", "9", "9" }, { "subKlant_176", "9", "9", "9", "9" }, { "subKlant_177", "9", "9", "9", "9" }, { "subKlant_178", "9", "9", "9", "9" }, { "subKlant_179", "9", "9", "9", "9" },

            { "409030", "50", "150", "9030", "830" }, // van hier af enkel uitgaande oproepen (facturatie)
            { "409031", "51", "151", "9031", "831" }, { "409032", "52", "152", "9032", "832" }, { "409033", "53", "153", "9033", "833" }, { "409034", "54", "154", "9034", "834" }, { "409035", "55", "155", "9035", "835" }, { "409036", "56", "156", "9036", "836" }, { "409037", "57", "157", "9037", "837" }, { "409038", "58", "158", "9038", "838" }, { "409039", "59", "159", "9039", "839" }, { "409040", "60", "160", "9040", "840" } };

    /*
     * public static final String[][] NUMBER_BLOCK =
     * {{"409000","A20","A120","TheBusinessAssistant","500"},
     * {"409001","A21","A121","Fax","501"}, {"409002","A22","A122","Thuis","501"},
     * {"409003","A23","A123","9003","501"}, {"409004","A24","A124","9004","501"},
     * {"409005","A25","A125","9005","501"}, {"409006","A26","A126","9006","501"},
     * {"409007","A27","A127","9007","501"}, {"409008","A28","A128","9008","502"},
     * {"409009","A29","A129","9009","503"}, {"409011","A31","A131","9011","504"},
     * {"409012","A32","A132","9012","505"}, {"409013","A33","A133","9013","506"},
     * {"409014","A34","A134","9014","507"}, {"409015","A35","A135","9015","508"},
     * {"409016","A36","A136","9016","509"}, {"409017","A37","A137","9017","510"},
     * {"409018","A38","A138","9018","511"}, {"409019","A39","A139","9019","512"},
     * {"409020","A40","A140","9020","513"}, {"409021","A41","A141","9021","514"},
     * {"409022","A42","A142","9022","515"}, {"409023","A43","A143","9023","516"},
     * {"409024","A44","A144","9024","517"}, {"409025","A45","A145","9025","518"},
     * {"409026","A46","A146","9026","519"}, {"409027","A47","A147","9027","520"},
     * {"409028","A48","A148","9028","521"}, {"409029","A49","A149","9029","522"},
     * {"409030","A50","A150","9030","523"}, {"409031","A51","A151","9031","524"},
     * {"409032","A52","A152","9032","525"}, {"409033","A53","A153","9033","526"},
     * {"409034","A54","A154","9034","527"}, {"409035","A55","A155","9035","528"},
     * {"409036","A56","A156","9036","529"}, {"409037","A57","A157","9037","530"},
     * {"409038","A58","A158","9038","531"}, {"409039","A59","A159","9039","532"},
     * {"409040","A60","A160","9040","533"}};
     */

    public static final String[] MONTHS = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december" };

    public static final int RECORD_PAGE_LEN = 30;

    // CallLogger attributes
    public static final String CALLLOG_SCOPE = "day";

    public static final String CALLLOG_DIR = "upd";

    // calllogger file paths
    public static final String CALLLOG_PATH = "C:\\jetty\\projects\\TBA\\calllogs";

    public static final String RECORDS_OF_TODAY_PATH = "C:\\jetty\\projects\\TBA\\calllogs\\";

    public static final long SECONDS = 1000;

    public static final long MINUTES = SECONDS * 60;

    public static final long HOURS = MINUTES * 60;

    public static final long DAYS = HOURS * 24;

    public static final long RECORD_DELETE_EXPIRE = DAYS * 365; // 90 days

    public static final long RECORD_NOTLOGGED_EXPIRE = DAYS * 10; // 10 days

    public static final long RECORD_AUTO_RELEASE_EXPIRE = DAYS * 3; // 10 days

    public static final long CUSTOMER_SESSION_TIMEOUT = MINUTES * 120; // 5
    // minuten

    public static final long ADMIN_SESSION_TIMEOUT = MINUTES * 60; // 50
    // minuten

    public static final long NORMAL_CALL_LENGTH = 90; // 90 seconds

    public static final double CENT_PER_LONG_CALL_SECOND = 0.01; // 1,4 cent
    public static final String CENT_PER_LONG_CALL_SECOND_STR = "0.01"; // 0,5
                                                                       // cent
    public static final int CENT_PER_HOUR_WORK = 2750; // 27,5 euro

    public static final int kOutCost = 100; // euro cent
    public static final int kSmsCost = 100; // euro cent
    public static final int kForwardCost = 100; // euro cent
    public static final int kFaxCost = 100; // euro cent
    public static final int kStandardAgendaCost = 45; // euro

    public static final boolean kCountAllLongCalls = false;
    public static final boolean kCountLongFwdCalls = false;
    public static final double kFacLong = 0.014;
    public static final double kFacLongFwd = 0.014;

    public static final int kFacTblMinCalls_I = 75;
    public static final double kFacTblStartCost_I = 110.0;
    public static final double kFacTblExtraCost_I = 1.00;
    public static final int kFacTblMinCalls_II = 40;
    public static final double kFacTblStartCost_II = 80.0;
    public static final double kFacTblExtraCost_II = 1.0;
    public static final int kFacTblMinCalls_III = 0;
    public static final double kFacTblStartCost_III = 80.0;
    public static final double kFacTblExtraCost_III = 0.0;
    // week tarief
    public static final int kFacTblMinCalls_IV = 12;
    public static final double kFacTblStartCost_IV = 60.0;
    public static final double kFacTblExtraCost_IV = 1.45;

    public static final long REFRESH_LONG = 3000; // 300 seconds

    public static final String REFRESH = "3000"; // 300 seconds

    public static final short MAX_MAIL_HOUR = 23;

    public static final String INVOICE_TYPE_STD = "_itst";
    public static final String INVOICE_TYPE_CUSTOM = "_itcu";
    public static final String INVOICE_TYPE_TELEMARK = "_itte";
    public static final String INVOICE_NO_CALLS = "_itnc";
    public static final String INVOICE_TYPE_WEEK = "_itwe";

    private Constants()
    {
    }
}
