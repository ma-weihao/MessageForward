package cn.quickweather.android.common.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

/**
 * Created by maweihao on 5/24/24
 */
private val density: Float by lazyUnsafe {
    applicationContext.resources.displayMetrics.density
}

val screenWidth: Int by lazyUnsafe {
    val service = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    service.defaultDisplay.width
}

fun Int.px(context: Context): Float {
    return context.resources.displayMetrics.density * this.toFloat()
}

fun Int.px(): Float {
    return Resources.getSystem().displayMetrics.density * this.toFloat()
}

val Int.px: Float
    get() {
        return Resources.getSystem().displayMetrics.density * this.toFloat()
    }

fun Float.px(): Float {
    return density * this
}

val GlobalRes: Resources
    get() {
        return applicationContext.resources
    }

fun Int?.toDrawable(): Drawable? {
    if (this == null) return null
    return ResourcesCompat.getDrawable(GlobalRes, this, null)
}

fun Int?.toResString(vararg formatArgs: Any?): String {
    if (this == null) return ""
    return GlobalRes.getString(this, *formatArgs)
}

fun Int.toResColor(): Int {
    return GlobalRes.getColor(this)
}

fun Boolean?.toVisibility(): Int {
    return if (this == true) View.VISIBLE else View.GONE
}

fun parseColor(s: String?, defaultValue: Int = 0): Int {
    return s?.let {
        try {
            Color.parseColor(s)
        } catch (ignored: Exception) {
            defaultValue
        }
    } ?: defaultValue
}

@ColorInt
fun Int.withAlpha(alpha: Float): Int {
    if (alpha < 0 || alpha > 1) return this
    return Color.argb((alpha * 255.0f + 0.5f).toInt(), Color.red(this), Color.green(this), Color.blue(this))
}

fun Int.toHexColor(): String {
    return String.format("#%06X", 0xFFFFFF and this)
}

fun RecyclerView.canScrollUp(): Boolean {
    return canScrollVertically(-1)
}

fun View.setMarginStart(value: Int) {
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    if (params.marginStart == value) return
    params.marginStart = value
    layoutParams = params
}

fun View.setMarginEnd(value: Int) {
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    if (params.marginEnd == value) return
    params.marginEnd = value
    layoutParams = params
}

fun View.applyMarginTop(@StringRes tag: Int, value: Int) {
    val last = getTag(tag) as? Int ?: 0
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    val marginTop = params.topMargin + value - last
    params.topMargin = marginTop
    setTag(tag, value)
    layoutParams = params
}

fun View.applyMarginBottom(@StringRes tag: Int, value: Int) {
    val last = getTag(tag) as? Int ?: 0
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    val marginBottom = params.bottomMargin + value - last
    params.bottomMargin = marginBottom
    setTag(tag, value)
    layoutParams = params
}

fun View.applyPaddingTop(@StringRes tag: Int, value: Int) {
    val last = getTag(tag) as? Int ?: 0
    val top = this.paddingTop + value - last
    setPadding(paddingLeft, top, paddingRight, paddingBottom)
    setTag(tag, value)
}

fun View.applyPaddingBottom(@StringRes tag: Int, value: Int) {
    val last = getTag(tag) as? Int ?: 0
    val bottom = this.paddingBottom + value - last
    setPadding(paddingLeft, paddingTop, paddingRight, bottom)
    setTag(tag, value)
}

fun View.applyHeight(value: Int) {
    if (layoutParams.height == value) return
    layoutParams.height = value
    layoutParams = layoutParams
}

fun View.applyWidth(value: Int) {
    if (layoutParams.width == value) return
    layoutParams.width = value
    layoutParams = layoutParams
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun Activity?.hideKeyboard() {
    this ?: return
    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    // 隐藏软键盘
    imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
}

fun EditText.showKeyboard() {
    requestFocus()
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(findFocus(), 0)
}

fun TextView.loadOrGone(content: String?) {
    text = content
    visibility = (content?.isNotBlank() == true).toVisibility()
}

fun copyContentToClipBoard(context: Context, content: String?) {
    val cm: ClipboardManager? =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val mClipData = ClipData.newPlainText("Label", content)
    cm?.setPrimaryClip(mClipData)
}

fun isDarkTheme(): Boolean {
    val flag = GlobalRes.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return flag == Configuration.UI_MODE_NIGHT_YES
}

fun Window.setStatusBarTextDark(dark: Boolean, fitDarkTheme: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val controller = decorView.windowInsetsController
        val finalDark = if (fitDarkTheme) {
            if (isDarkTheme()) !dark else dark
        } else {
            dark
        }
        if (finalDark) {
            controller?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            controller?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        }
    } else {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val decorView = decorView
        decorView.systemUiVisibility = if (dark) {
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }
}

internal typealias SimpleCallback = () -> Unit
fun AppBarLayout.addStateListener(onExpand: SimpleCallback, onCollapse: SimpleCallback, onIndeterminate: SimpleCallback) {
    addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
        private var state: CollapsingToolbarLayoutState? = null

        override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
            if (verticalOffset == 0) {
                if (state != CollapsingToolbarLayoutState.EXPANDED) {
                    onExpand.invoke()
                }
            } else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                    state = CollapsingToolbarLayoutState.COLLAPSED
                    onCollapse.invoke()
                }
            } else if (state != CollapsingToolbarLayoutState.INDETERMINATE) {
                if (state == CollapsingToolbarLayoutState.COLLAPSED) {
                    onIndeterminate.invoke()
                }
                state = CollapsingToolbarLayoutState.INDETERMINATE
            }
        }
    })
}

fun Context.enableWideColorGamut(): Boolean {
    if (!DisplayManagerCompat.getInstance(this).displays[0].isWideColorGamut) {
        return false
    }
    requireActivity().let { activity ->
        activity.window.colorMode = ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT
        val wideColorGamut = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            activity.window.isWideColorGamut
        } else {
            true
        }
        logI("enableWideColorGamut", "$wideColorGamut")
        return wideColorGamut
    }
}

val Context.activityFragmentManager: FragmentManager
    get() {
        return (this.requireActivity() as FragmentActivity).supportFragmentManager
    }

fun Context.isWideColorGamut(): Boolean {
    if (!DisplayManagerCompat.getInstance(this).displays[0].isWideColorGamut) {
        return false
    }
    requireActivity().let { activity ->
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            activity.window.isWideColorGamut
        } else {
            true
        }
    }
}

private enum class CollapsingToolbarLayoutState {
    EXPANDED, COLLAPSED, INDETERMINATE
}

open class SimpleViewHolder<Data>(parent: ViewGroup, @LayoutRes layout: Int) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false)) {
    open fun bind(data: Data) { }
}

class IntervalDecoration(private val bottom: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = bottom
    }

}

fun Context.requireActivity(): ComponentActivity {
    return findActivity() ?: error(
        "${this.javaClass.simpleName} is not an activity"
    )
}


fun Context.findActivity(): ComponentActivity? {
    return when (this) {
        is ComponentActivity -> {
            this
        }
        is ContextWrapper -> {
            baseContext.findActivity()
        }
        else -> {
            null
        }
    }
}