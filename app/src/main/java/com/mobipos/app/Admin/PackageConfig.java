package com.mobipos.app.Admin;

/**
 * Created by folio on 12/28/2017.
 */

public class PackageConfig {
    public static String get_admin_categories="admin-get-branch-data.php";
    public static String add_category="admin-add-category.php";
    public static String get_admin_items="admin-get-products.php";
    public static String get_low_stock="admin-get-stock-alert.php";
    public static String get_item_parameters="admin-get-item-parameters.php";
    public static String update_product_price="admin-update-price.php";

    public static String[] branches;
    public static String[] branchesId;

    public static String catname;
    public static String selectedBranchId;

    public static String[] catnames;

    public static boolean isItemUpdated=false;
}
