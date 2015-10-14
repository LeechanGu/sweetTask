package com.leechangu.sweettask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2015/10/2.
 */
public class TaskArrayAdapter extends ArrayAdapter<CommonTaskItem> {
    public TaskArrayAdapter(Context context, int resource, List<CommonTaskItem> objects) {
        super(context, R.layout.custom_task_row, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.custom_task_row, parent, false);

        CommonTaskItem taskItem = getItem(position);
        TextView taskContentTextView = (TextView)customView.findViewById(R.id.taskContentTextView);
        taskContentTextView.setText(taskItem.getContent());

        TextView timeBasisTextView = (TextView)customView.findViewById(R.id.timeBasisTextView);
        String timeBasisString = taskItem.getTimeBasisEnum().toString();
        timeBasisTextView.setText(timeBasisString );


        ProgressBar progressBar = (ProgressBar)customView.findViewById(R.id.progressBar);
        int result[] = getMaxAndValueForProgress(taskItem.getTimeBasisEnum());
        progressBar.setMax(result[0]);
        progressBar.setProgress(result[1]);

        TextView timeLeftTextView = (TextView)customView.findViewById(R.id.timeLeftTextView);
        String remainingTime = getRemainingTime(taskItem.getTimeBasisEnum());
        timeLeftTextView.setText(remainingTime);

        return customView;
    }
    public int[] getMaxAndValueForProgress(TimeBasisEnum timeBasis)
    {
        int result[] = new int[2];
        Calendar now= Calendar.getInstance();
        now.setFirstDayOfWeek(Calendar.MONDAY);
        int max=0,progress=0;
        switch (timeBasis)
        {
            case DAILY:
                max = 24;
                progress = now.get(Calendar.HOUR_OF_DAY);
                break;
            case WEEKLY:
                max = 7;
                progress = now.get(Calendar.DAY_OF_WEEK);
                break;
            case MONTHLY:
                max = now.getActualMaximum(Calendar.DAY_OF_MONTH);
                progress = now.get(Calendar.DAY_OF_MONTH);
                break;
        }
        result[0] = max;
        result[1] = progress;
        return result;
    }

    public String getRemainingTime( TimeBasisEnum timeBasis)
    {
        String result = "unknown";

        Calendar now= Calendar.getInstance();
        Calendar due=Calendar.getInstance();
        due.setFirstDayOfWeek(Calendar.MONDAY);
        switch (timeBasis)
        {
            case DAILY:
                due.set(Calendar.HOUR_OF_DAY, 0);
                due.set(Calendar.MINUTE, 0);
                due.set(Calendar.SECOND, 0);
                due.add(Calendar.DATE, 1);
                break;
            case WEEKLY:
                due.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                due.set(Calendar.HOUR_OF_DAY, 0);
                due.set(Calendar.MINUTE, 0);
                due.set(Calendar.SECOND, 0);
                due.add(Calendar.DATE, 7);
                break;
            case MONTHLY:
                due.set(Calendar.DAY_OF_MONTH, 1);
                due.set(Calendar.HOUR_OF_DAY, 0);
                due.set(Calendar.MINUTE, 0);
                due.set(Calendar.SECOND, 0);
                due.add(Calendar.MONTH, 1);
                break;
        }
        long diff = due.getTimeInMillis() - now.getTimeInMillis();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays>0)
        {
            result =  diffDays + " Day";
        }else if (diffHours>0)
        {
            result =  diffHours + " Hour";
        }
        else if (diffMinutes>0)
        {
            result =  diffMinutes + " Min";
        }
        else if (diffSeconds>0)
        {
            result =  "<1 min";
        }
        return result;
    }
}
