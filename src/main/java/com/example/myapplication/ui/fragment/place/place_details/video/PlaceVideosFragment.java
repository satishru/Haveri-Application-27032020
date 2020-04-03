package com.example.myapplication.ui.fragment.place.place_details.video;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.data.model.api.response.haveri_data.Place;
import com.example.myapplication.data.model.api.response.haveri_data.Videos;
import com.example.myapplication.databinding.FragmentPlaceVideosBinding;
import com.example.myapplication.ui.activity.media.video.VideosExploreActivity;
import com.example.myapplication.ui.base.BaseFragment;
import com.example.myapplication.ui.fragment.common.adapter.VideoListAdapter;
import com.example.myapplication.utils.AppConstants;

import javax.inject.Inject;
import javax.inject.Provider;

public class PlaceVideosFragment extends BaseFragment<FragmentPlaceVideosBinding, PlaceVideosFragmentViewModel> implements
        iPlaceVideosFragmentContract.iPlaceVideosFragmentNavigator,
        VideoListAdapter.VideoListAdapterListener {

    @Inject
    VideoListAdapter videoListAdapter;

    @Inject
    Provider<LinearLayoutManager> layoutManager;

    private FragmentPlaceVideosBinding fragmentPlaceVideosBinding;
    private PlaceVideosFragmentViewModel placeVideosFragmentViewModel;
    private Place selectedPlace;

    public static PlaceVideosFragment newInstance(Place selectedPlace) {
        Bundle args = new Bundle();
        PlaceVideosFragment fragment = new PlaceVideosFragment();
        args.putSerializable(AppConstants.INTENT_SELECTED_PLACE, selectedPlace);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return com.example.myapplication.BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_place_videos;
    }

    @Override
    public PlaceVideosFragmentViewModel getViewModel() {
        placeVideosFragmentViewModel = new ViewModelProvider(this, factory).get(
                PlaceVideosFragmentViewModel.class);
        return placeVideosFragmentViewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentPlaceVideosBinding = getViewDataBinding();
        placeVideosFragmentViewModel.setNavigator(this);
        getBundleData();
        if (selectedPlace != null) {
            placeVideosFragmentViewModel.setVideoData(selectedPlace);
        }
        setVideosAdapter();
    }

    private void getBundleData() {
        if (getArguments() != null && getArguments().containsKey(
                AppConstants.INTENT_SELECTED_PLACE)) {
            selectedPlace = (Place) getArguments().getSerializable(
                    AppConstants.INTENT_SELECTED_PLACE);
        }
    }

    private void setVideosAdapter() {
        videoListAdapter.setListener(this);
        fragmentPlaceVideosBinding.rvVideosList.setLayoutManager(layoutManager.get());
        fragmentPlaceVideosBinding.rvVideosList.setItemAnimator(new DefaultItemAnimator());
        fragmentPlaceVideosBinding.rvVideosList.addItemDecoration(getVerticalDivider());
        fragmentPlaceVideosBinding.rvVideosList.setAdapter(videoListAdapter);
        fragmentPlaceVideosBinding.rvVideosList.setNestedScrollingEnabled(true);
    }

    /**
     * VideoListAdapter.VideoListAdapterListener
     *
     * @param selectedVideo Video
     */
    @Override
    public void onVideoClick(Videos selectedVideo) {
        if (getBaseActivity() != null && isDistrictNotNull()) {
            startActivityWithAnimation(
                    VideosExploreActivity.newIntent(getBaseActivity(), selectedVideo));
        }
    }
}
