package com.necer.entity;

import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * Created by lml
 */
public class NDate implements Serializable {


    public LocalDate localDate;//公历日期
    public Lunar lunar;        //农历日期
    public String solarHoliday;//公历节日
    public String lunarHoliday;//农历节日
    public String solarTerm;//节气


    public NDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public NDate() {
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NDate) {
            NDate date = (NDate) obj;
            return localDate.equals(date.localDate);
        } else {
            return false;
        }
    }
}
