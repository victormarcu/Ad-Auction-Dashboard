package util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;

public class DateProcessor {
	
	// ==== Constants ====
	
	public static final long DATE_NULL = -1;
	
	static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	static final ZoneOffset systemZone = ZoneOffset.UTC;
	
	// see LocalDate.class header
	static final int DAYS_PER_CYCLE = 146097;
    static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);
    
    // see LocalTime.class header
    static final int MINUTES_PER_HOUR = 60;
    static final int SECONDS_PER_MINUTE = 60;
    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
	
	
	// ==== Static Methods ====
	
	/**
	 * Converts a Date String into a long
	 * 
	 * @param date - string representation of date in yyyy-MM-dd HH:mm:ss
	 * @return long representing the date
	 */
	public static long stringToLong(String date) {
		return charArrayToLong(date.toCharArray());
	}
	
	public static long charArrayToLong(char[] data) {
		if (data.length < 19)
			if (data[0] == 'n' && data[1] == '/' && data[2] == 'a')
				return DATE_NULL;
			else
				throw new IllegalArgumentException("date in incorrect format!");
		
		final long year = charArrayToInt(data, 0, 4);
		final long month = charArrayToInt(data, 5, 7);
		final long day = charArrayToInt(data, 8, 10);
		
		final int hour = charArrayToInt(data, 11, 13);
		final int minute = charArrayToInt(data, 14, 16);
		final int second = charArrayToInt(data, 17, 19);	

		return (year << 48) | (month << 40) | (day << 32) | (hour << 24) | (minute << 16) | (second << 8);
	}
	
	public static long charArrayToEpochSeconds(char[] data) {
		if (data.length != 19)
			if (data[0] == 'n' && data[1] == '/' && data[2] == 'a')
				return DATE_NULL;
			else
				throw new IllegalArgumentException("date in incorrect format!");
		
		final int year = charArrayToInt(data, 0, 4);
		final int month = charArrayToInt(data, 5, 7);
		final int day = charArrayToInt(data, 8, 10);
		
		final int hour = charArrayToInt(data, 11, 13);
		final int minute = charArrayToInt(data, 14, 16);
		final int second = charArrayToInt(data, 17, 19);
		
        long total = 0;
        total += 365 * year;
        if (year >= 0) {
            total += (year + 3) / 4 - (year + 99) / 100 + (year + 399) / 400;
        } else {
            total -= year / -4 - year / -100 + year / -400;
        }
        total += ((367 * month - 362) / 12);
        total += day - 1;
        if (month > 2) {
            total--;
            // if is a leap year
            if (!((year & 3) == 0) && ((year % 100) != 0 || (year % 400) == 0)) {
                total--;
            }
        }
        
        return (total - DAYS_0000_TO_1970) * 86400 + hour * SECONDS_PER_HOUR + minute * SECONDS_PER_MINUTE + second;
//        return ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneOffset.UTC).toEpochSecond();
	}
	
	public static long stringToEpoch(String data) {
		return charArrayToEpochSeconds(data.toCharArray());
	}
	
	
	/**
	 * Converts a long back into LocalDateTime
	 * 
	 * @param dateTime the long representation of this date
	 * @return  the LocalDateTime represented by thie date
	 */
	public static LocalDateTime longToLocalDateTime(long dateTime) {
		if (dateTime == DATE_NULL)
			return null;
		
		final int year = (int) (dateTime >> 48);
		final int month = (int) (dateTime >> 40 & 0xFF);
		final int day = (int) (dateTime >> 32 & 0xFF);
		
		// process time
		final int hour = (int) (dateTime >> 24 & 0xFF);
		final int minute = (int) (dateTime >> 16 & 0xFF);
		final int second = (int) (dateTime >> 8 & 0xFF);
		
		// create new LocalDateTime instance
		return LocalDateTime.of(year, month, day, hour, minute, second);
	}
	
	public static long longToEpoch(long dateTime) {
		if (dateTime == DATE_NULL)
			return DATE_NULL;
		
		final int year = (int) (dateTime >> 48);
		final int month = (int) (dateTime >> 40 & 0xFF);
		final int day = (int) (dateTime >> 32 & 0xFF);
		
		// process time
		final int hour = (int) (dateTime >> 24 & 0xFF);
		final int minute = (int) (dateTime >> 16 & 0xFF);
		final int second = (int) (dateTime >> 8 & 0xFF);
		
		final ZonedDateTime zdt = ZonedDateTime.of(year, month, day, hour, minute, second, 0, systemZone);
		
		// create new LocalDateTime instance
		return zdt.toEpochSecond();
	}
	
	public static LocalDateTime epochSecondsToLocalDateTime(long epochSecond) {
		return LocalDateTime.ofEpochSecond(epochSecond, 0, systemZone);
	}
 	
	
	public static LocalDateTime stringToLocalDateTime(String date) {
		return parseDate(date);
	}
	
	public static LocalDateTime parse(String date) {
		return parseDate(date);
	}
	
	
	// ==== Private Helper Methods ====
	
	private static LocalDateTime parseDate(String date) {
		final int year = Integer.parseInt(date.substring(0, 4));
		final int month = Integer.parseInt(date.substring(5, 7));
		final int day = Integer.parseInt(date.substring(8, 10));
		final int hour = Integer.parseInt(date.substring(11, 13));
		final int minute = Integer.parseInt(date.substring(14, 16));
		final int second = Integer.parseInt(date.substring(17, 19));

		return LocalDateTime.of(year, month, day, hour, minute, second);
	}
	
	/**
	 * Converts an array of characters into digits
	 * 
	 * @param data - character buffer representing data
	 * @param start - start reference
	 * @param end - end reference
	 * @return
	 */
	private static int charArrayToInt(char[] data, int start, int end) {
		int result = 0;
		
		for (int i = start; i < end; i++) {				
			result *= 10;
			result += data[i] & 0xF;
		}
		
		return result;
	}

}
