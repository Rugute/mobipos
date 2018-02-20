package com.mobipos.app.Defaults;

/**
 * Created by folio on 12/15/2017.
 */

public class AppConfig {

   // public static String protocol="http://mauzoafrica.mutengeneresort.com";
    public static String hostname ="/app/";
    public static String hostname_admin="/admin/custom/data/apis/log/";
    public static String protocol="http://192.168.173.1/mobipos.com";

    public static String url_register="register.php";
    public static String get_today_sales="admin-get-todays-sales.php";
    public static String admin_select_branches="admin-get-branches.php";
    public static String get_items="admin-get-products-to-sale.php";
    public static String get_categories="admin-get-categories.php";
    public static String add_outlet="admin-add-outlet.php";
    public static String add_product="admin-add-product.php";
    public static String add_taxes="admin-add-tax.php";
    public static String get_taxes="admin-get-taxes.php";
    public static String admin_add_measurements="admin-add-measurement.php";
    public static String admin_get_measurements="admin-get-measurements.php";
    public static String admin_get_employees="admin-get-employees.php";
    public static String admin_add_employees="admin-add-employee.php";
    public static String admin_get_reports="/admin/custom/data/csvExporter/data-exporter.php?";





    public static boolean firstRefresh=false;

    public static String selected_branch_id;

    public static String[] branchNames;
    public static String[] branchIds;
    public static String[] measureMents;
    public static String[] measureValue;





}
