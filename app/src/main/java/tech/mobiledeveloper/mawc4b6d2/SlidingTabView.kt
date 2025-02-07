package tech.mobiledeveloper.mawc4b6d2

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class SlidingTabView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var tabTitles = listOf<String>() // Список вкладок
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 40f
        color = 0xFF7A8A99.toInt() // Цвет невыбранного текста
        textAlign = Paint.Align.CENTER
    }
    private val selectedTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 40f
        color = 0xFF000000.toInt() // Цвет выбранного текста
        textAlign = Paint.Align.CENTER
    }
    private val tabPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt() // Цвет выделенной вкладки
    }

    private var selectedIndex = 0 // Текущая выбранная вкладка
    private var sliderLeft = 0f
    private var sliderRight = 0f
    private var tabWidth = 0f

    private var onTabSelectedListener: ((Int) -> Unit)? = null

    init {
        setOnClickListener { } // Чтобы View мог получать `onTouchEvent`
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (tabTitles.isEmpty()) return // Если вкладки не заданы, ничего не рисуем

        val totalWidth = width.toFloat()
        tabWidth = totalWidth / tabTitles.size

        // 🎯 Удалено: Отрисовка полоски под табами

        // Рисуем фон вкладки (выделенной)
        canvas.drawRoundRect(
            RectF(sliderLeft, 10f, sliderRight, height - 10f),
            20f, 20f, tabPaint
        )

        // Рисуем вкладки (текст)
        for (i in tabTitles.indices) {
            val x = i * tabWidth + tabWidth / 2
            val y = height / 2f + 15f
            val paint = if (i == selectedIndex) selectedTextPaint else textPaint
            canvas.drawText(tabTitles[i], x, y, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        if (event.action == MotionEvent.ACTION_DOWN && tabTitles.isNotEmpty()) {
            val clickedIndex = (event.x / tabWidth).toInt()
            if (clickedIndex != selectedIndex) {
                animateTabSelection(selectedIndex, clickedIndex)
                selectedIndex = clickedIndex
                onTabSelectedListener?.invoke(clickedIndex) // Уведомляем о выборе таба
                invalidate()
            }
        }
        return true
    }

    private fun animateTabSelection(fromIndex: Int, toIndex: Int) {
        if (tabTitles.isEmpty()) return

        val fromLeft = fromIndex * tabWidth
        val fromRight = (fromIndex + 1) * tabWidth
        val toLeft = toIndex * tabWidth
        val toRight = (toIndex + 1) * tabWidth

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            addUpdateListener { animation ->
                val progress = animation.animatedFraction
                sliderLeft = fromLeft + (toLeft - fromLeft) * progress
                sliderRight = fromRight + (toRight - fromRight) * progress
                invalidate()
            }
        }
        animator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (tabTitles.isNotEmpty()) {
            sliderLeft = selectedIndex * (w.toFloat() / tabTitles.size)
            sliderRight = (selectedIndex + 1) * (w.toFloat() / tabTitles.size)
        }
    }

    // ✅ Устанавливаем вкладки программно
    fun setTabs(tabs: List<String>) {
        if (tabs.isNotEmpty()) {
            tabTitles = tabs
            selectedIndex = 0
            requestLayout()
            invalidate()
        }
    }

    // ✅ Добавляем слушатель смены вкладки
    fun setOnTabSelectedListener(listener: (Int) -> Unit) {
        onTabSelectedListener = listener
    }
}
