package im.delight.apprater;

/*
 * Copyright (c) delight.im <info@delight.im>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateUtils;

/**
 * Android library that lets you prompt users to rate your application
 * <p>
 * Users will be redirected to Google Play by default, but any other app store can be used by providing a custom URI (via setTargetUri(...))
 * <p>
 * This library uses English default phrases but you may provide custom phrases either as Strings or resource IDs (via setPhrases(...))
 * <p>
 * Users will be prompted to rate your app after at least 2 days and 5 app launches (customizable via setDaysBeforePrompt(...) and setLaunchesBeforePrompt(...))
 * <p>
 * You may overwrite the default preference group name and key names by calling setPreferenceKeys(...) (usually not necessary)
 * <p>
 * The prompting dialog will be displayed (if adequate) as soon as you call show() on your AppRater instance
 * <p>
 * The dialog will only be shown if at least one application is available on the user's phone to handle the Intent that is defined by the target URI
 * <p>
 * AlertDialog is used so that the prompt window adapts to your application's styles and themes
 * <p>
 * Minimum API level: 8 (Android 2.2)
 */
public class AppRater {

    private static final String DEFAULT_PREF_GROUP = "app_rater";
    private static final String DEFAULT_PREFERENCE_DONT_SHOW = "flag_dont_show";
    private static final String DEFAULT_PREFERENCE_LAUNCH_COUNT = "launch_count";
    private static final String DEFAULT_PREFERENCE_FIRST_LAUNCH = "first_launch_time";
    private static final String DEFAULT_TARGET_URI = "market://details?id=%s";
    private static final String DEFAULT_TEXT_TITLE = "Rate this app";
    private static final String DEFAULT_TEXT_EXPLANATION = "Has this app convinced you? We would be very happy if you could rate our application on Google Play. Thanks for your support!";
    private static final String DEFAULT_TEXT_NOW = "Rate now";
    private static final String DEFAULT_TEXT_LATER = "Later";
    private static final String DEFAULT_TEXT_NEVER = "No, thanks";
    private static final int DEFAULT_DAYS_BEFORE_PROMPT = 2;
    private static final int DEFAULT_LAUNCHES_BEFORE_PROMPT = 5;
    private Context mContext;
    private String mPackageName;
    private int mDaysBeforePrompt;
    private int mLaunchesBeforePrompt;
    private String mTargetUri;
    private String mText_title;
    private String mText_explanation;
    private String mText_buttonNow;
    private String mText_buttonLater;
    private String mText_buttonNever;
    private String mPrefGroup;
    private String mPreference_dontShow;
    private String mPreference_launchCount;
    private String mPreference_firstLaunch;
    private Intent mTargetIntent;

    /**
     * Creates a new AppRater instance
     * @param context the Activity reference to use for this instance (usually the Activity you called this from)
     */
    public AppRater(final Context context) {
      this(context, context.getPackageName());
    }

    /**
     * Creates a new AppRater instance
     * @param context the Activity reference to use for this instance (usually the Activity you called this from)
     * @param packageName your application's package name that will be used to open the ratings page
     */
    public AppRater(final Context context, final String packageName) {
    	if (context == null) {
    		throw new RuntimeException("context may not be null");
    	}
    	mContext = context;
    	mPackageName = packageName;
    	mDaysBeforePrompt = DEFAULT_DAYS_BEFORE_PROMPT;
    	mLaunchesBeforePrompt = DEFAULT_LAUNCHES_BEFORE_PROMPT;
    	mTargetUri = DEFAULT_TARGET_URI;
    	mText_title = DEFAULT_TEXT_TITLE;
    	mText_explanation = DEFAULT_TEXT_EXPLANATION;
    	mText_buttonNow = DEFAULT_TEXT_NOW;
    	mText_buttonLater = DEFAULT_TEXT_LATER;
    	mText_buttonNever = DEFAULT_TEXT_NEVER;
        mPrefGroup = DEFAULT_PREF_GROUP;
        mPreference_dontShow = DEFAULT_PREFERENCE_DONT_SHOW;
        mPreference_launchCount = DEFAULT_PREFERENCE_LAUNCH_COUNT;
        mPreference_firstLaunch = DEFAULT_PREFERENCE_FIRST_LAUNCH;
    }

