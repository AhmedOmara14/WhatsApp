package com.example.what.fragment_main;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.what.Request_fragment.Request;
import com.example.what.chat_send_message.chatFragment;
import com.example.what.contacts_fragment.contactFragment;

public class tabaccess extends FragmentPagerAdapter {
    public tabaccess(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                chatFragment chatFragment = new chatFragment();
                return chatFragment;

            case 1:
                contactFragment contactFragment = new contactFragment();
                return contactFragment;


            case 2:
                Request RequestFragment = new Request();
                return RequestFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case 0:
                return "chats";
            case 1:
                return "contacts";

            case 2:
                return "Request";
            default:
                return null;
        }
    }
}
