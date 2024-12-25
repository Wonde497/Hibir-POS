package net.geidea.payment.customviews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import net.geidea.payment.transaction.model.EntryMode
import net.geidea.payment.R
import net.geidea.payment.databinding.ViewLedLightBinding
import net.geidea.utils.BuzzerUtils.playForCardReadError
import net.geidea.utils.BuzzerUtils.playForTransactionApproved
import net.geidea.utils.BuzzerUtils.playForTransactionDeclined
import net.geidea.utils.BuzzerUtils.playSuccessSound


class LedLightView constructor(ctx: Context, attrs: AttributeSet? = null) : LinearLayout(ctx, attrs) {

    companion object{
        const val OFF = 0
        const val ON = 1
        const val BLINK = 2
    }

    private val bind by lazy { ViewLedLightBinding.inflate(LayoutInflater.from(context), this, false) }

    private val iconList : MutableList<AppCompatImageView> = mutableListOf()
    private val colorList = intArrayOf(getColor(R.color.green),getColor(R.color.yellow),getColor(R.color.blue),getColor(R.color.red))

    @IntDef(OFF, ON, BLINK)
    @Retention(AnnotationRetention.SOURCE)
    annotation class STATUS

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.LedLightView)
            typedArray.recycle()
        }

        iconList.add(bind.light1)
        iconList.add(bind.light2)
        iconList.add(bind.light3)
        iconList.add(bind.light4)

        ledGlowDefault()

        addView(bind.root)
    }

    private fun setLightStatus(@IntRange(from = -1, to = 3) index: Int, @STATUS status: Int = OFF, color: Int = -1) {
        clearAnimation()
        for (i in iconList.indices) {
            if (status == BLINK) {
                iconList[i].setImageWithBlink(circleDrawable(color))
            } else {
                when (index) {
                    -1 -> iconList[i].setImageDrawable(ringDrawable(colorList[i]))
                    i -> {
                        val tintColor = if (color == -1) colorList[i] else color
                        val drawable = if (status == OFF) ringDrawable(tintColor) else circleDrawable(tintColor)
                        iconList[i].setImageDrawable(drawable)
                    }
                    else -> iconList[i].setImageDrawable(ringDrawable(colorList[i]))
                }
            }
        }
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(context,color)
    }

    private fun ringDrawable(color : Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.TRANSPARENT)
            setStroke(5,color)
        }
    }

    private fun circleDrawable(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }

    private fun ImageView.setImageWithBlink(drawable: Drawable){
        startAnimation(AlphaAnimation(1f, 0f).apply {
            duration = 500
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        })
        setImageDrawable(drawable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearAnimation()
    }

    fun ledGlowDefault(){
        setLightStatus(0, ON)
    }

    fun ledGlowCardTap(isSuccess : Boolean,entryMode: EntryMode? = null){
        if (isSuccess){
            if (entryMode == EntryMode.CONTACTLESS) {
                val green = getColor(R.color.green)
                iconList.forEach { it.setImageDrawable(circleDrawable(green)) }
                playSuccessSound()
            }

        }else{
            setLightStatus(3,ON,getColor(R.color.red))
            playForCardReadError()
        }
    }

    fun ledGlowTransactionStatus(isApproved: Boolean) {
        if (isApproved) {
            setLightStatus(0, ON, getColor(R.color.green))
            playForTransactionApproved()
        } else {
            setLightStatus(3, ON, getColor(R.color.red))
            playForTransactionDeclined()
        }
    }


}