    /**
     * Sets how many days to wait before users may be prompted
     *
     * @param days the number of days to wait before showing the prompt
     */
    public void setDaysBeforePrompt(final int days) {
    	mDaysBeforePrompt = days;
    }

    /**
     * Sets how often users must have launched the app (i.e. called AppRater.show()) before they may be prompted
     *
     * @param launches the number of launches to wait before showing the prompt
     */
    public void setLaunchesBeforePrompt(final int launches) {
    	mLaunchesBeforePrompt = launches;
    }

    /**
     * Sets the target URI string that must contain exactly one placeholder (`%s`) for the package name
     *
     * @param uri the target URI to open when the user wants to rate the app (must include `%s`)
     */
    public void setTargetUri(final String uri) {
    	mTargetUri = uri;
    }

    /**
     * Sets the given Strings to use for the prompt
     *
     * @param title the title for the prompt window (as a string)
     * @param explanation the explanatory text that will be shown inside the prompt window (as a string)
     * @param buttonNow the caption for the `Rate now` button (as a string)
     * @param buttonLater the caption for the `Maybe later` button (as a string)
     * @param buttonNever the caption for the `Never` button (as a string)
     */
    public void setPhrases(final String title, final String explanation, final String buttonNow, final String buttonLater, final String buttonNever) {
    	mText_title = title;
    	mText_explanation = explanation;
    	mText_buttonNow = buttonNow;
    	mText_buttonLater = buttonLater;
    	mText_buttonNever = buttonNever;
    }

    /**
     * Sets the Strings referenced by the given resource IDs to use for the prompt
     *
     * @param title the title for the prompt window (as a resource ID)
     * @param explanation the explanatory text that will be shown inside the prompt window (as a resource ID)
     * @param buttonNow the caption for the `Rate now` button (as a resource ID)
     * @param buttonLater the caption for the `Maybe later` button (as a resource ID)
     * @param buttonNever the caption for the `Never` button (as a resource ID)
     */
    public void setPhrases(final int title, final int explanation, final int buttonNow, final int buttonLater, final int buttonNever) {
    	try {
    		mText_title = mContext.getString(title);
    	}
    	catch (Exception e) {
    		mText_title = DEFAULT_TEXT_TITLE;
    	}
    	try {
    		mText_explanation = mContext.getString(explanation);
    	}
    	catch (Exception e) {
    		mText_explanation = DEFAULT_TEXT_EXPLANATION;
    	}
    	try {
    		mText_buttonNow = mContext.getString(buttonNow);
    	}
    	catch (Exception e) {
    		mText_buttonNow = DEFAULT_TEXT_NOW;
    	}
    	try {
    		mText_buttonLater = mContext.getString(buttonLater);
    	}
    	catch (Exception e) {
    		mText_buttonLater = DEFAULT_TEXT_LATER;
    	}
    	try {
    		mText_buttonNever = mContext.getString(buttonNever);
    	}
    	catch (Exception e) {
    		mText_buttonNever = DEFAULT_TEXT_NEVER;
    	}
    }

    /**
     * Sets the given keys for the preferences that will be used
     * @param group the preference group file that all values of this class go in
     * @param dontShow the preference name for the dont-show flag
     * @param launchCount the preference name for the launch counter
     * @param firstLaunchTime the preference name for the first launch time value
     */
    public void setPreferenceKeys(String group, String dontShow, String launchCount, String firstLaunchTime) {
    	mPrefGroup = group;
    	mPreference_dontShow = dontShow;
    	mPreference_launchCount = launchCount;
    	mPreference_firstLaunch = firstLaunchTime;
    }

