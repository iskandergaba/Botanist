// Basic test
// @author: Cactus, Chia George Washington, Marimo, and Venus Flytrap
package com.scientists.happy.botanist;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
/**
 * Instrumentation test, which will execute on an Android device.
 * @see <a href = "http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest
{
    /**
     * Basic test
     * @throws Exception if the package is bad
     */
    @Test
    public void useAppContext() throws Exception
    {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.scientists.happy.botanist", appContext.getPackageName());
    }
}