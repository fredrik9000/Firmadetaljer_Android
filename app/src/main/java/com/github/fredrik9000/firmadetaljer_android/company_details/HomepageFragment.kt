package com.github.fredrik9000.firmadetaljer_android.company_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment

class HomepageFragment : Fragment() {

    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments!!.getString(ARG_URL)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val webView = WebView(requireContext())
        webView.webViewClient = WebViewClient()
        webView.loadUrl(addHTTPIfNotPresent(url!!))
        return webView
    }

    private fun addHTTPIfNotPresent(url: String): String {
        return if (url.contains("https://") || url.contains("http://")) {
            url
        } else {
            "http://$url"
        }
    }

    companion object {
        const val ARG_URL = "URL"
    }

}