    private void createTargetIntent() {
    	mTargetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(mTargetUri, mPackageName)));
    }

    /**
     * Checks if the rating dialog should be shown to the user and displays it if needed
     *
     * @return the AlertDialog that has been shown or null
     */
    @SuppressLint("CommitPrefEdits")
	public AlertDialog show() {
    	createTargetIntent();
    	if (mContext.getPackageManager().queryIntentActivities(mTargetIntent, 0).size() <= 0) {
    		return null; // no app available to handle the intent
    	}

        final SharedPreferences prefs = mContext.getSharedPreferences(mPrefGroup, 0);
        final SharedPreferences.Editor editor = prefs.edit();

        if (prefs.getBoolean(mPreference_dontShow, false)) { // if user opted not to rate the app
        	return null; // do not show anything
        }

        long launch_count = prefs.getLong(mPreference_launchCount, 0); // get launch counter
        launch_count++; // increase number of launches by one
        editor.putLong(mPreference_launchCount, launch_count); // write new counter back to preference

        long firstLaunchTime = prefs.getLong(mPreference_firstLaunch, 0); // get date of first launch
        if (firstLaunchTime == 0) { // if not set yet
        	firstLaunchTime = System.currentTimeMillis(); // set to current time
            editor.putLong(mPreference_firstLaunch, firstLaunchTime);
        }

        savePreferences(editor);

        if (launch_count >= mLaunchesBeforePrompt) { // wait at least x app launches
            if (System.currentTimeMillis() >= (firstLaunchTime + (mDaysBeforePrompt * DateUtils.DAY_IN_MILLIS))) { // wait at least x days
                try {
                	return showDialog(mContext, editor, firstLaunchTime);
                }
                catch (Exception e) {
                	return null;
                }
            }
            else {
            	return null;
            }
        }
        else {
        	return null;
        }
    }

    /**
     * Displays a preview of the prompt as it will be shown to users later (use as a replacement for `show()`)
     *
     * @return the AlertDialog that has been shown or null
     * @deprecated you should remove any reference to this method before deploying a production version
     */
    @Deprecated
    public AlertDialog demo() {
    	createTargetIntent();
    	return showDialog(mContext, mContext.getSharedPreferences(mPrefGroup, 0).edit(), System.currentTimeMillis());
    }

    @SuppressLint("NewApi")
	private static void savePreferences(SharedPreferences.Editor editor) {
    	if (editor != null) {
	    	if (Build.VERSION.SDK_INT < 9) {
	    		editor.commit();
	    	}
	    	else {
	    		editor.apply();
	    	}
    	}
    }

    private static void closeDialog(DialogInterface dialog) {
        if (dialog != null) {
        	dialog.dismiss();
        }
    }

    private void setDontShow(SharedPreferences.Editor editor) {
        if (editor != null) {
            editor.putBoolean(mPreference_dontShow, true);
            savePreferences(editor);
        }
    }

    private void setFirstLaunchTime(SharedPreferences.Editor editor, long time) {
    	if (editor != null) {
    		editor.putLong(mPreference_firstLaunch, time);
    		savePreferences(editor);
    	}
    }

    private void buttonNowClick(SharedPreferences.Editor editor, DialogInterface dialog, Context context) {
    	setDontShow(editor);
        closeDialog(dialog);
        context.startActivity(mTargetIntent);
    }

    private void buttonLaterClick(SharedPreferences.Editor editor, DialogInterface dialog, long firstLaunchTime) {
    	setFirstLaunchTime(editor, firstLaunchTime + DateUtils.DAY_IN_MILLIS); // remind again later (but wait at least 24 hours)
    	closeDialog(dialog);
    }

    private void buttonNeverClick(SharedPreferences.Editor editor, DialogInterface dialog) {
    	setDontShow(editor);
        closeDialog(dialog);
    }

    private AlertDialog showDialog(final Context context, final SharedPreferences.Editor editor, final long firstLaunchTime) {
        final AlertDialog.Builder rateDialog = new AlertDialog.Builder(context);
        rateDialog.setTitle(mText_title);
        rateDialog.setMessage(mText_explanation);
        rateDialog.setNeutralButton(mText_buttonLater, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				buttonLaterClick(editor, dialog, firstLaunchTime);
			}
		});
        rateDialog.setPositiveButton(mText_buttonNow, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				buttonNowClick(editor, dialog, context);
			}
		});
        rateDialog.setNegativeButton(mText_buttonNever, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				buttonNeverClick(editor, dialog);
			}
		});
        return rateDialog.show();
    }

}
