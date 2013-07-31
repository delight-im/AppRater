package com.my.package;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateUtils;

/**
 * Android library that prompts the user to rate the app on Google Play
 * 
 * @author <a href="https://github.com/marcow">Marco W.</a>
 * @version 1.0
 */
public class AppRater {

    private static final String PREFERENCES_CATEGORY = "app_rater";
    private static final String PREFERENCE_DONT_SHOW = "dont_show_again";
    private static final String PREFERENCE_LAUNCH_COUNT = "launch_count";
    private static final String PREFERENCE_FIRST_LAUNCH = "first_launch_time";
    private static final String GOOGLE_PLAY_BASE_URI = "market://details?id=";
    private static final int DEFAULT_DAYS_BEFORE_PROMPT = 2;
    private static final int DEFAULT_LAUNCHES_BEFORE_PROMPT = 5;
    private static final String DEFAULT_TEXT_RATE_APP = "Rate this app";
    private static final String DEFAULT_TEXT_PLEASE_RATE = "Has this app convinced you? We would be very happy if you could rate our application on Google Play. Thanks for your support!";
    private static final String DEFAULT_TEXT_RATE_NOW = "Rate now";
    private static final String DEFAULT_TEXT_REMIND_LATER = "Later";
    private static final String DEFAULT_TEXT_DONT_SHOW = "No, thanks";
    private static AppRater mInstance;
    private int mResource_rateApp;
    private int mResource_pleaseRate;
    private int mResource_rateNow;
    private int mResource_remindLater;
    private int mResource_dontShow;
    private Intent mIntentGooglePlay;

    /** Retrieve a singleton instance of this class on which you can call show() later */
    public static AppRater getInstance() {
      if (mInstance == null) {
    		mInstance = new AppRater();
    	}
    	return mInstance;
    }

    private AppRater() { } // hide the default constructor

    /**
     * Checks if the rating dialog should be shown to the user and returns the AlertDialog if one was shown
     * <p>
     * Default values will be used: 2 days before prompt + 5 launches before prompt
     * <p>
     * Default texts will be used
     * 
     * @param context Activity instance where you called this from and want to display the dialog
     * @param packageName the package name of this application that will be used to open the app's details page on Google Play
     * @return AlertDialog instance if dialog was shown or null
     */
    public AlertDialog show(final Context context, final String packageName) {
    	return show(context, packageName, DEFAULT_DAYS_BEFORE_PROMPT, DEFAULT_LAUNCHES_BEFORE_PROMPT);
    }

    /**
     * Checks if the rating dialog should be shown to the user and returns the AlertDialog if one was shown
     * <p>
     * Default texts will be used
     * 
     * @param context Activity instance where you called this from and want to display the dialog
     * @param packageName the package name of this application that will be used to open the app's details page on Google Play
     * @param daysBeforePrompt number of days that must have gone by before the rating prompt will be shown
     * @param launchesBeforePrompt number of app launches that must have happened before the rating prompt will be shown
     * @return AlertDialog instance if dialog was shown or null
     */
    public AlertDialog show(final Context context, final String packageName, final int daysBeforePrompt, final int launchesBeforePrompt) {
    	return show(context, packageName, daysBeforePrompt, launchesBeforePrompt, 0, 0, 0, 0, 0);
    }

