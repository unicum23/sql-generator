package shudn;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

public class SignPointSvyaznoy {

    private static final Map<String, DayOfWeek> WEEK_DAYS = Map.of(
            "пн", MONDAY,
            "вт", TUESDAY,
            "ср", WEDNESDAY,
            "чт", THURSDAY,
            "пт", FRIDAY,
            "сб", SATURDAY,
            "вс", SUNDAY
    );

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new JSR310Module())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static void main(String[] args) {
        List<PointConfig> migrations = loadObjectList(PointConfig.class, "shudn/sign_point_svyaznoy_31_03.csv").stream()
                .map(SignPointSvyaznoy::normalizeShopName)
                .filter(distinctByKey(p -> p.partnerCode))
                .collect(Collectors.toList());

// Пилотный список
        List<PointConfig> pilotTT = loadObjectList(PointConfig.class, "shudn/pilot_TT_31_03.csv");
//        var partnerCodeAllTT = migrations.stream()
//                .map(PointConfig::getPartnerCode)
//                .collect(Collectors.toSet());
        var partnerCodePilotTT = pilotTT.stream()
                .map(PointConfig::getPartnerCode)
                .collect(Collectors.toSet());
//
//        partnerCodePilotTT.stream()
//                .filter(p -> !partnerCodeAllTT.contains(p))
//                .forEach(System.out::println);

        System.out.println(migrations.size());
        migrations.stream()
                .filter(Objects::nonNull)
                .filter(m -> partnerCodePilotTT.contains(m.getPartnerCode()))
                .map(SignPointSvyaznoy::toInsert)
                .skip(0)
                .limit(1000)
                .forEach(System.out::println);
    }

    private static PointConfig normalizeShopName(PointConfig p) {
        p.shopName = p.shopName.replace("_", " ");
        p.shopName = p.shopName.replaceAll("\\.\\.", ".");
        p.shopName = p.shopName.replaceAll(",,", ",");
        p.shopName = p.shopName.replaceAll("'", "''");
        p.shopName = p.shopName.replaceAll("\\)", ") ");
        p.shopName = p.shopName.replaceAll("\\(", " (");
        p.shopName = p.shopName.replaceAll("\\.", ". ");
        p.shopName = p.shopName.replaceAll("\\s+\\.", ". ");
        p.shopName = p.shopName.replaceAll(",", ", ");
        p.shopName = p.shopName.replaceAll("\\s+,", ", ");
        p.shopName = p.shopName.replaceAll("\\s+\\)", ") ");
        p.shopName = p.shopName.replaceAll("\\(\\s+", " (");
        p.shopName = StringUtils.normalizeSpace(p.shopName);

        p.shopAddress = p.shopAddress.replaceAll("\"\"\"\"", "\"")
                .replaceAll("\"\"", "\"")
                .replaceAll("'", "''");


        p.latitude = p.latitude.replaceAll(",", ".");
        p.longitude = p.longitude.replaceAll(",", ".");

        return p;
    }

    private static String toInsert(PointConfig point) {
        final String type = "PARTNER";
        final String workingHours = workingHours(mapper, point.workingHours);

        return String.format("INSERT INTO signing_point (regcenter_id, lending_office_id, type, code, partner_code, name, address, location_message, latitude, longitude, working_hours) " +
                        "VALUES (%s, %s, '%s', %s, '%s', '%s', '%s', %s, %s, %s, '%s');",
                null, null, type, null, point.partnerCode, point.shopName,
                Optional.ofNullable(point.shopAddress).orElse(""),
                null,
                StringUtils.isBlank(point.latitude) ? "null" : point.latitude,
                StringUtils.isBlank(point.longitude) ? "null" : point.longitude,
                workingHours);
    }

    @SneakyThrows
    private static String workingHours(ObjectMapper mapper, String workingHours) {
        if (StringUtils.isBlank(workingHours)) {
            return "";
        }

        final List<WorkingHours> hours = new ArrayList<>();
        final String[] lines = workingHours.split(",");

        for (String line : lines) {
            line = line.trim();
            if (StringUtils.isBlank(line) || line.toLowerCase().contains("выходной")) {
                continue;
            }


            final String[] ranges = line.substring(0, line.indexOf("(")).trim().split("-");
            assert (ranges.length == 1 || ranges.length == 2);

            DayOfWeek startDay = WEEK_DAYS.get(ranges[0].toLowerCase());
            if (ranges.length == 1) {
                final String[] times = line.substring(4, line.length() - 1).split("-");
                assert (times.length == 2);
                hours.add(new WorkingHours(startDay, times[0], times[1]));
            } else {
                final String[] times = line.substring(7, line.length() - 1).split("-");
                assert (times.length == 2);
                DayOfWeek endDay = WEEK_DAYS.get(ranges[1].toLowerCase());
                do {
                    hours.add(new WorkingHours(startDay, times[0], times[1]));
                    startDay = startDay.plus(1);
                } while (startDay != endDay);
                hours.add(new WorkingHours(endDay, times[0], times[1]));
            }
        }

        return hours.isEmpty() ? "null" : mapper.writeValueAsString(hours);
    }

    @SneakyThrows
    public static <T> List<T> loadObjectList(Class<T> type, String fileName) {

        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';');
        CsvMapper mapper = new CsvMapper();
        File file = new ClassPathResource(fileName, SignPointSvyaznoy.class.getClassLoader()).getFile();
        MappingIterator<T> readValues =
                mapper.reader(type)
                        .with(bootstrapSchema)
                        .readValues(file);
        return readValues.readAll();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointConfig {
        String partnerCode;
        String shopName;
        String shopAddress;
        String workingHours;
        String latitude;
        String longitude;
    }

    @Data
    public static class WorkingHours {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        private DayOfWeek dayOfWeek;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime startTime;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime endTime;
        private List<Object> breaks = List.of();

        public WorkingHours(DayOfWeek day, String from, String to) {
            this.dayOfWeek = day;
            this.startTime = LocalTime.parse(from, formatter);
            this.endTime = LocalTime.parse(to, formatter);
        }
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
