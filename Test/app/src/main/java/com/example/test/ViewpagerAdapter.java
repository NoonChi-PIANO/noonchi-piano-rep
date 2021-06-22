package com.example.test;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


public class ViewpagerAdapter extends FragmentStateAdapter {

    private static int PAGE_NUMBER=4; //생성할 프래그먼트 수

    public ViewpagerAdapter (FragmentActivity fa){
        super(fa);
    }
    //꼭 있어야함

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return Fpractice.newInstance();
            case 1:
                return Fscore.newInstance();
            case 2:
                return Fmusic.newInstance();
            case 3:
                return Fquestion.newInstance();

        }
        return null;    }

    @Override
    public int getItemCount() {
        return PAGE_NUMBER;
    }
}

