package ir.codecomp.customViews.progress

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams as LinearLayoutParams
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import androidx.core.widget.ImageViewCompat
import ir.codecomp.customViews.R

class ImageProgress: RelativeLayout {

    var progress: Float = 0.5f
        set(value) {
            field = value
            updateProgress()
        }

    var imageDrawableResId: Int = 0
        set(value) {
            field = value
            initialImage = AppCompatResources.getDrawable(context, value)!!
            progressImage = AppCompatResources.getDrawable(context, value)!!
            imageViewGray?.let { it.setImageDrawable(initialImage) }
            imageViewProgress?.let { it.setImageDrawable(progressImage) }
        }

    var direction: String = PROGRESS_DIRECTION_BTT
        set(value) {
            if (field != value) {
                field = value
                refreshViews()
            }
        }

    var initialColor: Int = 0
        set(value) {
            field = value
            imageViewGray?.let {
                ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(value))
            }
        }

    var progressColor: Int = 0
        set(value) {
            field = value
            imageViewProgress?.let {
                ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(value))
            }
        }

    companion object {
        val PROGRESS_DIRECTION_BTT = "btt"
        val PROGRESS_DIRECTION_LTR = "ltr"
        val PROGRESS_DIRECTION_RTL = "rtl"
        val PROGRESS_DIRECTION_TTB = "ttb"
    }

    private lateinit var initialImage: Drawable
    private lateinit var progressImage: Drawable
    private var mWith: Int = 0
    private var mHeight: Int = 0

    private var overLayout : LinearLayout? = null
    private var imageViewProgress: ImageView? = null
    private var imageViewGray: ImageView? = null

    constructor(context: Context) : super(context){
        onViewCreated()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        onViewCreated()
        checkAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        onViewCreated()
        checkAttributes(context, attrs)
    }

    private fun checkAttributes(context: Context, attrs: AttributeSet) {
        context.withStyledAttributes(attrs, R.styleable.CustomProgress) {
            val iColor = getColor(R.styleable.CustomProgress_initialColor, 0)
            initialColor = if (iColor != 0) iColor else Color.LTGRAY

            val pColor = getColor(R.styleable.CustomProgress_progressColor, 0)
            progressColor = if (pColor != 0) pColor else Color.GREEN

            val dir = this.getString(R.styleable.CustomProgress_direction)
            direction = if (dir != null && dir.isNotEmpty()) dir else PROGRESS_DIRECTION_BTT

            val drawableResId = getResourceId(R.styleable.CustomProgress_src, 0)
            imageDrawableResId = if (drawableResId > 0) {
                drawableResId
            } else {
                android.R.drawable.picture_frame
            }
        }
    }

    private fun onViewCreated() {
        viewTreeObserver.addOnPreDrawListener(
            object: ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    mWith = measuredWidth
                    mHeight = measuredHeight
                    buildViews()
                    return true
                }
            }
        )

    }

    private fun buildViews() {
        imageViewGray = ImageView(context)
        imageViewGray?.layoutParams = LayoutParams(mWith, mHeight)
        imageViewGray?.scaleType = ImageView.ScaleType.FIT_XY
        ImageViewCompat.setImageTintList(imageViewGray!!, ColorStateList.valueOf(initialColor))
        imageViewGray?.setImageDrawable(initialImage)

        imageViewProgress = ImageView(context)
        imageViewProgress?.layoutParams = LinearLayoutParams(mWith, mHeight)
        imageViewProgress?.scaleType = ImageView.ScaleType.FIT_XY
        ImageViewCompat.setImageTintList(imageViewProgress!!, ColorStateList.valueOf(progressColor))
        imageViewProgress?.setImageDrawable(progressImage)

        overLayout = LinearLayout(context)
        overLayout?.orientation = LinearLayout.VERTICAL

        refreshViews()
    }

    private fun refreshViews() {
        updateProgress()

        removeAllViews()
        overLayout?.let{
            it.removeAllViews()
            if (direction == PROGRESS_DIRECTION_BTT || direction == PROGRESS_DIRECTION_RTL) {
                addView(imageViewProgress)
                it.addView(imageViewGray)
                addView(overLayout)
            } else {
                addView(imageViewGray)
                it.addView(imageViewProgress)
                addView(overLayout)
            }
        }
    }

    private fun updateProgress() {
        if (direction == PROGRESS_DIRECTION_BTT) {
            val lp = LayoutParams(mWith, (mHeight * (1 - progress)).toInt())
            overLayout?.layoutParams = lp
        } else if (direction == PROGRESS_DIRECTION_TTB) {
            val lp = LayoutParams(mWith, (mHeight * progress).toInt())
            overLayout?.layoutParams = lp
        } else if (direction == PROGRESS_DIRECTION_LTR) {
            val lp = LayoutParams((mWith * progress).toInt(), mHeight)
            overLayout?.layoutParams = lp
        } else {
            val lp = LayoutParams((mWith * (1 - progress)).toInt(), mHeight)
            overLayout?.layoutParams = lp
        }
    }



}