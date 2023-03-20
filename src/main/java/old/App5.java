package old;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class App5 {

    private static Map<String, LendingOffice> data = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new JSR310Module())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    static {
        data.put("1-4TFH-83", new LendingOffice(8,5));
        data.put("2-25EF46X", new LendingOffice(4,80));
        data.put("1-4TFH-92", new LendingOffice(3,95));
        data.put("1-4TFH-31", new LendingOffice(3,96));
        data.put("1-4TFH-35", new LendingOffice(4,81));
        data.put("1-4TFH-55", new LendingOffice(7,53));
        data.put("1-4TFH-4", new LendingOffice(4,85));
        data.put("1-4TFH-46", new LendingOffice(3,100));
        data.put("2-21K1KOY", new LendingOffice(3,105));
        data.put("1-4TFH-88", new LendingOffice(4,92));
        data.put("1-4TFH-39", new LendingOffice(8,2));
        data.put("1-4TFH-14", new LendingOffice(8,4));
        data.put("1-4TFH-66", new LendingOffice(8,7));
        data.put("1-4TFH-37", new LendingOffice(8,8));
        data.put("1-4TFH-25", new LendingOffice(8,9));
        data.put("1-4TFH-26", new LendingOffice(8,11));
        data.put("1-4TFH-13", new LendingOffice(8,12));
        data.put("1-4TFH-23", new LendingOffice(8,13));
        data.put("1-4TFH-27", new LendingOffice(8,17));
        data.put("1-4TFH-38", new LendingOffice(8,18));
        data.put("1-4TFH-89", new LendingOffice(5,37));
        data.put("2-B3Y0EI", new LendingOffice(4,82));
        data.put("1-4TFH-49", new LendingOffice(1,69));
        data.put("1-4TFH-36", new LendingOffice(5,38));
        data.put("1-4TFH-9", new LendingOffice(5,39));
        data.put("2-2087U04", new LendingOffice(3,97));
        data.put("1-4TFH-95", new LendingOffice(7,50));
        data.put("1-4TFH-87", new LendingOffice(3,98));
        data.put("1-4TFH-29", new LendingOffice(8,19));
        data.put("1-4TFH-20", new LendingOffice(8,31));
        data.put("1-4TFH-21", new LendingOffice(7,52));
        data.put("1-4TFH-3", new LendingOffice(4,84));
        data.put("1-4TFH-64", new LendingOffice(5,40));
        data.put("1-4TFH-43", new LendingOffice(8,23));
        data.put("1-4TFH-1", new LendingOffice(3,99));
        data.put("1-4TFH-2", new LendingOffice(4,86));
        data.put("2-2G35ALR", new LendingOffice(4,87));
        data.put("1-4TFH-30", new LendingOffice(7,56));
        data.put("1-4TFH-7", new LendingOffice(7,57));
        data.put("1-4TFH-18", new LendingOffice(8,21));
        data.put("2-9GWSPT", new LendingOffice(3,101));
        data.put("2-299T49H", new LendingOffice(5,42));
        data.put("1-4TFH-10", new LendingOffice(4,88));
        data.put("2-2082ADX", new LendingOffice(3,102));
        data.put("1-4TFH-15", new LendingOffice(4,89));
        data.put("1-4TFH-58", new LendingOffice(4,91));
        data.put("1-4TFH-63", new LendingOffice(8,24));
        data.put("1-4TFH-70", new LendingOffice(8,26));
        data.put("1-4TFH-33", new LendingOffice(5,43));
        data.put("1-4TFH-65", new LendingOffice(8,27));
        data.put("1-4TFH-60", new LendingOffice(3,103));
        data.put("1-4TFH-17", new LendingOffice(3,104));
        data.put("1-4TFH-12", new LendingOffice(7,59));
        data.put("2-25EF3XB", new LendingOffice(8,28));
        data.put("1-4TFH-79", new LendingOffice(8,32));
        data.put("1-4TFH-28", new LendingOffice(7,60));
        data.put("1-4TFH-99", new LendingOffice(3,106));
        data.put("1-4TFH-19", new LendingOffice(8,34));
        data.put("1-4TFH-84", new LendingOffice(7,62));
        data.put("1-4TFH-5", new LendingOffice(7,63));
        data.put("1-4TFH-94", new LendingOffice(7,64));
        data.put("2-28ED84F", new LendingOffice(8,35));
        data.put("1-4TFH-16", new LendingOffice(8,36));
        data.put("1-4TFH-59", new LendingOffice(5,44));
        data.put("1-4TFH-32", new LendingOffice(5,45));
        data.put("2-2087TZY", new LendingOffice(3,107));
        data.put("1-4TFH-8", new LendingOffice(3,14));
        data.put("1-4TFH-56", new LendingOffice(3,48));
        data.put("1-4TFH-34", new LendingOffice(3,51));
        data.put("1-4TFH-40", new LendingOffice(3,55));
        data.put("1-4TFH-50", new LendingOffice(8,77));
        data.put("2-25EF44V", new LendingOffice(3,108));
        data.put("1-4TFH-100", new LendingOffice(5,46));
        data.put("1-4TFH-97", new LendingOffice(5,47));
        data.put("1-4TFH-6", new LendingOffice(7,65));
        data.put("1-4TFH-86", new LendingOffice(7,66));
        data.put("1-4TFH-54", new LendingOffice(7,70));
        data.put("1-4TFH-98", new LendingOffice(7,71));
        data.put("1-4TFH-72", new LendingOffice(7,72));
        data.put("1-4TFH-11", new LendingOffice(7,73));
        data.put("1-4TFH-52", new LendingOffice(7,74));
        data.put("1-4TFH-48", new LendingOffice(7,75));
        data.put("1-4TFH-57", new LendingOffice(7,76));
        data.put("1-4TFH-42", new LendingOffice(5,93));
        data.put("1-4TFH-85", new LendingOffice(5,94));
        data.put("1-4TFH-83", new LendingOffice(8,5));
        data.put("2-25EF46X", new LendingOffice(4,80));
        data.put("1-4TFH-92", new LendingOffice(3,95));
        data.put("1-4TFH-31", new LendingOffice(3,96));
        data.put("1-4TFH-35", new LendingOffice(4,81));
        data.put("1-4TFH-55", new LendingOffice(7,53));
        data.put("1-4TFH-4", new LendingOffice(4,85));
        data.put("1-4TFH-46", new LendingOffice(3,100));
        data.put("2-21K1KOY", new LendingOffice(3,105));
        data.put("1-4TFH-88", new LendingOffice(4,92));
        data.put("1-4TFH-39", new LendingOffice(8,2));
        data.put("1-4TFH-14", new LendingOffice(8,4));
        data.put("1-4TFH-66", new LendingOffice(8,7));
        data.put("1-4TFH-37", new LendingOffice(8,8));
        data.put("1-4TFH-25", new LendingOffice(8,9));
        data.put("1-4TFH-26", new LendingOffice(8,11));
        data.put("1-4TFH-13", new LendingOffice(8,12));
        data.put("1-4TFH-23", new LendingOffice(8,13));
        data.put("1-4TFH-27", new LendingOffice(8,17));
        data.put("1-4TFH-38", new LendingOffice(8,18));
        data.put("1-4TFH-89", new LendingOffice(5,37));
        data.put("2-B3Y0EI", new LendingOffice(4,82));
        data.put("1-4TFH-49", new LendingOffice(1,69));
        data.put("1-4TFH-36", new LendingOffice(5,38));
        data.put("1-4TFH-9", new LendingOffice(5,39));
        data.put("2-2087U04", new LendingOffice(3,97));
        data.put("1-4TFH-95", new LendingOffice(7,50));
        data.put("1-4TFH-87", new LendingOffice(3,98));
        data.put("1-4TFH-29", new LendingOffice(8,19));
        data.put("1-4TFH-20", new LendingOffice(8,31));
        data.put("1-4TFH-21", new LendingOffice(7,52));
        data.put("1-4TFH-3", new LendingOffice(4,84));
        data.put("1-4TFH-64", new LendingOffice(5,40));
        data.put("1-4TFH-43", new LendingOffice(8,23));
        data.put("1-4TFH-1", new LendingOffice(3,99));
        data.put("1-4TFH-2", new LendingOffice(4,86));
        data.put("2-2G35ALR", new LendingOffice(4,87));
        data.put("1-4TFH-30", new LendingOffice(7,56));
        data.put("1-4TFH-7", new LendingOffice(7,57));
        data.put("1-4TFH-18", new LendingOffice(8,21));
        data.put("2-9GWSPT", new LendingOffice(3,101));
        data.put("2-299T49H", new LendingOffice(5,42));
        data.put("1-4TFH-10", new LendingOffice(4,88));
        data.put("2-2082ADX", new LendingOffice(3,102));
        data.put("1-4TFH-15", new LendingOffice(4,89));
        data.put("1-4TFH-58", new LendingOffice(4,91));
        data.put("1-4TFH-63", new LendingOffice(8,24));
        data.put("1-4TFH-70", new LendingOffice(8,26));
        data.put("1-4TFH-33", new LendingOffice(5,43));
        data.put("1-4TFH-65", new LendingOffice(8,27));
        data.put("1-4TFH-60", new LendingOffice(3,103));
        data.put("1-4TFH-17", new LendingOffice(3,104));
        data.put("1-4TFH-12", new LendingOffice(7,59));
        data.put("2-25EF3XB", new LendingOffice(8,28));
        data.put("1-4TFH-79", new LendingOffice(8,32));
        data.put("1-4TFH-28", new LendingOffice(7,60));
        data.put("1-4TFH-99", new LendingOffice(3,106));
        data.put("1-4TFH-19", new LendingOffice(8,34));
        data.put("1-4TFH-84", new LendingOffice(7,62));
        data.put("1-4TFH-5", new LendingOffice(7,63));
        data.put("1-4TFH-94", new LendingOffice(7,64));
        data.put("2-28ED84F", new LendingOffice(8,35));
        data.put("1-4TFH-16", new LendingOffice(8,36));
        data.put("1-4TFH-59", new LendingOffice(5,44));
        data.put("1-4TFH-32", new LendingOffice(5,45));
        data.put("2-2087TZY", new LendingOffice(3,107));
        data.put("1-4TFH-8", new LendingOffice(3,14));
        data.put("1-4TFH-56", new LendingOffice(3,48));
        data.put("1-4TFH-34", new LendingOffice(3,51));
        data.put("1-4TFH-40", new LendingOffice(3,55));
        data.put("1-4TFH-50", new LendingOffice(8,77));
        data.put("2-25EF44V", new LendingOffice(3,108));
        data.put("1-4TFH-100", new LendingOffice(5,46));
        data.put("1-4TFH-97", new LendingOffice(5,47));
        data.put("1-4TFH-6", new LendingOffice(7,65));
        data.put("1-4TFH-86", new LendingOffice(7,66));
        data.put("1-4TFH-54", new LendingOffice(7,70));
        data.put("1-4TFH-98", new LendingOffice(7,71));
        data.put("1-4TFH-72", new LendingOffice(7,72));
        data.put("1-4TFH-11", new LendingOffice(7,73));
        data.put("1-4TFH-52", new LendingOffice(7,74));
        data.put("1-4TFH-48", new LendingOffice(7,75));
        data.put("1-4TFH-57", new LendingOffice(7,76));
        data.put("1-4TFH-42", new LendingOffice(5,93));
        data.put("1-4TFH-85", new LendingOffice(5,94));
    }

    public static void main(String[] args) {
        List<PointConfig> migrations = loadObjectList(PointConfig.class, "./sp.csv").stream()
                .filter(p -> "1,2 порядок".equals(p.order))
                .map(App5::normalizeShopName)
                .collect(Collectors.toList());

//        System.out.println(migrations.size());
        migrations.stream()
                .map(App5::toInsert)
                .filter(Objects::nonNull)
                .skip(2000)
                .limit(500)
                .forEach(p -> System.out.println(p));
    }

    private static PointConfig normalizeShopName(PointConfig p) {
        final String lowercaseName = p.shopName.toLowerCase();

        if ("IPOS".equals(p.type)) {
            p.shopName = "Стойка оформления ОТП Банка";
            return p;
        }

        if (lowercaseName.contains("днс")) {
            p.shopName = "Магазин ДНС";
            return p;
        }

        if (!p.commercialNetName.isBlank()) {
            p.shopName = p.commercialNetName;
        }

        p.shopName = p.shopName.replace("14-09", "");
        p.shopName = p.shopName.replace("_", " ");
        p.shopName = p.shopName.replaceAll("\\.\\.", ".");
        p.shopName = p.shopName.replaceAll(",,", ",");
        p.shopName = p.shopName.replaceAll("\\)", ") ");
        p.shopName = p.shopName.replaceAll("\\(", " (");
        p.shopName = p.shopName.replaceAll("\\.", ". ");
        p.shopName = p.shopName.replaceAll("\\s+\\.", ". ");
        p.shopName = p.shopName.replaceAll(",", ", ");
        p.shopName = p.shopName.replaceAll("\\s+,", ", ");
        p.shopName = p.shopName.replaceAll("\\s+\\)", ") ");
        p.shopName = p.shopName.replaceAll("\\(\\s+", " (");
        p.shopName = StringUtils.normalizeSpace(p.shopName);

        return p;
    }

    private static String toInsert(PointConfig point) {
        final LendingOffice lo = data.get(point.lendingOfficeCode);
        if (lo == null) {
            return null;
        }
        final String type = point.type.toLowerCase().contains("ipos") ? "IPOS" : "POS";
        final String workingHours = workingHours(mapper, point.workingHours);

        return String.format("INSERT INTO signing_point (regcenter_id, lending_office_id, type, code, name, address, location_message, latitude, longitude, working_hours) VALUES (%s, %s, '%s', '%s', '%s', '%s', '%s', %s, %s, '%s');",
                lo.regCenterId, lo.lendingOfficeId, type, point.shopCode, point.shopName,
                Optional.ofNullable(point.shopAddress).orElse(""),
                Optional.ofNullable(point.locationMessage).orElse(""),
                StringUtils.isBlank(point.latitude) ? "null" : point.latitude,
                StringUtils.isBlank(point.longitude) ? "null" : point.longitude,
                workingHours);
    }

    @SneakyThrows
    private static String workingHours(ObjectMapper mapper, String workingHours) {
        if (StringUtils.isBlank(workingHours)) {
            return "";
        }

        final String[] days = workingHours.split(";");

        final List<WorkingHours> hours = new ArrayList<>();

        for (String day : days) {
            if (StringUtils.isBlank(day) || day.toLowerCase().contains("выходной")) {
                continue;
            }

            final String data = day.trim().toLowerCase();
            
            if (data.startsWith("пн-пт:")) {
                final String[] times = data.substring(6).trim().split("-");
                assert(times.length == 2);

                hours.add(new WorkingHours(DayOfWeek.MONDAY, times[0], times[1]));
                hours.add(new WorkingHours(DayOfWeek.TUESDAY, times[0], times[1]));
                hours.add(new WorkingHours(DayOfWeek.WEDNESDAY, times[0], times[1]));
                hours.add(new WorkingHours(DayOfWeek.THURSDAY, times[0], times[1]));
                hours.add(new WorkingHours(DayOfWeek.FRIDAY, times[0], times[1]));
            } else if (data.startsWith("сб:")) {
                final String[] times = data.substring(3).trim().split("-");
                assert(times.length == 2);
                hours.add(new WorkingHours(DayOfWeek.SATURDAY, times[0], times[1]));
            } else if (data.startsWith("вс:")) {
                final String[] times = data.substring(3).trim().split("-");
                assert(times.length == 2);
                hours.add(new WorkingHours(DayOfWeek.SUNDAY, times[0], times[1]));
            }
        }

        return hours.isEmpty() ? "null" : mapper.writeValueAsString(hours);
    }

    @SneakyThrows
    public static <T> List<T> loadObjectList(Class<T> type, String fileName) {

        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';');
        CsvMapper mapper = new CsvMapper();
        File file = new ClassPathResource(fileName, App.class.getClassLoader()).getFile();
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
        String regcenterName;
        String lendingOfficeName;
        String lendingOfficeCode;
        String type;
        String organizationName;
        String shopName;
        String commercialNetName;
        String shopCode;
        String shopAddress;
        String locationMessage;
        String workingHours;
        String latitude;
        String longitude;
        String order;
    }

    @AllArgsConstructor
    public static class LendingOffice {
        public int regCenterId;
        public int lendingOfficeId;
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
}
