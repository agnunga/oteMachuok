package com.uiresource.messenger.data;

import java.util.Date;

public class Conversation {
	public long threadId;
	public long date;
	public long msgCount;
	public long lastMsgId;
	public String lastMsgText;
	public Date lastMsgTime;
	public boolean read;
	public Contact contact;
	
	public Conversation() {
		threadId = -1;
		date = -1;
		msgCount = -1;
		read = false;
		lastMsgId = -1;
		lastMsgText = "";
		lastMsgTime = new Date();
		contact = null;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Conversation) {
			Conversation c = (Conversation) o;
			return threadId == c.threadId;
		}
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ConversationActivity[");
		sb.append("name=" + contact.name);
		sb.append(" thread_id=" + threadId);
		sb.append(" last_msg=" + lastMsgId);
		sb.append(" msg_count=" + msgCount);
		sb.append(" number=" + contact.number);
		sb.append("]");
		
		return sb.toString();
	}
}

/*
Contacts DB fields:

times_contacted
primary_organization
phonetic_name
type
mode
last_time_contacted
_sync_time
_id
_sync_id
number_key
primary_email
name
sort_string
primary_phone
im_account
_sync_account
_sync_version
send_to_voicemail
custom_ringtone
status
_sync_local_id
number
label
display_name
_sync_dirty
im_handle
starred
notes
im_protocol

SMS DB fields:

_id
thread_id
address
person
date
protocol
read
status
type
reply_path_present
subject
body
service_center
*/