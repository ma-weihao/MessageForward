package cn.quickweather.messageforward.sms

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import cn.quickweather.messageforward.R

/**
 * Created by maweihao on 5/29/24
 */
enum class ForwardStatus(
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {

    // Do not change the order of this enum

    Pending(R.string.label_status_pending, R.drawable.ic_schedule),

    DetectingPriority(R.string.label_status_detecting_priority, R.drawable.ic_change_circle),

    ForwardSucceed(R.string.label_status_forward_succeed, R.drawable.ic_check_circle),

    ForwardFailedDueToSms(R.string.label_status_forward_failed, R.drawable.ic_cancel_circle),

    NotForwardDueToUnimportant(R.string.label_status_unimportant, R.drawable.ic_remove_circle),

    ;

    companion object {
        fun parse(value: Int): ForwardStatus {
            return entries.first { it.ordinal == value }
        }
    }
}