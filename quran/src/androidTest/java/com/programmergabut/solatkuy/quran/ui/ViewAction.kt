package com.programmergabut.solatkuy.quran.ui

import android.view.View
import android.view.ViewParent
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher
import org.hamcrest.Matchers


fun nestedScrollTo(): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return Matchers.allOf(
                isDescendantOfA(isAssignableFrom(NestedScrollView::class.java)),
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        }

        override fun getDescription(): String {
            return "View is not NestedScrollView"
        }

        override fun perform(uiController: UiController, view: View) {
            try {
                val nestedScrollView = findFirstParentLayoutOfClass(
                    view,
                    NestedScrollView::class.java
                ) as NestedScrollView?
                if (nestedScrollView != null) {
                    nestedScrollView.scrollTo(0, view.getTop())
                } else {
                    throw Exception("Unable to find NestedScrollView parent.")
                }
            } catch (e: Exception) {
                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(e)
                    .build()
            }
            uiController.loopMainThreadUntilIdle()
        }
    }
}

private fun findFirstParentLayoutOfClass(view: View, parentClass: Class<out View?>): View? {
    var parent: ViewParent? = FrameLayout(view.getContext())
    var incrementView: ViewParent? = null
    var i = 0
    while (parent != null && parent.javaClass != parentClass) {
        if (i == 0) {
            parent = findParent(view)
        } else {
            parent = findParent(incrementView!!)
        }
        incrementView = parent
        i++
    }
    return parent as View?
}

private fun findParent(view: View): ViewParent? {
    return view.getParent()
}

private fun findParent(view: ViewParent): ViewParent? {
    return view.parent
}

