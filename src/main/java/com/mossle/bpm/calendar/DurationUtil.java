package com.mossle.bpm.calendar;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.activiti.engine.ActivitiIllegalArgumentException;

import org.joda.time.DateTime;

/*
 org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior.java 在处理task的duedate时使用了DueDateBusinessCalendar
 org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl.java     把BusinessCalendar的实现注册到BusinesssCalendarManager中
 org.activiti.engine.impl.jobexecutor.TimerDeclarationImpl.java             是从BusinessCalendarManager里根据type获取的
 org.activiti.engine.impl.jobexecutor.TimerDeclarationType.java             是对BusinessCalendar的类型进行的注册
 org.activiti.engine.impl.persistence.entity.TimerEntity.java             是从BusinessCalendarManager里获取的CycleBusinessCalendar

 R3/2011/2012 以R开头就是重复多次，R后面是重复次数，如果没有次数，默认无限循环
 2011/2012    从2011开始，到2012截止
 2011/P1D     从2011开始，一天后截止
 P1D/2012     从一天后开始，2012截止
 P1D          从一天后开始，无截止时间

 UserTaskActivityBehavior的场景，如果P1D，就是持续1天，如果2012，就是持续到2012
 TimerDeclarationImpl的场景，可能是duration也可能是dueDate，也可能是cycle，如果是cycle，保存时会把当前时间作为开始时间
 TimerEntity的场景，只有为cycle时，才重新进行计算下一次的触发时间
 */
public class DurationUtil {
    Date start;
    Date end;
    Duration period;
    boolean isRepeat;
    int times;
    DatatypeFactory datatypeFactory;
    private boolean useBusinessTime;
    private AdvancedBusinessCalendar businessCalendar;

    public DurationUtil(String text, AdvancedBusinessCalendar businessCalendar)
            throws Exception {
        this.businessCalendar = businessCalendar;
        this.useBusinessTime = text.indexOf("business") != -1;

        if (useBusinessTime) {
            text = text.substring("business".length()).trim();
        }

        List<String> expressions = Arrays.asList(text.split("/"));
        this.datatypeFactory = DatatypeFactory.newInstance();

        if ((expressions.size() > 3) || expressions.isEmpty()) {
            throw new ActivitiIllegalArgumentException("Cannot parse duration");
        }

        // 获得重复次数
        if (expressions.get(0).startsWith("R")) {
            this.isRepeat = true;
            this.times = (expressions.get(0).length() == 1) ? Integer.MAX_VALUE
                    : Integer.parseInt(expressions.get(0).substring(1));
            expressions = expressions.subList(1, expressions.size());
        }

        // 如果是P开头的，说明是时间段
        if (this.isDuration(expressions.get(0))) {
            // 先计算时间段
            this.period = this.parsePeriod(expressions.get(0));
            // 如果有后半部分，就是结束时间
            // 如果没有，就可能是无限循环
            this.end = (expressions.size() == 1) ? null : this
                    .parseDate(expressions.get(1));
        } else {
            // 如果不是P开头，就是开始时间
            this.start = this.parseDate(expressions.get(0));

            if (this.isDuration(expressions.get(1))) {
                // 如果后半段是P开头的时间段，就解析时间段
                this.period = this.parsePeriod(expressions.get(1));
            } else {
                // 如果后半段是结束时间，时间段就是end-start
                this.end = this.parseDate(expressions.get(1));
                this.period = this.datatypeFactory.newDuration(this.end
                        .getTime() - this.start.getTime());
            }
        }

        // 如果只设置了一个时间段，既没有开始也没有结束时间
        if ((this.start == null) && (this.end == null)) {
            // 就把当前时间设置为开始时间
            this.start = new Date();
        }
    }

    public Date getDateAfter() {
        if (this.isRepeat) {
            return this.getDateAfterRepeat(new Date());
        }

        // TODO: is this correct?
        if (this.end != null) {
            return this.end;
        }

        return this.add(this.start, this.period);
    }

    public int getTimes() {
        return this.times;
    }

    private Date getDateAfterRepeat(Date date) {
        if (this.start != null) {
            Date cur = this.start;

            for (int i = 0; (i < this.times) && !cur.after(date); i++) {
                cur = add(cur, this.period);
            }

            return cur.before(date) ? null : cur;
        }

        Date cur = this.add(this.end, this.period.negate());

        Date next = this.end;

        for (int i = 0; (i < this.times) && cur.after(date); i++) {
            next = cur;
            cur = this.add(cur, this.period.negate());
        }

        return next.before(date) ? null : next;
    }

    private Date add(Date date, Duration duration) {
        if (!useBusinessTime) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            duration.addTo(calendar);

            return calendar.getTime();
        }

        return businessCalendar.add(date, duration, useBusinessTime);
    }

    private Date parseDate(String text) throws Exception {
        Date date = DateTime.parse(text).toDate();

        if (!this.useBusinessTime) {
            return date;
        }

        return businessCalendar.processDate(date, useBusinessTime);
    }

    private Duration parsePeriod(String period) throws Exception {
        return datatypeFactory.newDuration(period);
    }

    private boolean isDuration(String time) {
        return time.startsWith("P");
    }
}
