package com.mobipos.app.Defaults;

/**
 * Created by folio on 12/15/2017.
 */

public class AppConfig {

  //  public static String protocol="http://mauzoafrica.mutengeneresort.com";
    public static String hostname ="/app/";
    public static String hostname_admin="/admin/custom/data/apis/log/";
    public static String protocol="http://192.168.173.1/mobipos.com";

    public static String url_register="register.php";
    public static String get_today_sales="admin-get-todays-sales.php";
    public static String admin_select_branches="admin-get-branches.php";
    public static String get_items="admin-get-products-to-sale.php";
    public static String get_categories="admin-get-categories.php";

    public static boolean firstRefresh=false;

    public static String selected_branch_id;

    public static String[] branchNames;
    public static String[] branchIds;


}
