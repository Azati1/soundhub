package com.azati1.soundhub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.azati1.soundhub.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_rate_app_dialog.*

class RateAppDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rate_app_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rateAppButton.setOnClickListener {
            openApplicationPage()
            dismiss()
            (activity as? MainActivity)?.onClickRateButton()
        }

        closeDialogButton.setOnClickListener {
            dismiss()
            (activity as? MainActivity)?.onClickDismissButton()
        }

    }

    private fun openApplicationPage() {
        context?.let { context ->
            val appPackageName = context.applicationContext.packageName
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
    }

    companion object {
        fun create() : RateAppDialogFragment {
            return RateAppDialogFragment()
        }
    }

}
