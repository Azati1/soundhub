package com.azati1.soundhub

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setMargins
import com.azati1.soundhub.components.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_recomended_applications.*
import android.content.Intent
import android.util.Log

const val FIRST_TIMES_SHOWED_DATE = "FIRST_TIMES_SHOWED_DATE"
const val IS_ALREADY_SHOW_RATE_DIALOG = "IS_ALREADY_SHOW_RATE_DIALOG"
private const val PREFS_NAME: String = "PREFERENCES"

class RecomendedApplicationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recomended_applications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goBackButton.setOnClickListener {
            parentFragment?.fragmentManager?.popBackStack()

            context?.let { context ->

                val isAlreadyShowRateDialog = context
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getBoolean(IS_ALREADY_SHOW_RATE_DIALOG, false)

                val isNowShowRateUsDialog = (context.applicationContext as? AppComponent)?.isRateUsDialogShowed ?: false

                if (!isAlreadyShowRateDialog && !isNowShowRateUsDialog) {
                    parentFragment?.fragmentManager?.let { fragmentManager ->
                        Log.d("CDA123", "show rate dialog from recommended")
                        val rateAppDialogFragment = RateAppDialogFragment.create()
                        rateAppDialogFragment.show(fragmentManager, "")
                        (context.applicationContext as? AppComponent)?.isRateUsDialogShowed = true
                    }
                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(IS_ALREADY_SHOW_RATE_DIALOG, true)
                        .apply()
                }
            }
        }

        buildContent()
    }

    private fun buildContent() {
        val crossPressed = (context?.applicationContext as? AppComponent)?.getCrossPromo()
        crossPressed?.let { crossPromo ->
            buildWithStrategy("wheel", crossPressed.recomendedApps)
        }
    }

    private fun buildWithStrategy(strategy: String, recommendedApps: List<RecomendedApp>) {
        when (strategy) {
            "fixed" -> buildWithFixedStrategy(recommendedApps)
            "wheel" -> buildWithWheelStrategy(recommendedApps)
        }
    }

    private fun buildWithFixedStrategy(recommendedApps: List<RecomendedApp>) {
        recommendedApps.take(3).forEach {
            linearContent.addView(buildItem(it))
        }
    }

    private fun buildWithWheelStrategy(recommendedApps: List<RecomendedApp>) {
        if (recommendedApps.size > 3) {
            context?.let { context ->

                val currentTimeInMillis = System.currentTimeMillis()

                val firstTimeShow = context
                    .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .getLong(FIRST_TIMES_SHOWED_DATE, -1)

                if (firstTimeShow == -1L) {

                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putLong(FIRST_TIMES_SHOWED_DATE, currentTimeInMillis)
                        .apply()

                    recommendedApps.take(3).forEach {
                        linearContent.addView(buildItem(it))
                    }

                } else {

                    val offsetInMillis = currentTimeInMillis - firstTimeShow
                    var offsetInDays = offsetInMillis / (1000*60*60*24)
                    var idStartFrom = 0
                    while (offsetInDays > 0) {
                        idStartFrom = (idStartFrom + 3) % recommendedApps.size
                        offsetInDays--
                    }
                    val appsList = arrayListOf<RecomendedApp>()
                    appsList.add(recommendedApps[idStartFrom % recommendedApps.size])
                    appsList.add(recommendedApps[(idStartFrom + 1) % recommendedApps.size])
                    appsList.add(recommendedApps[(idStartFrom + 2) % recommendedApps.size])

                    appsList.take(3).forEach {
                        linearContent.addView(buildItem(it))
                    }

                }

            }
        } else {
            recommendedApps.take(3).forEach {
                linearContent.addView(buildItem(it))
            }
        }
    }

    private fun buildItem(item: RecomendedApp): View {

        val layout = LayoutInflater.from(context).inflate(R.layout.recommended_app_item, null, false)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(16)
        layoutParams.weight = 1.0f
        layout.layoutParams = layoutParams

        val name = layout.findViewById<TextView>(R.id.app_name)
        name.text = item.name

        Picasso.get()
            .load(item.pictureUrl)
            .centerCrop()
            .fit()
            .placeholder(R.drawable.ic_music)
            .into(layout.findViewById<ImageView>(R.id.app_picture))

        layout.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(item.appUrl)
                )
            )
        }

        return layout
    }

    companion object {

        fun create(): RecomendedApplicationsFragment {
            return RecomendedApplicationsFragment()
        }

    }

}
