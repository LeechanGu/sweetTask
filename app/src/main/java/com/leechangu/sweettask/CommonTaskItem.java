package com.leechangu.sweettask;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2015/10/2.
 */
public class CommonTaskItem implements Serializable {

    private static final long serialVersionUID = 8699489847426803789L;
    private String taskContent;
    private TimeBasisEnum timeBasis;
    private TaskTypeEnum taskType;
    private long created;
    private long modified;
    CommonTaskItem(String taskContent, TimeBasisEnum timeBasis, TaskTypeEnum taskType) {
        this.taskContent = taskContent;
        this.timeBasis = timeBasis;
        this.taskType = taskType;
        this.created =  System.currentTimeMillis();
        this.modified =  System.currentTimeMillis();
    }

    CommonTaskItem(String taskContent, TimeBasisEnum timeBasis, TaskTypeEnum taskType,long created, long modified) {
        this.taskContent = taskContent;
        this.timeBasis = timeBasis;
        this.taskType = taskType;
        this.created = created;
        this.modified = modified;
    }
    public void setTaskContent(String taskContent)
    {
        this.taskContent =taskContent ;
        this.modified = System.currentTimeMillis();
    }

    public void setTaskType(TaskTypeEnum taskType)
    {
        this.taskType =taskType ;
        this.modified = System.currentTimeMillis();
    }

    public void setTimeBasisEnum( TimeBasisEnum timeBasis)
    {
        this.timeBasis =timeBasis ;
        this.modified =  System.currentTimeMillis();
    }

    public String getContent()
    {
        return taskContent;
    }

    public TimeBasisEnum getTimeBasisEnum()
    {
        return timeBasis;
    }

    public TaskTypeEnum getTaskType()
    {
        return taskType;
    }

    public long getCreated()
    {
        return created;
    }

    public long getModified()
    {
        return modified;
    }


}
