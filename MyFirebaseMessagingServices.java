package com.app.carrozcustomer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.carrozcustomer.Activity.My_Wallet_Activity;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.app.carrozcustomer.Activity.Notification_Activity;
import com.app.carrozcustomer.Activity.Rental_Cap.Local_Customer_Activity;
import com.app.carrozcustomer.Activity.SplashScreenActivity;
import com.app.carrozcustomer.exteas.Constants;
import com.app.carrozcustomer.models.NotificationDetailModel;
import com.app.carrozcustomer.pref.Config;
import com.app.carrozcustomer.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MyFirebaseMessagingServices extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static int count = 0;
    String NOTIFICATION_CHANNEL_NAME = "com.sbsys.app";
    String NOTIFICATION_CHANNEL_ID = "123";
    public String title, Click_Action;
    private int noticount = 0;
    String ride_type = "0", cid;
    private MyApplication application;
    private SharedPreferences preferences;
    Bitmap bitmap;


    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        application = (MyApplication) getApplicationContext();
        SharedPreferences preferences1 = getSharedPreferences("mid", 0);
        preferences = application.getSharedPreferences();
        cid = preferences1.getString("customer_id", "");


        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            try {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                JSONObject object = new JSONObject(remoteMessage.getData());
                String val1 = object.getString("message");
                String val2 = object.getString("body");
                String val3 = object.getString("title");
                String val4 = object.getString("largeIcon");
                Log.d("nfjsdfjnsdf", val1);
                Log.d("nfjsdfjnsdf", val2);
                Log.d("nfjsdfjnsdf", val3);
                Log.d("nfjsdfjnsdf", val4);

                bitmap = getBitmapfromUrl(val4);
            } catch (Exception e) {

            }
        }

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            try {
//
//                JSONObject object = new JSONObject(remoteMessage.getData());
//                String val1 = object.getString("message");
//                String val2 = object.getString("body");
//                String val3 = object.getString("title");
//                String val4 = object.getString("largeIcon");
//                Log.d("nfjsdfjnsdf", val1);
//                Log.d("nfjsdfjnsdf", val2);
//                Log.d("nfjsdfjnsdf", val3);
//                Log.d("nfjsdfjnsdf", val4);
//
//                bitmap = getBitmapfromUrl(val4);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getLink().toString());
//        }

//      Log.e(TAG, "Data Payload:1 " + remoteMessage.getNotification().getBody());
        Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
        Log.e(TAG, "Data Payload111111: " + remoteMessage.getData());


        if (remoteMessage.getData() != null) {
            Log.e(TAG, "Data Payload2222222222: " + remoteMessage.getData().toString());

            if (remoteMessage.getData().get("ride_type") != null) {
                ride_type = remoteMessage.getData().get("ride_type");
                Log.e(TAG, "log " + remoteMessage.getData().toString() + " \n" + ride_type);
            }

            if (Objects.requireNonNull(ride_type).equalsIgnoreCase("outstation")) {
                Click_Action = remoteMessage.getData().get("icon");
                sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
                Log.e(TAG, "1 " + remoteMessage.getData().toString() + " \n" + ride_type);
            } else {
//                Log.e(TAG, "2 " + remoteMessage.getData().toString() + " \n" + ride_type);
                NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                String CHANNEL_ID = getResources().getString(R.string.app_name);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mNotificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID);
                } else {
                    mNotificationManager.cancelAll();
                }

                mNotificationManager.cancel(0);

                try {
                    String type = remoteMessage.getData().get("type");
                    Log.d("dafnjdsbvf==========", type);
                    String title = remoteMessage.getData().get("title");
                    Log.d("dafnjdsbvf==========", title);
                    String message = remoteMessage.getData().get("message");
                    Log.d("dafnjdsbvf==========", message);
                    Log.d("dafnjdsbvf==========", Config.getIsRegister()+"");


                    if (type.equalsIgnoreCase("custom") && Config.getIsRegister()) {
                            Log.d("sdfsdfsdf", "ddfdsfsdffsdf");
                            sendNotificationCustomDriver(title, message, bitmap);
                    }

                    if (type.equalsIgnoreCase(Constants.FLD_ACCEPT_BOOKING) && !Config.getCustomerId().equalsIgnoreCase("0")) {

                        Gson gson = new Gson();
                        NotificationDetailModel notificationDataModel = gson.fromJson(message, NotificationDetailModel.class);
                        message = notificationDataModel.getMessage().toString();
                        Log.e("Notification1111===>", " " + type + " " + message + " " + notificationDataModel.getMobile_number() + "," + notificationDataModel.getMessage().toString());
                        if (Local_Customer_Activity.isHome) {
                            //  Click_Action = remoteMessage.getData().get("type");
                            Local_Customer_Activity.rideRequestInterface.acceptRideRequest(notificationDataModel);
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                       /* Intent intent = new Intent(this, NewHomeActivity.class);
                        intent.putExtra(Constants.FLD_NOTY_TYPE, type);
                        intent.putExtra(Constants.FLD_ACCEPT_BOOKING,notificationDataModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        this.startActivity(intent);*/
                        } else {
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        }

                    } else if (type.equalsIgnoreCase(Constants.FLD_DRIVER_ARRIVED) && Integer.parseInt(cid) != 0) {
                        Gson gson = new Gson();
                        NotificationDetailModel notificationDataModel = gson.fromJson(message, NotificationDetailModel.class);
                        message = notificationDataModel.getMessage().toString();
                        Log.d("fdfkjfngfsvg", message);
                        Log.e("Notification4444===>", " " + type + " " + message + " " + notificationDataModel.getMobile_number());

                        if (Local_Customer_Activity.class != null && Local_Customer_Activity.isHome) {
                            Local_Customer_Activity.rideRequestInterface.driverArrivedRequest(notificationDataModel);
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        /*Intent intent = new Intent(this, NewHomeActivity.class);
                        intent.putExtra(Constants.FLD_NOTY_TYPE, type);
                        intent.putExtra(Constants.FLD_DRIVER_ARRIVED,notificationDataModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        this.startActivity(intent);*/
                        } else {
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        }

                    } else if (type.equalsIgnoreCase(Constants.FLD_START_RIDE) && Integer.parseInt(cid) != 0) {
                        Gson gson = new Gson();
                        NotificationDetailModel notificationDataModel = gson.fromJson(message, NotificationDetailModel.class);
                        message = notificationDataModel.getMessage().toString();
                        if (Local_Customer_Activity.class != null && Local_Customer_Activity.isHome) {
                            Local_Customer_Activity.rideRequestInterface.startRideRequest(notificationDataModel);
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        /*Intent intent = new Intent(this, NewHomeActivity.class);
                        intent.putExtra(Constants.FLD_NOTY_TYPE, type);
                        intent.putExtra(Constants.FLD_START_RIDE,notificationDataModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        this.startActivity(intent);*/
                        } else {
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        }

                    } else if (type.equalsIgnoreCase(Constants.FLD_END_RIDE) && Integer.parseInt(cid) != 0) {
                        Gson gson = new Gson();
                        NotificationDetailModel notificationDataModel = gson.fromJson(message, NotificationDetailModel.class);
                        message = notificationDataModel.getMessage().toString();
                        if (Local_Customer_Activity.class != null && Local_Customer_Activity.isHome) {
                            Local_Customer_Activity.rideRequestInterface.finishRideRequest(notificationDataModel);
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);

                        /*Intent intent = new Intent(this, NewHomeActivity.class);
                        intent.putExtra(Constants.FLD_NOTY_TYPE, type);
                        intent.putExtra(Constants.FLD_START_RIDE,notificationDataModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        this.startActivity(intent);*/

                        } else {
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        }

                    } else if (type.equalsIgnoreCase(Constants.FLD_FINISH_RIDE) && Integer.parseInt(cid) != 0) {
                        Gson gson = new Gson();
                        Log.e("GSON", " " + gson);

                        NotificationDetailModel notificationDataModel = gson.fromJson(message, NotificationDetailModel.class);
                        message = notificationDataModel.getMessage().toString();
                        if (Local_Customer_Activity.class != null && Local_Customer_Activity.isHome) {
                            Local_Customer_Activity.rideRequestInterface.finishRideRequest(notificationDataModel);
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);

                            notifManager.cancelAll();
                        /*Intent intent = new Intent(this, NewHomeActivity.class);
                        intent.pumessagera(Constants.FLD_NOTY_TYPE, type);
                        intent.pumessagera(Constants.FLD_START_RIDE,notificationDataModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        this.startActivity(intent);*/
                        } /*else if (RideFinishActivity.class != null && RideFinishActivity.isFinish) {
                            RideFinishActivity.rideRequestInterface.finishRideRequest(notificationDataModel);

                        }*/ else {
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        }

                    } else if (type.equalsIgnoreCase(Constants.FLD_CANCLE_BOOKING_AUTO) && Integer.parseInt(cid) != 0) {
                        Gson gson = new Gson();
                        NotificationDetailModel notificationDataModel = gson.fromJson(message, NotificationDetailModel.class);
                        message = notificationDataModel.getMessage().toString();
                        if (Local_Customer_Activity.class != null && Local_Customer_Activity.isHome) {
                            Local_Customer_Activity.rideRequestInterface.cancelRideRequest(message);
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        /*Intent intent = new Intent(this, NewHomeActivity.class);
                        intent.pumessagera(Constants.FLD_NOTY_TYPE, type);
                        intent.pumessagera(Constants.FLD_START_RIDE,notificationDataModel);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        this.startActivity(intent);*/
                        } else {
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        }

                    } else if (type.equalsIgnoreCase("device_signin")) {
                        Gson gson = new Gson();
                        NotificationDetailModel notificationDataModel = gson.fromJson(message, NotificationDetailModel.class);
                        message = notificationDataModel.getMessage().toString();
                        if (Local_Customer_Activity.class != null && Local_Customer_Activity.isHome) {
                            Local_Customer_Activity.rideRequestInterface.logoutDevice();
                       /* Intent intent = new Intent(this, HomeActivity.class);
                        intent.pumessagera(Constants.FLD_NOTY_TYPE, type);
                        intent.pumessagera(Constants.FLD_NOTY_CANCELRIDE, message);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        this.startActivity(intent);*/
                        } else {
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        }

                    } else if (type.equalsIgnoreCase("custom") && Config.getIsRegister()) {
                        if (Local_Customer_Activity.class != null && Local_Customer_Activity.isHome) {
                            Log.d("sdfsdfsdf", "ddfdsfsdffsdf");
                            sendNotificationCustomDriver(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"),  bitmap);
                        }

                    } else if (type.equalsIgnoreCase("addRideWallet") && Config.getIsRegister()) {
                        Gson gson = new Gson();
                        NotificationDetailModel notificationDataModel = gson.fromJson(message, NotificationDetailModel.class);
                        message = notificationDataModel.getMessage().toString();
                        if (Local_Customer_Activity.class != null && Local_Customer_Activity.isHome) {
                            sendNotificationCustomWallet(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), type, notificationDataModel);
                        }

                    } else if (type.equalsIgnoreCase("AutoCancelBooking") && Config.getIsRegister()) {
                        Gson gson = new Gson();
                        NotificationDetailModel notificationDataModel = gson.fromJson(message, NotificationDetailModel.class);
                        message = notificationDataModel.getMessage().toString();
                        if (Local_Customer_Activity.class != null && Local_Customer_Activity.isHome) {
                            NotificationUtils.creatMessageNotification(this, notificationDataModel, title, message, type);
                        }

                    } else {
                        // NotificationUtils.creatShareNotification(this, title, message, type, message);
                    }

                } catch (Exception e) {

                    Log.e(TAG, "Exception: " + e.getMessage());
                }

            }
        }
    }

    private void sendNotification(String messageTitle, String messageBody) {
//        Toast.makemessage( getApplicationConmessage(),"Testing GCM",Toast.LENGTH_SHORT ).show();
        Intent resultIntent = new Intent(this, SplashScreenActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SplashScreenActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        resultIntent.putExtra("msg", Click_Action);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.N ||
                android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            long[] pattern = {0, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000};
            Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 1000 milliseconds
            //deprecated in API 26
            v1.vibrate(pattern, -1);


            Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final Ringtone ringtone = RingtoneManager.getRingtone(this, notificationSoundURI);
            ringtone.play();
//            MediaPlayer player = MediaPlayer.create(this, notificationSoundURI);
//            player.setAudioStreamType(AudioManager.STREAM_ALARM);
//            player.setLooping(true);
//            player.start();
            Timer mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (!ringtone.isPlaying()) {
                        noticount++;
                        if (noticount > 10) {
                            ringtone.stop();
                        } else {
                            Log.e(TAG, "run==" + noticount);
                            ringtone.play();
                        }
                    }
                }
            }, 1000 * 1, 1000 * 1);

            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(notificationSoundURI)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{0, 3000, 500, 2000, 500, 2000, 500, 2000, 500, 2000})
                    .setContentIntent(resultPendingIntent);

