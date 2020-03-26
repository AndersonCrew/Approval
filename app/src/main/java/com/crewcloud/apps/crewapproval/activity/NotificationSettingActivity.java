package com.crewcloud.apps.crewapproval.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.R;
import com.crewcloud.apps.crewapproval.util.HttpRequest;
import com.crewcloud.apps.crewapproval.util.PreferenceUtilities;


import java.util.Calendar;

public class NotificationSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private View viewGetNotify, viewSound, viewVibrate, viewNotifiTime;
    private TextView tvGetNotify, tvSound, tvVibrate, tvTime, tvStartHour, tvEndHour;
    private Switch switchNotify, switchSound, switchVibrate, switchTime;
    private PreferenceUtilities mPref;
    private String notificationOptions = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_page);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        init();

    }

    public void init() {
        mPref = CrewCloudApplication.getInstance().getPreferenceUtilities();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewGetNotify = findViewById(R.id.view_getnotify);
        viewSound = findViewById(R.id.view_sound);
        viewVibrate = findViewById(R.id.view_vibrate);
        viewNotifiTime = findViewById(R.id.view_notifi_time);
        tvGetNotify = viewGetNotify.findViewById(R.id.tv_switch);
        tvSound = viewSound.findViewById(R.id.tv_switch);
        tvVibrate = viewVibrate.findViewById(R.id.tv_switch);
        tvTime = viewNotifiTime.findViewById(R.id.tv_switch);
        tvStartHour = findViewById(R.id.tv_starthour);
        tvEndHour = findViewById(R.id.tv_endhour);
        String _start = mPref.getSTART_TIME();
        String _end = mPref.getEND_TIME();
        if (_start.length() == 0) {
            _start = "AM 08:00";
            mPref.setSTART_TIME(_start);
        }
        if (_end.length() == 0) {
            _end = "PM 06:00";
            mPref.setEND_TIME(_end);
        }
        tvStartHour.setText(_start);
        tvEndHour.setText(_end);
        tvStartHour.setOnClickListener(this);
        tvEndHour.setOnClickListener(this);
        tvGetNotify.setText(getResources().getString(R.string.getnotify));
        tvSound.setText(getResources().getString(R.string.sound));
        tvVibrate.setText(getResources().getString(R.string.vibrate));
        tvTime.setText(getResources().getString(R.string.notification_time_long));
        switchNotify = viewGetNotify.findViewById(R.id.switch1);
        switchSound = viewSound.findViewById(R.id.switch1);
        switchVibrate = viewVibrate.findViewById(R.id.switch1);
        switchTime = viewNotifiTime.findViewById(R.id.switch1);
        switchNotify.setOnCheckedChangeListener(this);
        switchNotify.setChecked(mPref.getNOTIFI_MAIL());
        switchSound.setChecked(mPref.getNOTIFI_SOUND());
        switchVibrate.setChecked(mPref.getNOTIFI_VIBRATE());
        switchTime.setChecked(mPref.getNOTIFI_TIME());
        setEnable(switchNotify.isChecked());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setEnable(isChecked);
    }

    public void setEnable(boolean isChecked) {
        switchSound.setEnabled(isChecked);
        switchVibrate.setEnabled(isChecked);
        switchTime.setEnabled(isChecked);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPref.setNOTIFI_MAIL(switchNotify.isChecked());
        mPref.setNOTIFI_SOUND(switchSound.isChecked());
        mPref.setNOTIFI_VIBRATE(switchVibrate.isChecked());
        mPref.setNOTIFI_TIME(switchTime.isChecked());
        mPref.setSTART_TIME(tvStartHour.getText().toString().trim());
        mPref.setEND_TIME(tvEndHour.getText().toString().trim());
        notificationOptions = "{" +
                "\"enabled\": " + switchNotify.isChecked() + "," +
                "\"sound\": " + switchSound.isChecked() + "," +
                "\"vibrate\": " + switchVibrate.isChecked() + "," +
                "\"notitime\": " + switchTime.isChecked() + "," +
                "\"starttime\": \"" + getFullHour(tvStartHour) + "\"," +
                "\"endtime\": \"" + getFullHour(tvEndHour) + "\"" + "}";
        notificationOptions = notificationOptions.trim();
        HttpRequest.getInstance().updateAndroidDevice(mPref.getGCMregistrationid(), notificationOptions);
    }

    @Override
    public void onClick(View v) {
        if (v == tvStartHour) {
            ShowTimerDialog(tvStartHour, tvEndHour);
        } else if (v == tvEndHour) {
            ShowTimerDialogEnd(tvEndHour, tvStartHour);
        }
    }

    public void ShowTimerDialog(final TextView tv, final TextView tv2) {
        Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int h = getHour(tv2);
                        int m = getMinute(tv2);
                        if (hourOfDay < h) {
                            tv.setText(getFullHour(hourOfDay, minute));
                        } else if (hourOfDay > h) {
                            tv.setText(getFullHour(hourOfDay, minute));
                            tv2.setText(getFullHour(hourOfDay, minute));
                        } else {
                            if (minute < m) {
                                tv.setText(getFullHour(hourOfDay, minute));
                            } else {
                                tv.setText(getFullHour(hourOfDay, minute));
                                tv2.setText(getFullHour(hourOfDay, minute));
                            }
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void ShowTimerDialogEnd(final TextView tv, final TextView tv2) {
        Calendar calendar = Calendar.getInstance();
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int h = getHour(tv2);
                        int m = getMinute(tv2);
                        if (hourOfDay > h) {
                            tv.setText(getFullHour(hourOfDay, minute));
                        } else if (hourOfDay < h) {
                            tv.setText(getFullHour(hourOfDay, minute));
                            tv2.setText(getFullHour(hourOfDay, minute));
                        } else {
                            if (minute > m) {
                                tv.setText(getFullHour(hourOfDay, minute));
                            } else {
                                tv.setText(getFullHour(hourOfDay, minute));
                                tv2.setText(getFullHour(hourOfDay, minute));
                            }
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public String getFullHour(int hour, int minute) {
        String str_h = "", str_m = "";
        String AM = "AM";
        if (hour > 12) {
            AM = "PM";
            hour -= 12;
        } else {
            AM = "AM";
        }
        if (hour < 10) str_h = "0" + hour;
        else str_h = "" + hour;
        if (minute < 10) str_m = "0" + minute;
        else str_m = "" + minute;
        String text = AM + " " + str_h + ":" + str_m;
        return text;
    }

    public int getHour(TextView tv) {
        int h = 0;
        String[] str = tv.getText().toString().split(" ");
        h = Integer.parseInt(str[1].split(":")[0]);
        if (str[0].equalsIgnoreCase("PM")) h += 12;
        return h;
    }

    public int getMinute(TextView tv) {
        int h = 0;
        String[] str = tv.getText().toString().split(" ");
        h = Integer.parseInt(str[1].split(":")[1]);
        return h;
    }

    public String getFullHour(TextView tv) {
        String hour = "", minute = "";
        int h = getHour(tv);
        int m = getMinute(tv);
        if (h < 10) hour = "0" + h;
        else hour = "" + h;
        if (m < 10) minute = "0" + m;
        else minute = "" + m;
        String text = hour + ":" + minute;
        return text;
    }
}