    /**
     * Checks if the rating dialog should be shown to the user and returns the AlertDialog if one was shown
     * 
     * @param context Activity instance where you called this from and want to display the dialog
     * @param packageName the package name of this application that will be used to open the app's details page on Google Play
     * @param daysBeforePrompt number of days that must have gone by before the rating prompt will be shown
     * @param launchesBeforePrompt number of app launches that must have happened before the rating prompt will be shown
     * @param resTitle resource ID for the String to display as the title
     * @param resMessage resource ID for the String to display as the explanation message
     * @param resRateNow resource ID for the String to display on the button [Rate Now]
     * @param resRemindLater resource ID for the String to display on the button [Remind Later]
     * @param resDontShow resource ID for the String to display on the button [No, thanks]
     * @return AlertDialog instance if dialog was shown or null
     */
    public AlertDialog show(final Context context, final String packageName, final int daysBeforePrompt, final int launchesBeforePrompt, final int resTitle, final int resMessage, final int resRateNow, final int resRemindLater, final int resDontShow) {
    	if (context == null) {
    		throw new RuntimeException("Context in show() may not be null");
    	}
    	mResource_rateApp = resTitle;
    	mResource_pleaseRate = resMessage;
    	mResource_rateNow = resRateNow;
    	mResource_remindLater = resRemindLater;
    	mResource_dontShow = resDontShow;
    	
	mIntentGooglePlay = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_BASE_URI+packageName)); // intent to launch app's details page on Google Play
	if (context.getPackageManager().queryIntentActivities(mIntentGooglePlay, 0).size() <= 0) { // no app available to handle the intent
		return null;
	}

        final SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_CATEGORY, 0);
        final SharedPreferences.Editor editor = prefs.edit();

        if (prefs.getBoolean(PREFERENCE_DONT_SHOW, false)) { // if user opted not to rate the app
        	return null; // do not show anything
        }

        long launch_count = prefs.getLong(PREFERENCE_LAUNCH_COUNT, 0) + 1; // increment launch counter
        editor.putLong(PREFERENCE_LAUNCH_COUNT, launch_count); // write new counter back to preference

        long firstLaunchTime = prefs.getLong(PREFERENCE_FIRST_LAUNCH, 0); // get date of first launch
        if (firstLaunchTime == 0) { // if not set yet
        	firstLaunchTime = System.currentTimeMillis(); // set to current time
            editor.putLong(PREFERENCE_FIRST_LAUNCH, firstLaunchTime);
        }
        
        editor.commit(); // commit preference changes such as incremented launch counter
        
        if (launch_count >= launchesBeforePrompt) { // wait at least x app launches
            if (System.currentTimeMillis() >= (firstLaunchTime + (daysBeforePrompt * DateUtils.DAY_IN_MILLIS))) { // wait at least x days
                try {
                	return showRateDialog(context, editor, packageName, firstLaunchTime);
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

    private void closeDialog(DialogInterface dialog) {
        if (dialog != null) {
        	dialog.dismiss();
        }
    }

    private void setDontShow(SharedPreferences.Editor editor) {
        if (editor != null) {
            editor.putBoolean(PREFERENCE_DONT_SHOW, true);
            editor.commit();
        }
    }

    private void setFirstLaunchTime(SharedPreferences.Editor editor, long time) {
    	if (editor != null) {
    		editor.putLong(PREFERENCE_FIRST_LAUNCH, time);
    		editor.commit();
    	}
    }

    private void rateNowClick(SharedPreferences.Editor editor, DialogInterface dialog, Context context, String packageName) {
    	setDontShow(editor);
        closeDialog(dialog);
        context.startActivity(mIntentGooglePlay);
    }

    private void remindLaterClick(SharedPreferences.Editor editor, DialogInterface dialog, long firstLaunchTime) {
    	setFirstLaunchTime(editor, firstLaunchTime - DateUtils.DAY_IN_MILLIS); // remind again later (but wait at least 24 hours)
    	closeDialog(dialog);
    }

    private void dontShowClick(SharedPreferences.Editor editor, DialogInterface dialog) {
    	setDontShow(editor);
        closeDialog(dialog);
    }

    private AlertDialog showRateDialog(final Context context, final SharedPreferences.Editor editor, final String packageName, final long firstLaunchTime) {
        final AlertDialog.Builder rateDialog = new AlertDialog.Builder(context);
        rateDialog.setTitle(mResource_rateApp > 0 ? context.getString(mResource_rateApp) : DEFAULT_TEXT_RATE_APP);
        rateDialog.setMessage(mResource_pleaseRate > 0 ? context.getString(mResource_pleaseRate) : DEFAULT_TEXT_PLEASE_RATE);
        rateDialog.setNeutralButton(mResource_remindLater > 0 ? context.getString(mResource_remindLater) : DEFAULT_TEXT_REMIND_LATER, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				remindLaterClick(editor, dialog, firstLaunchTime);
			}
		});
        if (Build.VERSION.SDK_INT < 14) {
	        rateDialog.setPositiveButton(mResource_rateNow > 0 ? context.getString(mResource_rateNow) : DEFAULT_TEXT_RATE_NOW, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					rateNowClick(editor, dialog, context, packageName);
				}
			});
	        rateDialog.setNegativeButton(mResource_dontShow > 0 ? context.getString(mResource_dontShow) : DEFAULT_TEXT_DONT_SHOW, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dontShowClick(editor, dialog);
				}
			});
        }
        else {
	        rateDialog.setPositiveButton(mResource_dontShow > 0 ? context.getString(mResource_dontShow) : DEFAULT_TEXT_DONT_SHOW, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dontShowClick(editor, dialog);
				}
			});
	        rateDialog.setNegativeButton(mResource_rateNow > 0 ? context.getString(mResource_rateNow) : DEFAULT_TEXT_RATE_NOW, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					rateNowClick(editor, dialog, context, packageName);
				}
			});
        }
        return rateDialog.show();
    }

}
