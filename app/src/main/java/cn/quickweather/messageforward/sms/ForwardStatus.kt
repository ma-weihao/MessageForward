package cn.quickweather.messageforward.sms

import androidx.annotation.StringRes
import cn.quickweather.messageforward.R

/**
 * Created by maweihao on 5/29/24
 */
enum class ForwardStatus(
    @StringRes label: Int,
) {

    ForwardSucceed(R.string.label_status_forward_succeed),

    ForwardFailedDueToSms(R.string.label_status_forward_succeed),
    NotForwardDueToNonCode(R.string.label_status_forward_succeed),


    ;
}