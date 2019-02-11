package ca.polymtl.inf8480.tp1.shared;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jelacs on 08/02/19.
 */
public class Mail {


    private String subject;
    private String from;
    private boolean read;
    private Date sent_date;
    private DateFormat dateFormat;
    private byte[] content;

    public Mail(String subject, String from, boolean read, Date sent_date, byte[] content){
        this.subject = subject;
        this.from = from;
        this.read = read;
        this.sent_date = sent_date;
        this.dateFormat = new SimpleDateFormat("EEE MMM d yyyy HH:mm");
        this.content = content;
    }

    public Date getSent_date() {
        return sent_date;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public boolean isRead() {
        return read;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setSent_date(Date sent_date) {
        this.sent_date = sent_date;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
