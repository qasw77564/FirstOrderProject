package com.melonltd.naber.view.factory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

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

public class PageFragmentFactory {
    private final static String TAG = PageFragmentFactory.class.getSimpleName();

    public static Fragment of(@NonNull PageType pageType, Bundle bundle) {

        switch (pageType) {
            case LOGIN:
                return new LoginFragment().getInstance(bundle);
            case REGISTERED_USER:
                return new RegisteredFragment().getInstance(bundle);
            case REGISTERED_SELLER:
                return new RegisteredSellerFragment().getInstance(bundle);
            case RECOVER_PASSWORD:
                return new RecoverPasswordFragment().getInstance(bundle);
            case VERIFY_SMS:
                return new VerifySMSFragment().getInstance(bundle);

            case USER_HOME:
                return new UserHomeFragment().getInstance(bundle);
            case USER_ACCOUNT:
                return new UserSetUpFragment().getInstance(bundle);
            case USER_ACCOUNT_DETAIL:
                return new UserAccountDetailFragment().getInstance(bundle);
            case USER_ORDER_HISTORY:
                return new UserOrderHistoryFragment().getInstance(bundle);
            case USER_ORDER_DETAIL:
                return new UserOrderDetailFragment().getInstance(bundle);
            case USER_RESTAURANT_LIST:
                return new UserRestaurantListFragment().getInstance(bundle);
            case USER_RESTAURANT_DETAIL:
                return new UserRestaurantDetailFragment().getInstance(bundle);
            case USER_FOOD_LIST:
                return new UserFoodListFragment().getInstance(bundle);
            case USER_SHOPPING_CART:
                return new UserShoppingCartFragment().getInstance(bundle);
            case USER_SUBMIT_ORDER:
                return new UserSubmitOrdersFragment().getInstance(bundle);
            case USER_SIMPLE_INFO:
                return new UserSimpleInformationFragment().getInstance(bundle);
            case USER_RESET_PASSWORD:
                return new UserResetPasswordFragment().getInstance(bundle);
            case USER_FOOD_DETAIL:
                return new UserFoodDetailFragment().getInstance(bundle);
            case USER_BONUS_EXCHANGE:
                return new UserBonusExchangeFragment().getInstance(bundle);
            case USER_BONUS_EXCHANGE_DETAIL:
                return new UserBonusExchangeDetailFragment().getInstance(bundle);
            case USER_EXCHANGE_SUBMIT_ORDER:
                return new UserExchangeSubmitOrdersFragment().getInstance(bundle);

            // seller
            case SELLER_SEARCH:
                return new SellerSearchFragment().getInstance(bundle);
            case SELLER_ORDERS:
                return new SellerOrdersFragment().getInstance(bundle);
            case SELLER_STAT:
                return new SellerStatFragment().getInstance(bundle);
            case SELLER_ORDERS_LOGS:
                return new SellerOrdersLogsFragment().getInstance(bundle);
            case SELLER_ORDERS_LOGS_DETAIL:
                return new SellerOrderLogsDetailFragment().getInstance(bundle);
            case SELLER_CATEGORY_LIST:
                return new SellerCategoryListFragment().getInstance(bundle);
            case SELLER_FOOD_LIST:
                return new SellerFoodListFragment().getInstance(bundle);
            case SELLER_FOOD_EDIT:
                return new SellerFoodEditFragment().getInstance(bundle);
            case SELLER_SET_UP:
                return new SellerSetUpFragment().getInstance(bundle);
            case SELLER_SIMPLE_INFO:
                return new SellerSimpleInformationFragment().getInstance(bundle);
            case SELLER_DETAIL:
                return new SellerDetailFragment().getInstance(bundle);
            case SELLER_RESET_PASSWORD:
                return new SellerResetPasswordFragment().getInstance(bundle);
            default:
                return new UserHomeFragment().getInstance(bundle);
        }

    }
}
