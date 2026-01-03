package trust.jesus.discover.actis

import android.app.Activity
import android.app.ActivityManager
import android.app.ApplicationErrorReport.CrashInfo
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import trust.jesus.discover.BuildConfig
import trust.jesus.discover.little.Globus
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.concurrent.Volatile


class ErrorHandler private constructor(activity: Activity) : Thread.UncaughtExceptionHandler {



    override fun uncaughtException(thread: Thread, exception: Throwable) {
        // Don't re-enter -- avoid infinite loops if crash-reporting crashes.
        if (mCrashing) return
        mCrashing = true
        defaultExceptionHandler(thread, exception)
    }

    companion object {
        fun getINSTANCE(activity: Activity): ErrorHandler {
            if (mErrorHandler == null) {
                mErrorHandler = ErrorHandler(activity)
            }
            return mErrorHandler!!
        }


        //called in Activity.create..
        fun toCatch(activity: Activity) {
            //if (BuildConfig.DEBUG.not())
                Thread.setDefaultUncaughtExceptionHandler(getINSTANCE(activity))
        }
        private val gc: Globus = Globus.getAppContext() as Globus
        fun defaultExceptionHandler(thread: Thread?, exception: Throwable?) {

            try {
                gc.log("defaultExceptionHandler")
                if (exception != null)
                    gc.handleUncaughtException(exception)
                if (mOldHandler != null) {
                    gc.log("defaultExceptionHandler  mOldHandler != null")
                    if (thread != null) {
                        if (exception != null) {
                            mOldHandler.uncaughtException(thread, exception)
                        }
                    }
                }
            } catch (ex: Exception) {
                //Log.e(mPackageName, ex.message!!)
                gc.crashLog("ex: " + ex.message, 68)
            }
        }


        // Prevents infinite loops.
        @Volatile
        private var mCrashing = false


        private val mOldHandler: Thread.UncaughtExceptionHandler? = Thread
            .getDefaultUncaughtExceptionHandler()

        private var mErrorHandler: ErrorHandler? = null


    }
}