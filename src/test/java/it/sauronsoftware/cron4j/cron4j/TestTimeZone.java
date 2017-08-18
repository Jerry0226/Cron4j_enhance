package it.sauronsoftware.cron4j.cron4j;

import java.util.TimeZone;

public class TestTimeZone {

	public static void main(String[] args) {
		TimeZone tz = TimeZone.getDefault();
		System.out.println(tz);
	}
}
