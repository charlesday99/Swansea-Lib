package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import application.ScreenManager;

public class Event {
	
	private int ID;
	private String title;
	private String details;
	private String date;
	private int maxAttending;
	
	private static int totalEventNo = 0;

	private static ArrayList<Event> allEvents = new ArrayList<Event>();
	private static ArrayList<Event> usersEvents = new ArrayList<Event>();

	public Event(String title, String details, String date, int maxAttending) {
		
		this.title = title;
		this.details = details;
		this.date = date;
		this.maxAttending = maxAttending;
		
	}
	
	public static int getTotalEventNo() {
		return totalEventNo;
	}

	public static void setTotalEventNo(int totalEventNo) {
		Event.totalEventNo = totalEventNo;
	}
	
	private static boolean checkFutureDate(String dateString) throws ParseException {
		LocalDateTime localDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime eventDate = LocalDateTime.parse(dateString, formatter);
		return eventDate.isAfter(localDate);
	}
	
	public static void loadEvents() throws SQLException, ParseException {
		
		Connection connectionToDB = DBHelper.getConnection();
        Statement stmt = connectionToDB.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM events");
        
        allEvents.clear();
        usersEvents.clear();
        
        ArrayList<Integer> usersEventIDs = null;;
        if(ScreenManager.getCurrentUser() instanceof User) {
        	usersEventIDs = ((User) ScreenManager.getCurrentUser()).loadUserEvents();
        }

		while(rs.next()) {
			totalEventNo += 1;
			if(ScreenManager.getCurrentUser() instanceof User) {
				if(checkFutureDate(rs.getString(4)) && !(usersEventIDs.contains(rs.getInt(1)))) {
					allEvents.add(new Event(rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)));
					allEvents.get(allEvents.size()-1).setID(rs.getInt(1));
				} else if (usersEventIDs.contains(rs.getInt(1))) {
					usersEvents.add(new Event(rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)));
					usersEvents.get(usersEvents.size()-1).setID(rs.getInt(1));
				}
			} else {
				allEvents.add(new Event(rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)));
				allEvents.get(allEvents.size()-1).setID(rs.getInt(1));
			}
		}
		
		
		connectionToDB.close();
		
	}

	public static void addEvent(String title, String details, String date, int maxAllowed) {
		allEvents.add(new Event(title, details, date, maxAllowed));
	}
	
	

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDetails() {
		return details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public int getMaxAttending() {
		return maxAttending;
	}
	
	public void setMaxAttending(int maxAttending) {
		this.maxAttending = maxAttending;
	}
	
	public static ArrayList<Event> getAllEvents() {
		return allEvents;
	}

	public static void setAllEvents(ArrayList<Event> newEvents) {
		allEvents = newEvents;
	}
	
	public static void updateEvent(Event event) throws SQLException {
		
		Connection connectionToDB = DBHelper.getConnection();
        Statement stmt = connectionToDB.createStatement();
        stmt.execute("UPDATE events SET title = '" + event.title + "', details = '" +
        event.details + "', date = '" + event.date + "', maxAllowed = " + event.maxAttending + " WHERE eID = " + event.ID);
        connectionToDB.close();

	}
	
	public static void deleteEvent(int ID) throws SQLException {
		
		Connection connectionToDB = DBHelper.getConnection();
        Statement stmt = connectionToDB.createStatement();
        stmt.execute("DELETE FROM events WHERE eID = " + ID);
        connectionToDB.close();

	}
	
	public static ArrayList<Event> getUserEvents() throws SQLException {
		return usersEvents;
	}
	
	public static void addUserEvent(String username, int ID) throws SQLException {
		
		Connection connectionToDB = DBHelper.getConnection();
        PreparedStatement sqlStatement = connectionToDB.prepareStatement("INSERT INTO userEvents VALUES (?,?)");

        sqlStatement.setInt(1, ID);
        sqlStatement.setString(2, username);
        
        sqlStatement.execute();
        connectionToDB.close();
  
	}

}