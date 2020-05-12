package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

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
    public static int TIMEOUT_MS=10000;
    public static String BACKEND_URL = "http://shrav280805androidnode.wl.r.appspot.com/";
//    public static String BACKEND_URL = "http://localhost:8080/";

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
        if(elapsed.equalsIgnoreCase("0d ago")){

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

//    public static void shareDialogDisplay(final String newsUrl) {
//        final Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.share_dialog_box);
//
//        ImageButton twitterButton = dialog.findViewById(R.id.dialog_twitter_button);
//        ImageButton facebookButton = dialog.findViewById(R.id.dialog_facebook_button);
//        ImageButton emailButton = dialog.findViewById(R.id.dialog_email_button);
//
//        twitterButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                String twitterUrl = "https://twitter.com/intent/tweet?text=Check%20out%20this%20Link:&url="+newsUrl+"&hashtags=CSCI571NewsSearch";
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl)));
//            }
//        });
//
//        facebookButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final ShareDialog shareDialog = new ShareDialog();
//                ShareHashtag shareHashtag = new ShareHashtag.Builder()
//                        .setHashtag("CSCI571")
//                        .build();
//
//                if (shareDialog.canShow(ShareLinkContent.class)) {
//
//                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                            .setQuote("CSCI571 NewsApp")
//                            .setContentUrl(Uri.parse(newsUrl))
//                            .setShareHashtag(shareHashtag)
//                            .build();
//
//                    shareDialog.show(linkContent);
//
//                }
//            }
//        });
//
//        emailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                        "mailto","", null));
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "#CSCI571 NewsApp");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "Check out this link : "+ newsUrl);
//                startActivity(Intent.createChooser(emailIntent, "Send email..."));
//            }
//        });
//        dialog.show();
//    }


}
