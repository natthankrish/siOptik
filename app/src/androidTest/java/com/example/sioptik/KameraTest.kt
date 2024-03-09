import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.sioptik.Kamera
import com.example.sioptik.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class KameraTest {

    @Test
    fun captureButton_isClickable() {
        // Launch the activity to make the UI visible
        ActivityScenario.launch(Kamera::class.java)

        // Check if the capture button is clickable
        onView(withId(R.id.captureButton))
            .check(matches(isClickable()))

        // Optionally, simulate a click on the button
        onView(withId(R.id.captureButton)).perform(click())
    }

}
