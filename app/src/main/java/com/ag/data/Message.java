package com.ag.data;

public class Message {
	public static final String T_INBOUND = "1";
	public static final String T_OUTBOUND = "2";
	
	public int _id;
    public String thread_id;
    public String address;
    public String person;
    public long date;
    public String protocol;
    public String read;
    public String status;
    public String type;
    public String subject;
    public String body;
}
