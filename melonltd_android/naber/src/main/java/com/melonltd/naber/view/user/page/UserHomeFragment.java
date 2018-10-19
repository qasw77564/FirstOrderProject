package com.melonltd.naber.view.user.page;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.util.DistanceTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.common.BaseCore;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.view.user.adapter.UserRestaurantAdapter;
import com.melonltd.naber.vo.AdvertisementVo;
import com.melonltd.naber.vo.BulletinVo;
import com.melonltd.naber.vo.LocationVo;
import com.melonltd.naber.vo.ReqData;
import com.melonltd.naber.vo.RestaurantInfoVo;
import com.sunfusheng.marqueeview.MarqueeView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

import static com.melonltd.naber.view.common.BaseCore.LOCATION_CODE;
import static com.melonltd.naber.view.common.BaseCore.LOCATION_MG;


public class UserHomeFragment extends Fragment {
    private static final String TAG = UserHomeFragment.class.getSimpleName();
    public static UserHomeFragment FRAGMENT = null;
    private UserRestaurantAdapter adapter;
    private List<RestaurantInfoVo> restaurantList = Lists.newArrayList();
    private List<AdvertisementVo> advertisementVos = Lists.newArrayList();


    private Location location;

    public UserHomeFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserHomeFragment();
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getContext());
        adapter = new UserRestaurantAdapter(restaurantList, new ItemOnClickListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container.getTag(R.id.user_home_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_home, container, false);
            getViews(v);
            container.setTag(R.id.user_home_page, v);
            return v;
        }
        return (View) container.getTag(R.id.user_home_page);
    }

    private void getViews(View v) {
        final BGABanner banner = v.findViewById(R.id.homeBanner);
        final MarqueeView bulletinText = v.findViewById(R.id.bulletinText);

        final BGARefreshLayout bgaRefreshLayout = v.findViewById(R.id.top30BGARefreshLayout);
        RecyclerView recyclerView = v.findViewById(R.id.top30RecyclerView);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("Loading more !");

        bgaRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //setListener
        banner.setAdapter(new BGABanner.Adapter<CardView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, CardView itemView, String model, int position) {
                SimpleDraweeView simpleDraweeView = itemView.findViewById(R.id.sdv_item_fresco_content);
                if (!Strings.isNullOrEmpty(model)) {
                    simpleDraweeView.setImageURI(Uri.parse(model));
                } else {
                    ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_default_image).build();
                    simpleDraweeView.setImageURI(imageRequest.getSourceUri());
                }
            }
        });

        // 輪播圖 點擊事件
        banner.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, @Nullable Object model, int position) {
                if (advertisementVos.get(position).link_type != null && !Strings.isNullOrEmpty(advertisementVos.get(position).link_to)) {
                    switch (advertisementVos.get(position).link_type) {
                        case APP:
                            Map<String, String> toData = Tools.JSONPARSE.fromJson(advertisementVos.get(position).link_to, HashMap.class);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(toData.get("android")));
                            startActivity(intent);
                            break;
                        case WEB:
                            Uri uriUrl = Uri.parse(advertisementVos.get(position).link_to);
                            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                            startActivity(launchBrowser);
                            break;
                        case INSIDE:
                            ReqData req = new ReqData();
                            req.search_type = "DISTANCE";
                            req.uuids = Lists.newArrayList(advertisementVos.get(position).link_to);
                            ApiManager.restaurantList(req, new ThreadCallback(getContext()) {
                                @Override
                                public void onSuccess(String responseBody) {
                                    List<RestaurantInfoVo> list = Tools.JSONPARSE.fromJsonList(responseBody, RestaurantInfoVo[].class);
                                    if (!list.isEmpty()) {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(NaberConstant.RESTAURANT_INFO, list.get(0));
                                        UserRestaurantListFragment.TO_RESTAURANT_DETAIL_INDEX = 0;
                                        UserRestaurantDetailFragment.TO_CATEGORY_MENU_INDEX = -1;
                                        UserFoodListFragment.TO_MENU_DETAIL_INDEX = -1;
                                        UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_RESTAURANT_DETAIL, bundle);
                                    }
                                }

                                @Override
                                public void onFail(Exception error, String msg) {

                                }
                            });
                            break;
                    }
                }
            }
        });

        // 輪播圖
        ApiManager.advertisement(new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                advertisementVos.clear();
                advertisementVos = Tools.JSONPARSE.fromJsonList(responseBody, AdvertisementVo[].class);
                Model.BANNER_IMAGES = Lists.<String>newArrayList();
                for (int i = 0; i < advertisementVos.size(); i++) {
                    Model.BANNER_IMAGES.add(advertisementVos.get(i).photo);
                }
                banner.setData(R.layout.item_fresco, Model.BANNER_IMAGES, null);
            }

            @Override
            public void onFail(Exception error, String msg) {

            }
        });

        // 取得全部公告
        ApiManager.bulletin(new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                List<BulletinVo> list = Tools.JSONPARSE.fromJsonList(responseBody, BulletinVo[].class);
                List<String> homeBulletins = Lists.newArrayList();
                for (int i = 0; i < list.size(); i++) {
                    Iterator<String> iterator = Splitter.on("$split").split(list.get(i).content_text).iterator();
                    if ("HOME".equals(list.get(i).bulletin_category)) {
                        homeBulletins = Lists.newArrayList(iterator);
                    }
                    String content_text = "";
                    while (iterator.hasNext()) {
                        content_text += iterator.next() + "\n";
                    }
                    Map<String, String> m = Maps.newHashMap();
                    m.put("title", list.get(i).title);
                    m.put("content_text", content_text);
                    Model.BULLETIN_VOS.put(list.get(i).bulletin_category, m);
                }
                bulletinText.startWithList(homeBulletins, R.anim.anim_bottom_in, R.anim.anim_top_out);
            }

            @Override
            public void onFail(Exception error, String msg) {
                bulletinText.startWithList(Lists.<CharSequence>newArrayList(), R.anim.anim_bottom_in, R.anim.anim_top_out);
            }
        });

        recyclerView.setAdapter(adapter);
