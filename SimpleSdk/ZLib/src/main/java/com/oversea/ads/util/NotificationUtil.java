package com.oversea.ads.util;

import java.lang.reflect.Method;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

public class NotificationUtil {
	private static final String TAG = "NotificationUtil";
	private static int ID = 0;
	public static void showNotification(Context context, String title, String content, Bitmap largeIcon, String intentOnClick,String launchType) {
		LogEx.getInstance().d(TAG, "showNotification -");
		LogEx.getInstance().d(TAG, "title == " + title);
		LogEx.getInstance().d(TAG, "content == " + content);
		LogEx.getInstance().d(TAG, "launchType == " + launchType);
		LogEx.getInstance().d(TAG, "intentOnClick == " + intentOnClick);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		try {
			intent = Intent.parseUri(intentOnClick, 0);
		} catch (Exception e) {
		}
		
		PendingIntent pIntent = null;
		if("service".equals(launchType)) {
			pIntent = PendingIntent.getService(context, 0, intent, 0);
			LogEx.getInstance().d(TAG, "Service.pIntent == " + pIntent);
		} else if("broadcast".equals(launchType)) {
			//不运行的包也要拉起来//
			intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
			pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			LogEx.getInstance().d(TAG, "Broadcast.pIntent == " + pIntent);
		} else {
			//"activity"和其它值//
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			pIntent = PendingIntent.getActivity(context, 0, intent, 0);
			LogEx.getInstance().d(TAG, "Activity.pIntent == " + pIntent);
		}

		Notification notification = null;
		Notification.Builder builder = new Notification.Builder(context);
		builder.setContentTitle(title);
		builder.setContentText(content);
		builder.setSmallIcon(context.getApplicationInfo().icon);
		builder.setDefaults(Notification.DEFAULT_ALL);
		builder.setContentIntent(pIntent);
		builder.setAutoCancel(true);

		if (largeIcon != null) {
			try {
				Class<?> cls = Class.forName("android.app.Notification$BigPictureStyle", true, context.getClassLoader());
				Object obj = cls.newInstance();

				Method setBuilder = cls.getMethod("setBuilder", Notification.Builder.class);
				setBuilder.setAccessible(true);

				Method bigPicture = cls.getMethod("bigPicture", Bitmap.class);
				bigPicture.setAccessible(true);

				Method setSummaryText = cls.getMethod("setSummaryText", CharSequence.class);
				setSummaryText.setAccessible(true);

				Method build = cls.getMethod("build");
				build.setAccessible(true);

				setBuilder.invoke(obj, builder);
				bigPicture.invoke(obj, largeIcon);
				setSummaryText.invoke(obj, content);
				notification = (Notification) build.invoke(obj);

			} catch (Exception e) {
				LogEx.getInstance().e("NotificationUtil", "e == " + e);
			}
		}

		if (notification == null) {
			try {
				Method method = Notification.Builder.class.getMethod("build");
				notification = (Notification) method.invoke(builder);
			} catch (Exception e) {
				notification = builder.getNotification();
			}
		}

		notificationManager.notify(ID++, notification);
		LogEx.getInstance().d(TAG, "showNotification +");
	}
}
