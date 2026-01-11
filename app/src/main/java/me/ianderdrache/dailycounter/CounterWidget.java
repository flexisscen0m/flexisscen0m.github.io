package me.ianderdrache.dailycounter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.Calendar;

public class CounterWidget extends AppWidgetProvider {

    private static final String ACTION_INCREMENT = "me.ianderdrache.dailycounter.INCREMENT_COUNTER";
    private static final String PREFS_NAME = "DailyCounterPrefs";
    private static final String PREF_COUNTER = "counter_";
    private static final String PREF_LAST_DATE = "last_date_";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            checkAndResetIfNeeded(context, appWidgetId);
            updateWidget(context, appWidgetManager, appWidgetId);
        }
        scheduleMidnightReset(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_INCREMENT.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, CounterWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            for (int appWidgetId : appWidgetIds) {
                checkAndResetIfNeeded(context, appWidgetId);
                incrementCounter(context, appWidgetId);
                updateWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        scheduleMidnightReset(context);
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        int counter = getCounter(context, appWidgetId);
        views.setTextViewText(R.id.counter_text, String.valueOf(counter));

        Intent intent = new Intent(context, CounterWidget.class);
        intent.setAction(ACTION_INCREMENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private int getCounter(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PREF_COUNTER + appWidgetId, 0);
    }

    private void incrementCounter(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int counter = prefs.getInt(PREF_COUNTER + appWidgetId, 0);
        prefs.edit().putInt(PREF_COUNTER + appWidgetId, counter + 1).apply();
    }

    private void checkAndResetIfNeeded(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String today = getCurrentDate();
        String lastDate = prefs.getString(PREF_LAST_DATE + appWidgetId, "");

        if (!today.equals(lastDate)) {
            prefs.edit()
                .putInt(PREF_COUNTER + appWidgetId, 0)
                .putString(PREF_LAST_DATE + appWidgetId, today)
                .apply();
        }
    }

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
    }

    private void scheduleMidnightReset(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MidnightResetReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void resetAllCounters(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, CounterWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String today = getCurrentDateStatic();

        for (int appWidgetId : appWidgetIds) {
            editor.putInt(PREF_COUNTER + appWidgetId, 0);
            editor.putString(PREF_LAST_DATE + appWidgetId, today);
        }
        editor.apply();

        CounterWidget widget = new CounterWidget();
        for (int appWidgetId : appWidgetIds) {
            widget.updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static String getCurrentDateStatic() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
    }
}
