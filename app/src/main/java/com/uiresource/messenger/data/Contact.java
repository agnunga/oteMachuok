package com.uiresource.messenger.data;

public class Contact {
	public long id;
	public String name;
	public String number;

	public Contact(long id, String name, String number) {
		this.id = id;
		this.name = name;
		this.number = number;
	}
	
	public static Contact parseCached(String s) {
		String[] line = s.split("\t");
		int recipientId = Integer.parseInt(line[0]);
		String name = line[1];
		String number = line[2];
		return new Contact(recipientId, name, number);
	}
		
	@Override
	public String toString() {
		return id + "\t" + name + "\t" + number;
	}
	
	public String getFormatted() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" <");
		sb.append(number);
		sb.append(">");
		return sb.toString();
	}
}
