package com.mossle.internal.sequence.component;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import com.mossle.internal.sequence.persistence.domain.SequenceInfo;
import com.mossle.internal.sequence.persistence.manager.SequenceInfoManager;
import com.mossle.internal.sequence.support.SequenceHelper;

import org.hibernate.LockMode;
import org.hibernate.Query;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseSequenceHelper implements SequenceHelper {
    private SequenceInfoManager sequenceInfoManager;

    @Transactional
    public String process(String code, Date date) {
        if (code == null) {
            throw new IllegalArgumentException("code is null");
        }

        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }

        code = code.trim().toUpperCase();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date targetDate = calendar.getTime();
        String hql = "from SequenceInfo where code=?";
        Query query = sequenceInfoManager.createQuery(hql, code);
        query.setLockMode("SequenceInfo", LockMode.WRITE);

        SequenceInfo sequenceInfo = (SequenceInfo) query.setFirstResult(0)
                .setMaxResults(1).uniqueResult();

        int sequence = 0;

        if (sequenceInfo == null) {
            sequenceInfo = new SequenceInfo();
            sequenceInfo.setCode(code);
            sequenceInfo.setValue(1);
            sequenceInfo.setUpdateDate(targetDate);
        } else {
            Date updateDate = sequenceInfo.getUpdateDate();

            if (updateDate == null) {
                sequenceInfo.setUpdateDate(targetDate);
                sequenceInfo.setValue(1);
            } else if (updateDate.before(targetDate)) {
                sequenceInfo.setUpdateDate(targetDate);
                sequenceInfo.setValue(1);
            } else {
                sequenceInfo.setValue(sequenceInfo.getValue() + 1);
            }
        }

        sequenceInfoManager.save(sequenceInfo);
        sequence = sequenceInfo.getValue();

        if (sequence >= 100000) {
            throw new IllegalStateException(sequence
                    + " is larger than 100000, cannot process it.");
        }

        // sequence += 100000;
        // String code = Integer.toString(sequence).substring(1);
        // return code;
        return Integer.toString(sequence);
    }

    @Resource
    public void setSequenceInfoManager(SequenceInfoManager sequenceInfoManager) {
        this.sequenceInfoManager = sequenceInfoManager;
    }
}
