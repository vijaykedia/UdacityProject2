package com.kediavijay.popularmovies2;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void testDateParsing() throws Exception {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        try {
            final Date date = dateFormat.parse("2015-09-30");
            final long releaseDate = date.getTime();
            System.out.println(releaseDate);
        } catch (final ParseException ignored) {
        }
    }
}