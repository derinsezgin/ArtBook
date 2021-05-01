package com.derinsezgin.artbook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.derinsezgin.artbook.R;
import com.derinsezgin.artbook.ui.adaper.HowtoPagerAdapter;
import com.sembozdemir.viewpagerarrowindicator.library.ViewPagerArrowIndicator;

public class AboutFragment extends Fragment {

    private ViewPager viewPager;
    private ViewPagerArrowIndicator viewPagerArrowIndicator ;

    String images[] = {
            "https://i.hizliresim.com/jUmlPV.png",
            "https://i.hizliresim.com/PZvsai.png",
            "https://i.hizliresim.com/GgWZgK.png",
            "https://i.hizliresim.com/5YZGPl.png"
    };

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewPager);
        viewPagerArrowIndicator = view.findViewById(R.id.viewPagerArrowIndicator);

        HowtoPagerAdapter adapter = new HowtoPagerAdapter(getActivity(), images);
        viewPager.setAdapter(adapter);
        viewPagerArrowIndicator.bind(viewPager);
    }
}