//        adapter.setItemOnClickListener(new ItemOnClickListener());

        bgaRefreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                getLocation();
                bgaRefreshLayout.endRefreshing();
                doLoadData(true);
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                bgaRefreshLayout.endLoadingMore();
                return false;
            }
        });

        doLoadData(true);
    }

    public void doLoadData(boolean isRefresh) {
        if (isRefresh) {
            UserRestaurantListFragment.TO_RESTAURANT_DETAIL_INDEX = -1;
            restaurantList.clear();
        }

        restaurantList.clear();
        adapter.notifyDataSetChanged();
        ReqData req = new ReqData();
        req.search_type = "TOP";
        ApiManager.restaurantList(req, new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                List<RestaurantInfoVo> list = Tools.JSONPARSE.fromJsonList(responseBody, RestaurantInfoVo[].class);

                for (int i = 0; i < list.size(); i++) {
                    list.get(i).distance = DistanceTools.getDistance(location, LocationVo.of(list.get(i).latitude, list.get(i).longitude));
                }

                restaurantList.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception error, String msg) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (BaseCore.checkLocationPermission(getContext())) {
            ActivityCompat.requestPermissions(getActivity(), BaseCore.LOCATION_PERMISSION, LOCATION_CODE);
        }
//        doLoadData(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserMainActivity.changeTabAndToolbarStatus();
        getLocation();
        adapter.notifyDataSetChanged();
        // 4.4.2 版本出現問題
//        doLoadData(true);
        boolean isFirst = SPService.getIsFirstLogin();
//        if (isFirst) {
//            startActivity(new Intent(getActivity().getBaseContext(), IntroActivity.class));
//        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Model.LOCATION == null) {
                location = LOCATION_MG.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                location = Model.LOCATION;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class ItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int index = (int) view.getTag();
            Bundle bundle = new Bundle();
            bundle.putSerializable(NaberConstant.RESTAURANT_INFO, restaurantList.get(index));
            UserRestaurantListFragment.TO_RESTAURANT_DETAIL_INDEX = index;
            UserRestaurantDetailFragment.TO_CATEGORY_MENU_INDEX = -1;
            UserFoodListFragment.TO_MENU_DETAIL_INDEX = -1;
            UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_RESTAURANT_DETAIL, bundle);
        }
    }
}
