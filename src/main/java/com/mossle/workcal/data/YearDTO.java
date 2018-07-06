package com.mossle.workcal.data;

import java.util.ArrayList;
import java.util.List;

public class YearDTO {
    private int year;
    private List<HolidayDTO> holidays = new ArrayList<HolidayDTO>();

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<HolidayDTO> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<HolidayDTO> holidays) {
        this.holidays = holidays;
    }
}
