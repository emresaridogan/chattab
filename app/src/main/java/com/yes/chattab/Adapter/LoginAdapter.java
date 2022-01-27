package com.yes.chattab.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yes.chattab.LoginFragment;
import com.yes.chattab.RegisterFragment;

public class LoginAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;

    public LoginAdapter(FragmentManager fm, Context context, int totalTabs){
        super(fm);
        this.context=context;
        this.totalTabs=totalTabs;
    }
    public Fragment getItem(int position){
        if (position==0) {
            LoginFragment loginFragment = new LoginFragment();
            return loginFragment;
        }
            else{
                RegisterFragment registerFragment = new RegisterFragment();
                return registerFragment;
            }

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