//            mNotificationBuilder. |= Notification.FLAG_INSISTENT;

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, mNotificationBuilder.build());

        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Uri alarmSound1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            final Ringtone ringtonenew = RingtoneManager.getRingtone(getApplicationContext(), alarmSound1);
            ringtonenew.play();
//            MediaPlayer player = MediaPlayer.create(this, notificationSoundURI);
//            player.setAudioStreamType(AudioManager.STREAM_ALARM);
//            player.setLooping(true);
//            player.start();
            Timer mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (!ringtonenew.isPlaying()) {
                        noticount++;
                        if (noticount > 10) {
                            ringtonenew.stop();
                        } else {
                            ringtonenew.play();
                        }
                    }
                }
            }, 1000 * 1, 1000 * 1);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                mChannel.setDescription(messageBody);
                mChannel.setName(messageTitle);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setSound(null, null);
                mChannel.setVibrationPattern(new long[]{0, 3000, 500, 2000, 500, 2000, 500, 2000, 500, 2000});
                mChannel.setShowBadge(true);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(mChannel);
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setSmallIcon(R.mipmap.app_icon)
                    .setVibrate(new long[]{0, 3000, 500, 2000, 500, 2000, 500, 2000, 500, 2000})
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setSound(alarmSound1)
                    .setColor(getResources().getColor(android.R.color.holo_red_dark));

            if (notificationManager != null) {

                notificationManager.notify(1, builder.build());
            }

        }
    }

    private void sendNotificationCustomDriver(String messageTitle, String messageBody,  Bitmap laricon) {
        Log.d("fvfvfdbdf", "ffvsfvsrv");
        Log.d("fvfvfdbdf=>", laricon.toString());

        Intent intent = new Intent(this, Notification_Activity.class);
        intent.putExtra("customer_id", Config.getUserid());
        intent.putExtra("screenType", "Driver");
        intent.putExtra("type", "notificationDriver");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.N ||
                android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            long[] pattern = {0, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000};
            Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 1000 milliseconds
            //deprecated in API 26
            v1.vibrate(pattern, -1);

            Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final Ringtone ringtone = RingtoneManager.getRingtone(this, notificationSoundURI);
            ringtone.play();
//            MediaPlayer player = MediaPlayer.create(this, notificationSoundURI);
//            player.setAudioStreamType(AudioManager.STREAM_ALARM);
//            player.setLooping(true);
//            player.start();
            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentTitle(messageTitle)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(laricon).setBigContentTitle("Carroz"))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(notificationSoundURI)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{0, 3000, 500, 2000, 500, 2000, 500, 2000, 500, 2000})
                    .setContentIntent(pendingIntent)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);

