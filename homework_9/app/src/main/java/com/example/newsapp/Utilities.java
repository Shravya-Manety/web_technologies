package com.example.newsapp;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class Utilities {

    public static String EXTRA_NEWS_ARTICLE_ID = "com.example.newsapp.articleid";
    public static String PARENT_CLASS_NAME = "parent";
    public static int TIMEOUT_MS=10000;        //10 second

    public static String getArticleDate(String date, String type){
        DateTime dt = new DateTime(date);
        DateTimeFormatter fmt;
        if(type.equalsIgnoreCase("detailed")){
            fmt = DateTimeFormat.forPattern("dd MMM yyyy");
        }
        else{
            fmt = DateTimeFormat.forPattern("dd MMM");
        }
        String formattedDate = fmt.print(dt);
        return formattedDate;
    }

    public static String getTimeFromDate(String newsDate) {

        String elapsed="";

        DateTimeZone dateTimeZone = DateTimeZone.forID("America/Los_Angeles");
        DateTime newsItemDate = new DateTime(newsDate, dateTimeZone);
        DateTime now = new DateTime(dateTimeZone);
        Period period = new Period(newsItemDate, now);
        System.out.println("period:"+period);
        elapsed = String.valueOf(Days.daysBetween(newsItemDate.toLocalDate(), now.toLocalDate()).getDays()) + "d ago";
        if(elapsed.equalsIgnoreCase("0")){

            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendHours().appendSuffix("h ago")
                    .printZeroNever()
                    .toFormatter();
            elapsed = formatter.print(period);
            if(elapsed.equalsIgnoreCase("")){
                formatter = new PeriodFormatterBuilder()
                        .appendMinutes().appendSuffix("m ago")
                        .printZeroNever()
                        .toFormatter();
                elapsed = formatter.print(period);
                if(elapsed.equalsIgnoreCase("")){
                    formatter = new PeriodFormatterBuilder()
                            .appendSeconds().appendSuffix("s ago")
                            .printZeroNever()
                            .toFormatter();
                    elapsed = formatter.print(period);
                }
                if(elapsed.equalsIgnoreCase(""))
                    elapsed = "just now";
            }
        }
        return elapsed;
    }
}
