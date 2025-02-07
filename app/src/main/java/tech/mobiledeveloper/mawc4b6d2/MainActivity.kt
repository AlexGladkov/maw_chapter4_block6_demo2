package tech.mobiledeveloper.mawc4b6d2

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var headerBackground: ImageView
    private lateinit var avatar: ImageView
    private lateinit var accountName: TextView
    private lateinit var followersCount: TextView
    private lateinit var tabView: SlidingTabView
    private lateinit var recyclerView: RecyclerView
    private lateinit var shadowView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val initialBackDropHeight = dpToPx(128)
        val initialAvatarPosition = dpToPx(44)
        val initialAvatarHeight = dpToPx(72)
        val initialAccountPosition = initialAvatarPosition + initialAvatarHeight + dpToPx(16)
        val initialAccountHeight = dpToPx(23)
        val initialFollowersPosition = initialAccountPosition + initialAccountHeight + dpToPx(2)
        val initialFollowersHeight = dpToPx(16)
        val initialTabsPosition = initialFollowersPosition + initialFollowersHeight + dpToPx(32)
        val initialTabsHeight = dpToPx(40)
        val initialListPosition = initialTabsPosition + initialTabsHeight + dpToPx(16)
        val stopTitlePosition = dpToPx(16)
        val collapsedAppBarHeight = dpToPx(80)
        val initialShadowView = dpToPx(80)

        // Инициализация UI-элементов
        headerBackground = findViewById(R.id.headerBackground)
        avatar = findViewById(R.id.avatar)
        accountName = findViewById(R.id.accountName)
        followersCount = findViewById(R.id.followersCount)
        tabView= findViewById(R.id.slidingTabView)
        recyclerView = findViewById(R.id.recyclerView)
        shadowView = findViewById(R.id.shadowView)

        val tabs = listOf("TWEETS", "LIKES", "MEDIA")
        tabView.setTabs(tabs)
        tabView.setOnTabSelectedListener { index ->
            val adapter = recyclerView.adapter as SimpleAdapter
            when (index) {
                0 -> adapter.updateContent(generateContent("Tweets"))
                1 -> adapter.updateContent(generateContent("Likes"))
                2 -> adapter.updateContent(generateContent("Media"))
            }
        }

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SimpleAdapter(generateContent("Tweets"))
        recyclerView.clipToPadding = false

        shadowView.translationY = initialShadowView

        // ScrollListener для управления анимацией
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var totalDy = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalDy += dy

                avatar.translationY = initialAvatarPosition
                accountName.translationY = initialAccountPosition
                tabView.translationY = initialTabsPosition

                val progress = totalDy.toFloat() / (initialBackDropHeight - collapsedAppBarHeight)

                // Анимация аватарки (уменьшается, но не двигается вниз)
                val avatarScale = 1 - progress.coerceAtMost(1f)
                avatar.scaleX = avatarScale
                avatar.scaleY = avatarScale

                shadowView.alpha = avatarScale

//                // Анимация текста (поднимается, но фиксируется на середине свернутого фона)
                val followersTranslation = initialFollowersPosition - totalDy
                val accountTranslation = initialAccountPosition - totalDy
                if (accountTranslation > stopTitlePosition) {
                    accountName.translationY = accountTranslation
                    followersCount.translationY = followersTranslation
                } else {
                    accountName.translationY = stopTitlePosition
                    followersCount.translationY = stopTitlePosition + initialAccountHeight + dpToPx(2)
                }
//
//                // Сжатие фонового изображения
                val newHeight = initialBackDropHeight - totalDy
                headerBackground.layoutParams.height = newHeight.coerceAtLeast(collapsedAppBarHeight).roundToInt()
                headerBackground.requestLayout()

                // Двигаем табы
                val adjustedTabsY = (initialTabsPosition - totalDy).coerceAtLeast(collapsedAppBarHeight)
                tabView.translationY = adjustedTabsY

                // Двигаем recycler
                val adjustedRecyclerY = (initialListPosition - totalDy).coerceAtLeast(collapsedAppBarHeight + initialTabsHeight + dpToPx(16))
                val layoutParams = recyclerView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.topMargin = adjustedRecyclerY.toInt()
                recyclerView.layoutParams = layoutParams
            }
        })
    }

    private fun dpToPx(dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
    }

    private fun generateContent(type: String): List<String> {
        return when (type) {
            "Tweets" -> List(50) { "Tweet #$it: This is a tweet about something interesting." }
            "Likes" -> List(50) { "Like #$it: You liked this post from @user$it." }
            "Mentions" -> List(50) { "Mention #$it: @user$it mentioned you in a comment." }
            else -> emptyList()
        }
    }
}