//            mNotificationBuilder. |= Notification.FLAG_INSISTENT;

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, mNotificationBuilder.build());

        } else {
            Log.d("ddsvfsvfsv", "dcdcd");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Uri alarmSound1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            final Ringtone ringtonenew = RingtoneManager.getRingtone(getApplicationContext(), alarmSound1);
            ringtonenew.play();
//            MediaPlayer player = MediaPlayer.create(this, notificationSoundURI);
//            player.setAudioStreamType(AudioManager.STREAM_ALARM);
//            player.setLooping(true);
//            player.start();

            Bitmap largeBitmap = null;
            try {
                largeBitmap = Glide.with(this)
                        .asBitmap()
                        .load(laricon)
                        .into(100, 100) // Width and height
                        .get();
            } catch (Exception ex) {
                Log.d("dsfmksdfsdfv", ex.getMessage());
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                mChannel.setDescription(messageBody);
                mChannel.setName(messageTitle);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setSound(null, null);
                mChannel.setVibrationPattern(new long[]{0, 3000, 500, 2000, 500, 2000, 500, 2000, 500, 2000});
                mChannel.setShowBadge(false);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(mChannel);
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody).setStyle(new NotificationCompat.BigPictureStyle().bigPicture(largeBitmap))
                    .setSmallIcon(R.mipmap.app_icon)
                    .setVibrate(new long[]{0, 3000, 500, 2000, 500, 2000, 500, 2000, 500, 2000})
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSound(alarmSound1).setLargeIcon(laricon)
                    .setColor(getResources().getColor(android.R.color.holo_red_dark))
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);

            if (notificationManager != null) {

                notificationManager.notify(1, builder.build());
            }

        }
    }

    private void sendNotificationCustomWallet(String messageTitle, String messageBody, String notificationType, NotificationDetailModel
            notificationDataModel) {

        Intent intent = new Intent(this, My_Wallet_Activity.class);
        intent.putExtra("customer_id", Config.getUserid());
        intent.putExtra("screenType", "Wallet");
        intent.putExtra("minAmount", notificationDataModel.getMin_amount());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.N ||
                android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            long[] pattern = {0, 2000, 500, 2000, 500, 2000, 500, 2000, 500, 2000};
            Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 1000 milliseconds
            //deprecated in API 26
            v1.vibrate(pattern, -1);


            Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            final Ringtone ringtone = RingtoneManager.getRingtone(this, notificationSoundURI);
            ringtone.play();
//            MediaPlayer player = MediaPlayer.create(this, notificationSoundURI);
//            player.setAudioStreamType(AudioManager.STREAM_ALARM);
//            player.setLooping(true);
//            player.start();
            Timer mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (!ringtone.isPlaying()) {
                        noticount++;
                        if (noticount > 10) {
                            ringtone.stop();
                        } else {
                            Log.e(TAG, "run==" + noticount);
                            ringtone.play();
                        }
                    }
                }
            }, 1000 * 1, 1000 * 1);

            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(notificationSoundURI)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{0, 3000, 500, 2000, 500, 2000, 500, 2000, 500, 2000})
                    .setContentIntent(pendingIntent);

