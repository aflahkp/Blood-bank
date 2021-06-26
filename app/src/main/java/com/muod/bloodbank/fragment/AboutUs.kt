package com.muod.bloodbank.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.muod.bloodbank.R


class AboutUs : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false)
    }

    companion object {

        var aboutUs:AboutUs ? = null
        fun newInstance():AboutUs{
            if(aboutUs == null) {
                aboutUs = AboutUs()
            }
            return aboutUs!!
        }
    }
}
