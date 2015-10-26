package com.mossle.org.support;

import com.mossle.org.persistence.domain.JobInfo;

public class JobInfoDTO {
    private JobInfo jobInfo;
    private boolean printJobInfo;
    private int count;

    public JobInfoDTO(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
        this.printJobInfo = true;
        this.count = 1;
    }

    public JobInfo getJobInfo() {
        return jobInfo;
    }

    public boolean isPrintJobInfo() {
        return this.printJobInfo;
    }

    public int getCount() {
        return this.count;
    }

    public boolean isSameJobTitle(JobInfo jobInfo) {
        boolean result = this.getJobInfo().getJobTitle().getId()
                .equals(jobInfo.getJobTitle().getId());

        if (result) {
            this.increment();
        }

        return result;
    }

    public void increment() {
        this.printJobInfo = false;
        this.count++;
    }
}