//            mNotificationBuilder. |= Notification.FLAG_INSISTENT;

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, mNotificationBuilder.build());

        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Uri alarmSound1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            final Ringtone ringtonenew = RingtoneManager.getRingtone(getApplicationContext(), alarmSound1);
            ringtonenew.play();
//            MediaPlayer player = MediaPlayer.create(this, notificationSoundURI);
//            player.setAudioStreamType(AudioManager.STREAM_ALARM);
//            player.setLooping(true);
//            player.start();
            Timer mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if (!ringtonenew.isPlaying()) {
                        noticount++;
                        if (noticount > 10) {
                            ringtonenew.stop();
                        } else {
                            ringtonenew.play();
                        }
                    }
                }
            }, 1000 * 1, 1000 * 1);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                mChannel.setDescription(messageBody);
                mChannel.setName(messageTitle);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setSound(null, null);
                mChannel.setVibrationPattern(new long[]{0, 3000, 500, 2000, 500, 2000, 500, 2000, 500, 2000});
                mChannel.setShowBadge(true);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(mChannel);
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setSmallIcon(R.mipmap.app_icon)
                    .setVibrate(new long[]{0, 3000, 500, 2000, 500, 2000, 500, 2000, 500, 2000})
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSound(alarmSound1)
                    .setColor(getResources().getColor(android.R.color.holo_red_dark));

            if (notificationManager != null) {

                notificationManager.notify(1, builder.build());
            }

        }
    }
}
