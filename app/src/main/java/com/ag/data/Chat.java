package com.ag.data;

public class Chat {
	private long threadId;
	private long date;
	private String msgCount;
	private long lastMsgId;
    private String recipient_ids;
    private String snippet;
    private String type;
    private long read;
    private long unreadCount;
    private Contact contact;
    private boolean online;

    public long getUnreadCount() {
        return unreadCount > 0 ? unreadCount : 0;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }

    public long getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(long lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public String getRecipient_ids() {
        return recipient_ids;
    }

    public void setRecipient_ids(String recipient_ids) {
        this.recipient_ids = recipient_ids;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getRead() {
        return read;
    }

    public void setRead(long read) {
        this.read = read;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Chat() {
		/*threadId = -1;
		date = -1;
		msgCount = "";
		read = -1;
		lastMsgId = -1;
		snippet = "";
		lastMsgTime = new Date();
		contact = null;*/
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Chat) {
			Chat c = (Chat) o;
			return threadId == c.threadId;
		}
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Chat[");
		sb.append("name=" + getContact().getName());
		sb.append(" thread_id=" + threadId);
		sb.append(" last_msg=" + lastMsgId);
		sb.append(" msg_count=" + msgCount);
		sb.append(" number=" + getContact().getNumber());
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