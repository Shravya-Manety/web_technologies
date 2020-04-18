package com.example.newsapp;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class Utilities {

    public static String getTimeFromDate(DateTime newsItemDate) {
        DateTime now = new DateTime();
        Period period = new Period(newsItemDate, now);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendHours().appendSuffix(" h ago")
                .printZeroNever()
                .toFormatter();

        String elapsed = formatter.print(period);
        if(elapsed.equalsIgnoreCase("")){
            formatter = new PeriodFormatterBuilder()
                    .appendMinutes().appendSuffix(" m ago")
                    .printZeroNever()
                    .toFormatter();
            elapsed = formatter.print(period);
            if(elapsed.equalsIgnoreCase("")){
                formatter = new PeriodFormatterBuilder()
                        .appendSeconds().appendSuffix(" s ago")
                        .printZeroNever()
                        .toFormatter();
                elapsed = formatter.print(period);
            }
            if(elapsed.equalsIgnoreCase(""))
                elapsed = "just now";
        }
        return elapsed;
    }
}
