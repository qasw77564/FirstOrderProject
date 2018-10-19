package com.melonltd.naber.view.factory;

import android.support.v4.app.Fragment;

import com.melonltd.naber.R;
import com.melonltd.naber.view.common.page.LoginFragment;
import com.melonltd.naber.view.common.page.RecoverPasswordFragment;
import com.melonltd.naber.view.common.page.RegisteredFragment;
import com.melonltd.naber.view.common.page.RegisteredSellerFragment;
import com.melonltd.naber.view.common.page.VerifySMSFragment;
import com.melonltd.naber.view.seller.page.SellerCategoryListFragment;
import com.melonltd.naber.view.seller.page.SellerDetailFragment;
import com.melonltd.naber.view.seller.page.SellerFoodEditFragment;
import com.melonltd.naber.view.seller.page.SellerFoodListFragment;
import com.melonltd.naber.view.seller.page.SellerOrderLogsDetailFragment;
import com.melonltd.naber.view.seller.page.SellerOrdersFragment;
import com.melonltd.naber.view.seller.page.SellerOrdersLogsFragment;
import com.melonltd.naber.view.seller.page.SellerResetPasswordFragment;
import com.melonltd.naber.view.seller.page.SellerSearchFragment;
import com.melonltd.naber.view.seller.page.SellerSetUpFragment;
import com.melonltd.naber.view.seller.page.SellerSimpleInformationFragment;
import com.melonltd.naber.view.seller.page.SellerStatFragment;
import com.melonltd.naber.view.user.page.UserAccountDetailFragment;
import com.melonltd.naber.view.user.page.UserBonusExchangeDetailFragment;
import com.melonltd.naber.view.user.page.UserBonusExchangeFragment;
import com.melonltd.naber.view.user.page.UserExchangeSubmitOrdersFragment;
import com.melonltd.naber.view.user.page.UserFoodDetailFragment;
import com.melonltd.naber.view.user.page.UserFoodListFragment;
import com.melonltd.naber.view.user.page.UserHomeFragment;
import com.melonltd.naber.view.user.page.UserOrderDetailFragment;
import com.melonltd.naber.view.user.page.UserOrderHistoryFragment;
import com.melonltd.naber.view.user.page.UserResetPasswordFragment;
import com.melonltd.naber.view.user.page.UserRestaurantDetailFragment;
import com.melonltd.naber.view.user.page.UserRestaurantListFragment;
import com.melonltd.naber.view.user.page.UserSetUpFragment;
import com.melonltd.naber.view.user.page.UserShoppingCartFragment;
import com.melonltd.naber.view.user.page.UserSimpleInformationFragment;
import com.melonltd.naber.view.user.page.UserSubmitOrdersFragment;

public enum PageType {
    LOGIN(R.string.common_page_login_title, 100, LoginFragment.class),
    RECOVER_PASSWORD(R.string.common_page_recover_password_title, 101, RecoverPasswordFragment.class),
    REGISTERED_USER(R.string.user_page_registered_title, 103, RegisteredFragment.class),
    VERIFY_SMS(R.string.user_page_verify_sms_title, 105, VerifySMSFragment.class),
    REGISTERED_SELLER(R.string.seller_page_registered_title, 104, RegisteredSellerFragment.class),

    USER_HOME(R.string.menu_home_btn, 0, UserHomeFragment.class),
    USER_RESTAURANT_LIST(R.string.menu_restaurant_btn, 1, UserRestaurantListFragment.class),
    USER_RESTAURANT_DETAIL(R.string.user_page_restaurant_detail_title, 1, UserRestaurantDetailFragment.class),
    USER_FOOD_LIST(R.string.user_page_category_menu_title, 1, UserFoodListFragment.class),
    USER_FOOD_DETAIL(R.string.user_page_menu_detail_title, 1, UserFoodDetailFragment.class),
    USER_SHOPPING_CART(R.string.menu_shopping_cart_btn, 2, UserShoppingCartFragment.class),
    USER_SUBMIT_ORDER(R.string.user_page_submit_order_title, 2, UserSubmitOrdersFragment.class),
    USER_ORDER_HISTORY(R.string.menu_history_btn, 3, UserOrderHistoryFragment.class),
    USER_ORDER_DETAIL(R.string.user_page_order_detail_title, 3, UserOrderDetailFragment.class),
    USER_ACCOUNT(R.string.menu_account_btn, 4, UserSetUpFragment.class),
    USER_ACCOUNT_DETAIL(R.string.user_page_account_detail_title, 4, UserAccountDetailFragment.class),
    USER_SIMPLE_INFO(R.string.user_page_simple_info_title, 4, UserSimpleInformationFragment.class),
    USER_RESET_PASSWORD(R.string.common_page_reset_password_title, 4, UserResetPasswordFragment.class),
    USER_BONUS_EXCHANGE(R.string.user_page_bonus_exchange_title,4, UserBonusExchangeFragment.class),
//    UserBonusExchangeDetailFragment
    USER_BONUS_EXCHANGE_DETAIL(R.string.user_page_bonus_exchange_detail_title,4, UserBonusExchangeDetailFragment.class),
    USER_EXCHANGE_SUBMIT_ORDER(R.string.user_page_submit_order_title, 2, UserExchangeSubmitOrdersFragment.class),


    SELLER_SEARCH(R.string.seller_menu_search_btn, 0, SellerSearchFragment.class),
    SELLER_ORDERS(R.string.seller_menu_orders_btn, 1, SellerOrdersFragment.class),
    SELLER_STAT(R.string.seller_menu_stat_btn, 2, SellerStatFragment.class),
    SELLER_ORDERS_LOGS(R.string.seller_menu_orders_logs, 2, SellerOrdersLogsFragment.class),
    SELLER_ORDERS_LOGS_DETAIL(R.string.seller_menu_orders_logs_detail, 2, SellerOrderLogsDetailFragment.class),
    SELLER_CATEGORY_LIST(R.string.seller_menu_category_list_btn, 3, SellerCategoryListFragment.class),
    SELLER_FOOD_LIST(R.string.seller_menu_food_list, 3, SellerFoodListFragment.class),
    SELLER_FOOD_EDIT(R.string.seller_menu_food_edit, 3, SellerFoodEditFragment.class),
    SELLER_SET_UP(R.string.seller_menu_set_up_btn, 4, SellerSetUpFragment.class),
    SELLER_DETAIL(R.string.seller_menu_seller_detail, 4, SellerDetailFragment.class),
    SELLER_SIMPLE_INFO(R.string.seller_menu_simple_information, 4, SellerSimpleInformationFragment.class),
    SELLER_RESET_PASSWORD(R.string.common_page_reset_password_title,4,SellerResetPasswordFragment.class);


    private final int id;
    private final int position;
    private final Class zlass;

    PageType(int id, int position, Class zlass) {
        this.id = id;

        this.position = position;
        this.zlass = zlass;
    }

    public boolean equals(int id) {
        return this.id == id;
    }

    public static PageType ofId(int id) {
        for (PageType entity : values()) {
            if (entity.equals(id)) {
                return entity;
            }
        }
        return USER_HOME;
    }

    public int getId() {
        return id;
    }

    public static int equalsPositionByName(String name) {
        for (PageType entity : values()) {
            if (entity.name().equals(name)) {
                return entity.position;
            }
        }
        return 100;
    }

    public static int equalsIdByName(String name) {
        for (PageType entity : values()) {
            if (entity.name().equals(name)) {
                return entity.id;
            }
        }
        return 0;
    }

    public Class toClass() {
        return this.zlass.asSubclass(Fragment.class);
    }
}
