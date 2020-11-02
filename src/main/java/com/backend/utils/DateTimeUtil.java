package com.backend.utils;

import com.backend.enumeration.ErrorCode;
import com.backend.exception.RestApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DateTimeUtil {
    private static final Logger log = LoggerFactory.getLogger(DateTimeUtil.class);
    public static final String DD_MM_YYYY = "dd-MM-yyyy";
    public static final String MMMM_DD_HH_MM = "MMMM dd HH:mm";
    public static final String JAPANES_MMMM_DD_HH_MM = "MM月dd日 HH:mm";
    private static final List<Integer> MONTHS = IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList());

    public static String formatInstant(Instant input, String format) {
        if (input == null) {
            return null;
        } else {
            DateTimeFormatter dateTimeFormatter =
                    DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
            return dateTimeFormatter.format(input);
        }
    }

    public static Date convertStringToDate(String input, String format) {
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            return sdf.parse(input);
        } catch (ParseException e) {
            throw new RestApiException(ErrorCode.TIME_FORMAT_INVALID);
        }
    }

    public static String convertDateToString(Date input, String format) {
        if (Objects.isNull(input)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        return sdf.format(input);

    }

    public static Instant convertStringToInstantUtc(String dateStr, String format) {
        return convertStringToInstant(dateStr, format, null);
    }

    public static Instant convertStringToInstant(String dateStr, String format, ZoneId zoneId) {
        if (dateStr == null || format == null)
            return null;
        if (zoneId == null) {
            zoneId = ZoneId.of("UTC");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        try {
            LocalDate localDate = LocalDate.parse(dateStr, formatter);
            return localDate.atStartOfDay(zoneId).toInstant();
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatInstantWithTimezone(Instant input, String format, String timezone) {
        if (input == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        ZoneId zone = ZoneId.systemDefault();
        try {
            if (timezone != null) {
                zone = ZoneId.of(timezone);
            }
            ZonedDateTime zoneDateTime = input.atZone(zone);
            return formatter.format(zoneDateTime);
        } catch (Exception e) {
            return formatInstant(input, format);
        }
    }

    public static Instant convertInstantWithTimezone(String input, String format, String timezone,
                                                     boolean reverse) throws ParseException {
        if (input == null) {
            return null;
        }
        // Get datetime UTC
        TimeZone zoneUTC = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        sdf.setTimeZone(zoneUTC);
        Instant dateUTC = sdf.parse(input).toInstant();

        // Convert time utc with timezone corresponding
        try {
            ZoneId zone = ZoneId.systemDefault();
            if (timezone != null) {
                zone = ZoneId.of(timezone);
            }
            ZoneOffset offset = zone.getRules().getOffset(dateUTC);
            long offsetSecond = (long) offset.getTotalSeconds();
            if (reverse) {
                offsetSecond = -1 * offsetSecond;
            }
            return dateUTC.plusSeconds(offsetSecond);
        } catch (Exception e) {
            return Objects.requireNonNull(convertStringToDate(input, format)).toInstant();
        }
    }

    public static Integer getAge(Instant birthday, String timeZone) {
        try {
            if (birthday == null) {
                return null;
            }
            if (StringUtils.isEmpty(timeZone)) {
                timeZone = ZoneId.systemDefault().toString();
            }
            LocalDate birthdayLocalDate = birthday.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate now = LocalDate.now(ZoneId.of(timeZone));
            return Period.between(birthdayLocalDate, now).getYears();
        } catch (Exception e) {
            log.error("Get age error", e);
            return null;
        }
    }

    public static String convertInstantToString(Instant startDate, Locale locale, ZoneId zoneId) {
        if (startDate == null)
            return "";
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(MMMM_DD_HH_MM).withLocale(locale).withZone(zoneId);
        return formatter.format(startDate);
    }

    public static String convertInstantToJapanesStringFormat(Instant startDate, Locale locale,
                                                             ZoneId zoneId) {
        if (startDate == null)
            return "";
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(JAPANES_MMMM_DD_HH_MM).withLocale(locale).withZone(zoneId);
        return formatter.format(startDate);
    }

    public static Map<String, String> findTimeByPeriod(int period) {
        Map<String, String> map = new HashMap<String, String>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        switch (period) {
            // tháng này
            case 1:
                cal.set(Calendar.DAY_OF_MONTH, 1);
                Date from = cal.getTime();
                map.put("fromDateSign", sdf.format(from));
                cal.set(Calendar.DAY_OF_MONTH, 31);
                from = cal.getTime();
                map.put("toDateSign", sdf.format(from));
                break;
            // tháng trước
            case 2:
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DATE, 1);
                Date from1 = cal.getTime();
                map.put("fromDateSign", sdf.format(from1));
                cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
                Date to1 = cal.getTime();
                map.put("toDateSign", sdf.format(to1));
                break;
            // năm này
            case 3:
                cal.set(Calendar.DAY_OF_YEAR, 1);
                Date date = cal.getTime();
                map.put("fromDateSign", sdf.format(date));
                cal.set(Calendar.DAY_OF_YEAR, 365);
                date = cal.getTime();
                map.put("toDateSign", sdf.format(date));
                break;
            // năm trước
            case 4:
                cal.add(Calendar.YEAR, -1);
                cal.set(Calendar.DATE, 1);
                Date from2 = cal.getTime();
                map.put("fromDateSign", sdf.format(from2));
                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR));
                Date to2 = cal.getTime();
                map.put("toDateSign", sdf.format(to2));
                break;
        }
        return map;
    }

    public static Map<String, List<String>> findListMonthAndYear(Date dateFrom, Date dateTo) {
        Map<String, List<String>> result = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.setTime(dateFrom);
        int yearFrom = c.get(Calendar.YEAR);
        int monthFrom = c.get(Calendar.MONTH) + 1;

        c.setTime(dateTo);
        int yearTo = c.get(Calendar.YEAR);
        int monthTo = c.get(Calendar.MONTH) + 1;
        List<String> listYear = IntStream.rangeClosed(yearFrom, yearTo).boxed().map(String::valueOf)
                .collect(Collectors.toList());
        result.put("year", listYear);
        if (monthFrom > monthTo) {
            // Để lấy cả giá trị monthTo và monthFrom vào months
            List<Integer> listMonth =
                    IntStream.rangeClosed(monthTo + 1, monthFrom - 1).boxed().collect(Collectors.toList());
            List<Integer> months = new ArrayList<>(MONTHS);
            months.removeAll(listMonth);
            result.put("month", months.stream().map(String::valueOf).collect(Collectors.toList()));
        } else {
            List<String> listMonth = IntStream.rangeClosed(monthFrom, monthTo).boxed()
                    .map(DateTimeUtil::formatMonth).collect(Collectors.toList());
            result.put("month", listMonth);
        }
        return result;
    }

    private static String formatMonth(int month) {
        return month < 10 ? "0" + month : String.valueOf(month);
    }
}
