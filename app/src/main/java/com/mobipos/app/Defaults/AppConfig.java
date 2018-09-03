package com.mobipos.app.Defaults;

/**
 * Created by folio on 12/15/2017.
 */

public class AppConfig {

    public static String protocol="https://system.mauzoafrica.com";
    public static String hostname ="/app/";
    public static String hostname_admin="/admin/custom/data/apis/log/";
   // public static String protocol="http://192.168.173.1/mobipos.com";

    public static String url_register="register.php";
    public static String get_today_sales="admin-get-todays-sales.php";
    public static String get_transactions="admin-get-transcations.php";
    public static String get_other_date_sales="admin-get-other-sales-date.php";

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
    public static String admin_add_discounts="admin-add-discounts.php";
    public static String admin_get_discounts="admin-get-discounts.php";
    public static String admin_update_inventory="update-inventory.php";
    public static String add_printer="";
    public static String view_printers="admin-get-printers.php";
    public static String admin_get_reports="/admin/custom/data/csvExporter/data-exporter.php?";
    public static String admin_delete_user="/app/delete-fxn.php";
    public static String admin_add_printer="admin-add-printer.php";
    public static String admin_create_default_sales_branch="admin-create-default-sales-branch.php";
    public static String admin_check_default_sales_branch="admin-check-sales-branch.php";




    public static boolean firstRefresh=false;
    public static String selected_branch_id;
    public static String[] branchNames;
    public static String[] branchIds;
    public static String[] measureMents;
    public static String[] measureValue;
    public static String[] printerName;
    public static String[] printerMac;
    public static String[] discountName;
    public static String[] discountValue;
    public static String[] discountId;

    public static String printMac = "";
    public static String[] formattedData;
    public static String print_branch_name;
    public static String print_biz_name;


    public static String tendered_amount;
    public static String cash_sale;
    public static String taxable_amount;
    public static String discount;
    public static String change;
    public static String grand;
    public static int discount_amnt;


    public static String store_request;
}
