package com.htmessage.fanxinht.domain;

public class InviteMessage {
	private String from;
	private long time;
	private String reason;
 	private Status status;
	private int id;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}


	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public enum Status {

 		BEINVITEED,
 		BEREFUSED,
 		BEAGREED,
 		AGREED,
 		REFUSED
	}
	
}



