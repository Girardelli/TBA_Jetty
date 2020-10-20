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
   public static final String NANCY_EMAIL = "ine.hermans@theBusinessAssistant.be";
   public static final String WS_LOGIN = "loginid:";

   // Account Role strings.
   public static final String EJB_ACC_ROLE_CUSTOMER = "klant";
   public static final String EJB_ACC_ROLE_ADMIN = "admin";

   // HTML page names
   public static final String CMPNY_NAME = "TheBusinessAssistant";
   public static final String DOMAIN_NAME = "thebusinessassistant.be/tba/";
   public static final String TBA_URL_BASE = "https://" + DOMAIN_NAME;
   public static final String TBA_URL_WS = "wss://thebusinessassistant.be/tba/ws";
   public static final String MYSQL_URL = "jdbc:mysql://localhost/tbadb?user=root&password=gzb625$";
   // public static final String INVOICE_HEAD_TMPL =
   // "C:\\jboss-4.0.2\\projects\\TBA\\templates\\invoice-head-template.htm";
   public static final String TEMP_DIR = "c:\\temp";
   public static final String INVOICE_HEAD_TMPL = "C:\\jetty\\templates\\invoiceTemplate.pdf";
   public static final String INVOICE_DETAIL_TMPL = "C:\\jetty\\templates\\invoice-detail-template.htm";
   public static final String INVOICE_FOOTER_TMPL = "C:\\jetty\\templates\\invoice-footer-template.htm";
   public static final String INVOICE_DIR = "C:\\TheBusinessAssistant\\TheBusinessAssistant\\facturen\\";
   public static final String FILEUPLOAD_DIR = "C:\\jetty\\tempdir";
   public static final String WORKORDER_FILEUPLOAD_DIR = "C:\\Users\\yves\\Dropbox\\tbapublic\\Workorders";
   public static final String WC_KLANTEN_XML = "WoltersKluwerKlanten.xml";
   public static final String WC_VERKOPEN_XML = "WoltersKluwerVerkopen.xml";
   public static final String ADMIN_LOGIN_HTML = "/tba/admin.html";
   public static final String BAD_LOGIN_HTML = "/badlogin.html";
   public static final String LOGIN_HTML = "/tba/login.html";
   public static final String SERVLET_LOGIN_HTML = "/login.html";
   public static final String HOME_HTML = "/index.html";
   public static final String FORM_SUBMIT_SUCCESS = "/formok.html";
   public static final String EMPL_MAIL_ADDR = "anke.rombouts@thebusinessassistant.be;jolien.driesen@thebusinessassistant.be;emily.van.ginneken@thebusinessassistant.be;jolien.soetemans@thebusinessassistant.be";

   // JSP page names
   public static final String ADMIN_FAIL_JSP = "/admin/adminfail.jsp";
   public static final String PROTECT_FAIL_JSP = "/protect/protectfail.jsp";
   public static final String ADD_ACCOUNT_JSP = "/admin/addAccount.jsp";
   public static final String ADD_EMPLOYEE_JSP = "/admin/addEmployee.jsp";
   public static final String ADMIN_EMPLOYEE_COST_JSP = "/admin/employeecost.jsp";
   public static final String ADMIN_ACCOUNT_JSP = "/admin/adminaccount.jsp";
   public static final String ARCHIVED_ACCOUNT_JSP = "/admin/archivedaccount.jsp";
   public static final String ADMIN_EMPLOYEE_JSP = "/admin/adminemployee.jsp";
   public static final String ADMIN_LOGIN = "/admin.html";
   public static final String SHOW_ERROR_JSP = "/admin/showMailFail.jsp";
   public static final String UPDATE_ACCOUNT_JSP = "/admin/updateAccount.jsp";
   // public static final String SELECT_SUBCUSTOMER_JSP =
   // "/admin/selectSubCustomer.jsp";
   public static final String UPDATE_RECORD_JSP = "/admin/updateRecord.jsp";
   public static final String ADD_RECORD_JSP = "/admin/addrecord.jsp";
   public static final String INVOICE_JSP = "/admin/invoice.jsp";
   public static final String ADMIN_SEARCH_JSP = "/admin/search.jsp";
   public static final String ADMIN_TASK_JSP = "/admin/admintasks.jsp";
   public static final String ADD_TASK_JSP = "/admin/addtask.jsp";
   public static final String ADD_INVOICE_JSP = "/admin/addinvoice.jsp";
   public static final String UPDATE_TASK_JSP = "/admin/updatetask.jsp";
   public static final String ANNOUNCEMENT_JSP = "/admin/announcement.jsp";
   public static final String ARE_YOU_SURE_JSP = "/admin/areyousure.jsp";
   // public static final String NEW_CALL_JSP = "/admin/newcall.jsp";
   public static final String ADMIN_INVOICE_JSP = "/admin/admininvoice.jsp";
   public static final String OPEN_INVOICE_JSP = "/admin/openinvoice.jsp";
   public static final String CANVAS_JSP = "/admin/canvas.jsp";
   public static final String ADMIN_WORK_ORDER_JSP = "/admin/adminWorkOrders.jsp";
   public static final String WORKORDER_JSP = "/admin/updateWorkOrder.jsp";
   // customer jsp
   public static final String LOGIN_HOME_JSP = "/protect/prothome.jsp";
   public static final String REGISTER_JSP = "/protect/register.jsp";
   public static final String LOGIN_ERROR_JSP = "/loginerror.jsp";
   public static final String CLIENT_CALLS_JSP = "/protect/clientcalls.jsp";
   public static final String CLIENT_ARCHIVED_CALLS_JSP = "/protect/archivedcalls.jsp";
   public static final String CLIENT_SHOW_REC_JSP = "/protect/showrecord.jsp";
   public static final String CLIENT_PREF_JSP = "/protect/updatepref.jsp";
   public static final String CLIENT_SEARCH_JSP = "/protect/searchcust.jsp";
   public static final String CLIENT_SHOW_TASKS_JSP = "/protect/clienttasks.jsp";
   public static final String CLIENT_WORKORDERS_JSP = "/protect/myworkorders.jsp";
   public static final String CLIENT_UPDATE_WORKORDER_JSP = "/protect/updateworkorder.jsp";
   public static final String DICTAAT_FORM_JSP = "/dictaatform.jsp";

   // session attribute constants
   public static final String SRV_ACTION = "_act";

   // error responses
   public static final String NO_ERROR = "OK";
   public static final String ADD_ACCOUNT_FAILED = "NOK";
   public static final String NO_VALUE = "NOVALUE";
   public static final String ACCOUNT_EXISTS = "Account already exists";
   public static final String ERROR_VECTOR = "errv";

   // Accounts
   public static final String ACCOUNT_SELECT = "_sel";
   public static final String ACCOUNT_FULLNAME = "_fn";
   public static final String ACCOUNT_FORWARD_NUMBER = "_fwnr";
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
   public static final String ACCOUNT_FAC_STD_IN_CALL = "_fcsic";
   public static final String ACCOUNT_FAC_STD_OUT_CALL = "_fcsoc";
   public static final String ACCOUNT_FAC_FAX_CALL = "_fcfax";
   public static final String ACCOUNT_FAC_OUT_LEVEL1 = "_fcl1";
   public static final String ACCOUNT_FAC_OUT_LEVEL2 = "_fcl2";
   public static final String ACCOUNT_FAC_OUT_LEVEL3 = "_fcl3";
   public static final String ACCOUNT_FAC_AGENDA_FIXED = "_fcafx"; // fixed fee on all calls (yes or no)
   public static final String ACCOUNT_FAC_AGENDA_CALL = "_fcpac"; // price of a agenda call (can also be the price for fixed fee)
   public static final String ACCOUNT_FAC_AGENDA_UNIT = "_fcaun"; // unit of the agenda price (% or euro)
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
   public static final String ACCOUNT_NR = "_fanr";
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
   public static final String ACCOUNT_COUNTRY_CODE = "_acc";
   public static final String ACCOUNT_INFO = "_aci";
   public static final String ACCOUNT_TO_DELETE = "_atd";
   public static final String ACCOUNT_FWDNR = "_afwd";
   public static final String ACCOUNT_NEW_FWDNR = "_nafwd";
   public static final String ACCOUNT_REDIRECT_ACCOUNT_ID = "_rdaid";

   // Task
   public static final String TASK_ID = "_tid";
   public static final String TASK_FORWARD_NUMBER = "_tfwnr";
   public static final String TASK_DATE = "_tdate";
   public static final String TASK_TIMESTAMP = "_ttmst";
   public static final String TASK_DESCRIPTION = "_tdscr";
   public static final String TASK_FINTROID = "_tfinid";
   public static final String TASK_EXEC_DATE = "_texecd";
   public static final String TASK_VAL_DATE = "_tvald";
   public static final String TASK_FROM_BANK_NR = "_tbnknr";
   public static final String TASK_PAY_DETAILS = "_tdetail";
   public static final String TASK_IS_FIXED_PRICE = "_tisfx";
   public static final String TASK_IS_RECURING = "_tirec";
   public static final String TASK_FIXED_PRICE = "_tfxpr";
   public static final String TASK_TIME_SPEND = "_ttmsp";
   public static final String TASK_IS_PAYED = "_tispa";
   public static final String TASK_DONE_BY_EMPL = "_tdbe";
   public static final String TASK_TO_DELETE = "_ttd";
   public static final String TASK_ACCOUNT_ID = "_tai";

   // Workorder
   public static final String WORKORDER_ID = "_woid";
   public static final String WORKORDER_TITLE = "_woti";
   public static final String WORKORDER_INSTRUCTION = "_woin";
   public static final String WORKORDER_DUEDATE = "_wodd";
   public static final String WORKORDER_STATE = "_wost";
   public static final String WORKORDER_FILE = "_wofi";
   public static final String WORKORDER_FILE_ID = "_wfid";
   public static final String WORKORDER_URGENT = "_wiur";

   // invoice
   public static final String INVOICE_AMONTH = "_iam";
   public static final String INVOICE_CUSTOMER = "_icu";
   public static final String INVOICE_MONTH = "_imo";
   public static final String INVOICE_YEAR = "_iye";
   public static final String INVOICE_ID = "_iid";
   public static final String INVOICE_CUST_REF = "_icid";
   public static final String INVOICE_PAYDATE = "_ipd";
   public static final String NO_CUSTOMER = "_ncu";
   public static final String FINTRO_FILE = "_iff";
   public static final String FINTRO_PROCESS_FILE = "_ipf";
   public static final String INVOICE_INFO = "_ii";
   public static final String INVOICE_IS_CREDITNOTA = "_iic";
   public static final String INVOICE_NR = "_inr";
   public static final String INVOICE_DESCRIPTION = "_ide";
   public static final String INVOICE_TO_FREEZE = "_itf";
   public static final String INVOICE_TO_SAVE = "_its";
   public static final String INVOICE_TO_DELETE = "_itd";
   public static final String INVOICE_TO_SETPAYED = "_isp";
   public static final String INVOICE_TYPE_STD = "_itst";
   public static final String INVOICE_TYPE_CUSTOM = "_itcu";
   public static final String INVOICE_TYPE_TELEMARK = "_itte";
   public static final String INVOICE_NO_CALLS = "_itnc";
   public static final String INVOICE_TYPE_WEEK = "_itwe";

   // records
   public static final String RECORD_ID = "_rid";
   public static final String RECORD_NUMBER = "_rnb";
   public static final String RECORD_CALLER_NAME = "_cna";
   public static final String RECORD_TEMP_CALLER = "_tca";
   public static final String RECORD_SHORT_TEXT = "_stx";
   public static final String RECORD_LONG_TEXT = "_ltx";
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
   public static final String RECORDS_TO_HANDLE = "_rtd";
   public static final String RECORD_UPDATED = "_rud";
   public static final String RECORD_NEW_KEY = "_nrk";
   public static final String RECORD_NOTIFY = "_nno";
   public static final String RECORD_URGENT = "_rur";

   // Login
   public static final String LOGIN_ID = "_lid";
   public static final String LOGIN_NAME = "_lna";
   public static final String LOGIN_PASSWORD = "_pwd";
   public static final String LOGIN_PASSWORD2 = "_pwd2";
   public static final String LOGIN_USERID = "_uid";
   public static final String LOGIN_ROLE = "_role";
   public static final String LOGIN_REGCODE = "_rgcd";

   // filter
   public static final String ACCOUNT_FILTER_CUSTOMER = "_fcus";
   public static final String ACCOUNT_FILTER_CALL_STATE = "_fcst";
   public static final String ACCOUNT_FILTER_CALL_DIR = "_fdir";

   // attribute names
   public static final String SESSION_OBJ = "_sobj";
   public static final String SESSION_ID = "_sid";
   public static final String ERROR_TXT = "_errt";
   public static final String NEXT_PAGE = "_npg";
   public static final String PREVIOUS_PAGE = "_ppg";
   public static final String PENDING_CALL_ID = "_pci";
   public static final String TBA_HOME = "home";

   // AdminDispatchServlet actions
   public static final String GOTO_CANVAS = "_a12";
   public static final String GOTO_ADD_INVOICE = "_a13";
   // public static final String GOTO_RECORD_ADMIN = "_a14";
   public static final String RECORD_DELETE = "_a15";
   public static final String ACTION_GOTO_RECORD_UPDATE = "_a16";
   public static final String AUTO_RECORD_UPDATE = "_a17";
   public static final String GOTO_ADD_RECORD = "_a18";
   public static final String RECORD_SHOW_PREV = "_a19";
   public static final String RECORD_SHOW_NEXT = "_a20";
   public static final String RECORD_SHOW_PREV_10 = "_a21";
   public static final String RECORD_SHOW_NEXT_10 = "_a22";
   public static final String UPDATE_SHORT_TEXT = "_a23";
   public static final String SEARCH_SHOW_PREV = "_a24";
   public static final String SEARCH_SHOW_NEXT = "_a25";
   public static final String SAVE_RECORD = "_a26";
   public static final String GOTO_ACCOUNT_ADD = "_a27";
   public static final String GOTO_EMPLOYEE_ADD = "_a28";
   public static final String ACCOUNT_DELETE = "_a29";
   public static final String ACCOUNT_UPDATE = "_a30";
   public static final String GENERATE_ACCOUNT_XML = "_a31";
   public static final String ACCOUNT_ADD = "_a32";
   public static final String GOTO_ACCOUNT_ADMIN = "_a33";
   public static final String GOTO_ACCOUNT_DELETE = "_a34";
   public static final String GOTO_SAVE_ACCOUNT = "_a35";
   // public static final String SAVE_ACCOUNT = "_a36";
   public static final String ADMIN_HOME = "_a37";
   public static final String ADMIN_LOG_OFF = "_a38";
   public static final String ACCOUNT_DEREG = "_a39";
   public static final String SAVE_MAN_RECORD = "_a40";
   public static final String GOTO_UPDATE_PREFS = "_a41";
   public static final String SAVE_PREFS = "_a42";
   public static final String GOTO_INVOICE = "_a43";
   public static final String GENERATE_INVOICE = "_a44";
   public static final String SAVE_INVOICE = "_a45";
   public static final String SAVE_PAYDATE = "_a46";
   public static final String GENERATE_ALL_INVOICES = "_a47";
   public static final String GENERATE_ALL_INVOICEDOCS = "_a48";
   public static final String GENERATE_CREDITNOTE = "_a49";
   public static final String GENERATE_INVOICE_XML = "_a50";
   public static final String GOTO_RECORD_SEARCH = "_a51";
   public static final String TASK_DELETE = "_a52";
   public static final String TASK_UPDATE = "_a53";
   public static final String TASK_ADD = "_a54";
   public static final String TASK_SHOW_PREV = "_a55";
   public static final String TASK_SHOW_NEXT = "_a56";
   public static final String EMPLCOST_SHOW_PREV = "_a57";
   public static final String EMPLCOST_SHOW_NEXT = "_a58";
   public static final String GOTO_TASK_ADMIN = "_a59";
   public static final String GOTO_TASK_ADD = "_a60";
   public static final String SAVE_TASK = "_a61";
   public static final String MAIL_CUSTOMER = "_a62";
   // public static final String REFRESH_OPEN_CALLS = "_a63";
   // public static final String NEW_CALL = "_a64";
   // public static final String SAVE_NEW_CALL = "_a65";
   public static final String SAVE_NEW_SUBCUSTOMER = "_a66";
   public static final String REMOVE_OPEN_CALL = "_a67";
   public static final String GOTO_INVOICE_ADMIN = "_a68";
   public static final String GOTO_OPEN_INVOICE = "_a69";
   public static final String PROCESS_FINTRO_XLSX = "_a70";
   public static final String DOWNLOAD_FINTRO_PROCESS_TXT = "_a71";
   public static final String DOWNLOAD_WK_VERKOPEN_XML = "_a72";
   public static final String DOWNLOAD_WK_KLANTEN_XML = "_a73";
   public static final String DOWNLOAD_FACTUUR = "_a74";
   public static final String GOTO_INVOICE_ADD = "_a75";
   public static final String INVOICE_ADD = "_a76";
   public static final String GOTO_EMPLOYEE_ADMIN = "_a77";
   public static final String GOTO_EMPLOYEE_COST = "_a78";
   public static final String INVOICE_FREEZE = "_a79";
   public static final String INVOICE_MAIL = "_a80";
   public static final String INVOICE_DELETE = "_a81";
   public static final String INVOICE_SETPAYED = "_a82";
   public static final String INVOICE_DETAIL = "_a83";
   public static final String MAIL_IT = "_a84";
   public static final String REMOVE_PENDING_CALL = "_a85";
   public static final String SHOW_MAIL_ERROR = "_a86";
   public static final String FIX_ACCOUNT_IDS = "_a87";
   public static final String GOTO_ADMIN_WORKORDERS = "_a88";
   public static final String GOTO_WORKORDER = "_a90";
   public static final String SAVE_WORKORDER = "_a91";
   public static final String DOWNLOAD_WORKORDER_FILE = "_a92";
   public static final String DOWNLOAD_WORKORDER_OUTPUTFILE = "_a93";
   public static final String UPLOAD_WORKORDER_FILE = "_a94";
   public static final String DELETE_WORKORDER_FILE = "_a95";
   public static final String GOTO_ARCHIVED_ACCOUNTS = "_a96";
   public static final String DELETE_LOGIN = "_a97";
   public static final String DELETE_LOGIN_CONFIRMED = "_a98";
   public static final String GOTO_EMPLOYEE_DELETE = "_a99";
   public static final String DELETE_EMPLOYEE_CONFIRMED = "_100";
   public static final String EMPLOYEE_ADD = "_101";

   // LoginServlet actions
   public static final String ACTION_LOGIN = "_l88";
   public static final String ACTION_REGISTER = "_l89";
   public static final String ACTION_FIRST_REGISTER = "_l90";
   public static final String ACTION_DICTAAT_FORM = "_l91";
   public static final String ACTION_TELEMARKETING_FORM = "_l92";

   // CustomerDispatchServlet
   public static final String ACTION_LOGOFF = "_calo";
   public static final String ACTION_SHOW_CALLS = "_cash";
   public static final String ACTION_REFRESH_CALLS = "_carc";
   public static final String ACTION_GOTO_SEARCH_PAGE = "_cags";
   public static final String ACTION_SEARCH_CALLS = "_casc";
   public static final String ACTION_GOTO_SHOW_TASKS = "_cast";
   public static final String ACTION_ARCHIVE_RECORDS = "_caar";
   public static final String ACTION_ARCHIVED_CALLS = "_caac";
   public static final String ACTION_GOTO_WORKORDERS = "_cagw";
   public static final String ACTION_UPDATE_WORKORDER = "_cauw";
   public static final String ACTION_SAVE_WORKORDER = "_casw";
   public static final String ACTION_DELETE_WORKORDER = "_cadw";

   // attribute values
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
   public static final String RECORD_ARCHIVED = "_rar";
   public static final String ACCOUNT_FILTER_ALL = "_afa";
   public static final int ACCOUNT_NOFILTER = 0;
   public static final String ACCOUNT_FILTER_UNFINISHED = "_unf";
   public static final String ACCOUNT_FILTER_FINISHED = "_fin";
   public static final String ACCOUNT_FILTER_IN = "_f_in";
   public static final String ACCOUNT_FILTER_OUT = "_f_out";

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

   public static final String[][] COUNTRY_CODES = { { "BE", "NL", "FR", "DE", "LU" }, { "Belgie", "Nederland", "Frankrijk", "Duitsland", "Luxenburg" } };

   // Forward number block (must be replaced by a XML file)
   // forward number assigned to customer, internal number, nacht number

   public static final String[] NUMBER_BLOCK = {
         // INtertel number blocks
           "409000", "409001", "409002", "409003", "409004", "409005", "409006", "409007", "409008", "409009", 
           "409010", "409011", "409012", "409013", "409014", "409015", "409016", "409017", "409018", "409019",  
           "473050", "473051", "473052", "473053", "473054", "473055", "473056", "473057", "473058", "473059",  
           "402100", "402101", "402102", "402103", "402104", "402105", "402106", "402107", "402108", "402109",  
           "408290", "408291", "408292", "408293", "408294", "408295", "408296", "408297", "408298", "408299",  
           "401800", "401801", "401802", "401803", "401804", "401805", "401806", "401807", "401808", "401809",  
           "445050", "445051", "445052", "445053", "445054", "445055", "445056", "445057", "445058", "445059",  
           "490670", "490671", "490672", "490673", "490674", "490675", "490676", "490677", "490678", "490679", 
           "490398",  // Intertel test klant
           //no call tags
           "noCall_1",
           "noCall_2",
           "noCall_3",
           "noCall_4",
           "noCall_5",
           "noCall_6",
           "noCall_7",
           "noCall_8",
           "noCall_9",
           "noCall_10",
           "noCall_11",
           "noCall_12",
           "noCall_13",
           "noCall_14",
           "noCall_15",
           "noCall_16",
           "noCall_17",
           "noCall_18",
           "noCall_19",
           "noCall_20",
           "noCall_21",
           "noCall_22",
           "noCall_23",
           "noCall_24",
           "noCall_25",
           "noCall_26",
           "noCall_27",
           "noCall_28",
           "noCall_29",
           "noCall_30",
           "noCall_31",
           "noCall_32",
           "noCall_33",
           "noCall_34",
           "noCall_35",
           "noCall_36",
           "noCall_37",
           "noCall_38",
           "noCall_39",
           "noCall_40",
           "noCall_41",
           "noCall_42",
           "noCall_43",
           "noCall_44",
           "noCall_45",
           "noCall_46",
           "noCall_47",
           "noCall_48",
           "noCall_49",
           "noCall_50",
           "noCall_51",
           "noCall_52",
           "noCall_53",
           // sub customers
           "subKlant_1",
           "subKlant_2",
           "subKlant_3",
           "subKlant_4",
           "subKlant_5",
           "subKlant_6",
           "subKlant_7",
           "subKlant_8",
           "subKlant_9",
           "subKlant_10",
           "subKlant_11",
           "subKlant_12",
           "subKlant_13",
           "subKlant_14",
           "subKlant_15",
           "subKlant_16",
           "subKlant_17",
           "subKlant_18",
           "subKlant_19",
           "subKlant_20",
           "subKlant_21",
           "subKlant_22",
           "subKlant_23",
           "subKlant_24",
           "subKlant_25",
           "subKlant_26",
           "subKlant_27",
           "subKlant_28",
           "subKlant_29",
           "subKlant_30",
           "subKlant_31",
           "subKlant_32",
           "subKlant_33",
           "subKlant_34",
           "subKlant_35",
           "subKlant_36",
           "subKlant_37",
           "subKlant_38",
           "subKlant_39",
           "subKlant_40",
           "subKlant_41",
           "subKlant_42",
           "subKlant_43",
           "subKlant_44",
           "subKlant_45",
           "subKlant_46",
           "subKlant_47",
           "subKlant_48",
           "subKlant_49",
           "subKlant_50",
           "subKlant_51",
           "subKlant_52",
           "subKlant_53",
           "subKlant_54",
           "subKlant_55",
           "subKlant_56",
           "subKlant_57",
           "subKlant_58",
           "subKlant_59",
           "subKlant_60",
           "subKlant_61",
           "subKlant_62",
           "subKlant_63",
           "subKlant_64",
           "subKlant_65",
           "subKlant_66",
           "subKlant_67",
           "subKlant_68",
           "subKlant_69",
           "subKlant_70",
           "subKlant_71",
           "subKlant_72",
           "subKlant_73",
           "subKlant_74",
           "subKlant_75",
           "subKlant_76",
           "subKlant_77",
           "subKlant_78",
           "subKlant_79",
           "subKlant_80",
           "subKlant_81",
           "subKlant_82",
           "subKlant_83",
           "subKlant_84",
           "subKlant_85",
           "subKlant_86",
           "subKlant_87",
           "subKlant_88",
           "subKlant_89",
           "subKlant_90",
           "subKlant_91",
           "subKlant_92",
           "subKlant_93",
           "subKlant_94",
           "subKlant_95",
           "subKlant_96",
           "subKlant_97",
           "subKlant_98",
           "subKlant_99",
           "subKlant_100",
           "subKlant_101",
           "subKlant_102",
           "subKlant_103",
           "subKlant_104",
           "subKlant_105",
           "subKlant_106",
           "subKlant_107",
           "subKlant_108",
           "subKlant_109",
           "subKlant_110",
           "subKlant_111",
           "subKlant_112",
           "subKlant_113",
           "subKlant_114",
           "subKlant_115",
           "subKlant_116",
           "subKlant_117",
           "subKlant_118",
           "subKlant_119",
           "subKlant_120",
           "subKlant_121",
           "subKlant_122",
           "subKlant_123",
           "subKlant_124",
           "subKlant_125",
           "subKlant_126",
           "subKlant_127",
           "subKlant_128",
           "subKlant_129",
           "subKlant_130",
           "subKlant_131",
           "subKlant_132",
           "subKlant_133",
           "subKlant_134",
           "subKlant_135",
           "subKlant_136",
           "subKlant_137",
           "subKlant_138",
           "subKlant_139",
           "subKlant_140",
           "subKlant_141",
           "subKlant_142",
           "subKlant_143",
           "subKlant_144",
           "subKlant_145",
           "subKlant_146",
           "subKlant_147",
           "subKlant_148",
           "subKlant_149",
           "subKlant_150",
           "subKlant_151",
           "subKlant_152",
           "subKlant_153",
           "subKlant_154",
           "subKlant_155",
           "subKlant_156",
           "subKlant_157",
           "subKlant_158",
           "subKlant_159",
           "subKlant_160",
           "subKlant_161",
           "subKlant_162",
           "subKlant_163",
           "subKlant_164",
           "subKlant_165",
           "subKlant_166",
           "subKlant_167",
           "subKlant_168",
           "subKlant_169",
           "subKlant_170",
           "subKlant_171",
           "subKlant_172",
           "subKlant_173",
           "subKlant_174",
           "subKlant_175",
           "subKlant_176",
           "subKlant_177",
           "subKlant_178",
           "subKlant_179",
           "subKlant_180",
           "subKlant_181",
           "subKlant_182",
           "subKlant_183",
           "subKlant_184",
           "subKlant_185",
           "subKlant_186",
           "subKlant_187",
           "subKlant_188",
           "subKlant_189",
           // prospection customers (only select these for customers that only have outgoing calls
           "409030", "409031", "409032", "409033", "409034", "409035", "409036", "409037", "409038", "409039", 
           "409040", "409041", "409042", "409043", "409044", "409045", "409046", "409047", "409048", "409049"
         };


   public static final String[] MONTHS = { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december" };
   public static final int RECORD_PAGE_LEN = 30;
   // CallLogger attributes
   public static final String CALLLOG_SCOPE = "day";
   public static final String CALLLOG_DIR = "upd";

   // calllogger file paths
   public static final String CALLLOG_PATH = "C:\\jetty\\projects\\TBA\\calllogs";
   public static final String INTERTELL_CALLLOG_PATH = "C:\\jetty\\projects\\TBA\\Intertel";
   public static final String RECORDS_OF_TODAY_PATH = "C:\\jetty\\projects\\TBA\\calllogs\\";
   public static final long SECONDS = 1000;
   public static final long MINUTES = SECONDS * 60;
   public static final long HOURS = MINUTES * 60;
   public static final long DAYS = HOURS * 24;
   public static final long RECORD_DELETE_EXPIRE = DAYS * 500; // 500 days
   public static final long LOGIN_DELETE_EXPIRE = DAYS * 45; // 45 days
   public static final long RECORD_AUTO_RELEASE_EXPIRE = DAYS * 3; // 10 days
   public static final long CUSTOMER_SESSION_TIMEOUT = MINUTES * 120; // 5 minuten
   public static final long ADMIN_SESSION_TIMEOUT = MINUTES * 60; // 50 minuten
   public static final long NORMAL_CALL_LENGTH = 90; // 90 seconds
   public static final int kOutCost = 120; // euro cent
   public static final int kSmsCost = 100; // euro cent
   public static final int kForwardCost = 100; // euro cent
   public static final int kFaxCost = 100; // euro cent
   public static final int kStandardAgendaCost = 45; // euro
   public static final int kHourTarifCost = 4500; // euro cent
   public static final double kFacLong = 0.014;
   public static final double kFacLongFwd = 0.014;
   public static final int kFacTblMinCalls_I = 75;
   public static final double kFacTblStartCost_I = 110.0;
   public static final double kFacTblExtraCost_I = 1.2;
   public static final int kFacTblMinCalls_II = 40;
   public static final double kFacTblStartCost_II = 80.0;
   public static final double kFacTblExtraCost_II = 1.2;
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
   public static final int kAbbrevWidth = 50;

   public static final String kNullCost = "00:00:00";

   public static final String kRed = "FF9797";
   public static final String kOrange = "FFCC66";
   public static final String kGreen = "97FF97";
   public static final String kGrey = "AEAEAE";

   private Constants()
   {
   }
}
