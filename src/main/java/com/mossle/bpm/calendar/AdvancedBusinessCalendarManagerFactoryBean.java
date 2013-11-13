package com.mossle.bpm.calendar;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.calendar.BusinessCalendar;
import org.activiti.engine.impl.calendar.BusinessCalendarManager;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class AdvancedBusinessCalendarManagerFactoryBean implements
        InitializingBean, FactoryBean<AdvancedBusinessCalendarManager> {
    private AdvancedBusinessCalendarManager advancedBusinessCalendarManager;
    private String sunday;
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String holiday;

    public AdvancedBusinessCalendarManager getObject() {
        return advancedBusinessCalendarManager;
    }

    public Class<AdvancedBusinessCalendarManager> getObjectType() {
        return AdvancedBusinessCalendarManager.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        this.advancedBusinessCalendarManager = new AdvancedBusinessCalendarManager();
        this.addBusinessCalendar(new DueDateBusinessCalendar());
        this.addBusinessCalendar(new DurationBusinessCalendar());
        this.addBusinessCalendar(new CycleBusinessCalendar());
    }

    public void addBusinessCalendar(AdvancedBusinessCalendar businessCalendar) {
        businessCalendar.setSunday(sunday);
        businessCalendar.setMonday(monday);
        businessCalendar.setTuesday(tuesday);
        businessCalendar.setWednesday(wednesday);
        businessCalendar.setThursday(thursday);
        businessCalendar.setFriday(friday);
        businessCalendar.setSaturday(saturday);
        businessCalendar.setHoliday(holiday);
        businessCalendar.init();
        this.advancedBusinessCalendarManager
                .addBusinessCalendar(businessCalendar);
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }
}
