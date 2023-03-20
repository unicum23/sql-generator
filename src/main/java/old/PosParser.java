package old;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PosParser {

    private static Map<String, LendingOffice> data = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new JSR310Module())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    private static final CsvMapper CSV_MAPPER = new CsvMapper();

    static {
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Абакан", new LendingOffice(4,80));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Армавир", new LendingOffice(3,95));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Архангельск", new LendingOffice(1,66));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Астрахань", new LendingOffice(3,96));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Барнаул", new LendingOffice(4,81));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Белгород", new LendingOffice(7,48));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Благовещенск", new LendingOffice(5,38));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Владивосток", new LendingOffice(5,39));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Владикавказ", new LendingOffice(3,97));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Владимир", new LendingOffice(7,50));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Волгоград", new LendingOffice(3,98));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Вологда", new LendingOffice(1,70));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Воронеж", new LendingOffice(7,51));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Екатеринбург", new LendingOffice(6,21));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Иваново", new LendingOffice(7,52));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Ижевск", new LendingOffice(2,2));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Иркутск", new LendingOffice(4,84));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Казань", new LendingOffice(2,4));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Калининград", new LendingOffice(1,71));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Кемерово", new LendingOffice(4,85));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Киров", new LendingOffice(2,5));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Комсомольск-на-Амуре", new LendingOffice(5,40));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Котлас", new LendingOffice(1,72));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Краснодар", new LendingOffice(3,99));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Красноярск", new LendingOffice(4,86));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Курган", new LendingOffice(6,23));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Курск", new LendingOffice(7,55));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Кызыл", new LendingOffice(4,87));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Магнитогорск", new LendingOffice(6,24));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Майкоп", new LendingOffice(3,100));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Мурманск", new LendingOffice(1,73));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Нальчик", new LendingOffice(3,101));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Нижний Новгород", new LendingOffice(2,8));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Нижний Тагил", new LendingOffice(6,27));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Новокузнецк", new LendingOffice(4,88));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Новороссийск", new LendingOffice(3,102));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Новосибирск", new LendingOffice(4,89));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Новый Уренгой", new LendingOffice(6,28));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Омск", new LendingOffice(4,91));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Оренбург", new LendingOffice(2,9));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Пенза", new LendingOffice(2,11));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Пермь", new LendingOffice(6,31));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Петрозаводск", new LendingOffice(1,74));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Петропавловск-Камчатский", new LendingOffice(5,43));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Псков", new LendingOffice(1,75));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Пятигорск", new LendingOffice(3,103));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Ростов-на-Дону", new LendingOffice(3,104));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Рязань", new LendingOffice(7,59));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Самара", new LendingOffice(2,12));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Санкт-Петербург", new LendingOffice(1,76));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Саратов", new LendingOffice(2,14));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Смоленск", new LendingOffice(7,60));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Сочи", new LendingOffice(3,105));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Ставрополь", new LendingOffice(3,106));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Сургут", new LendingOffice(6,32));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Сыктывкар", new LendingOffice(1,77));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Тамбов", new LendingOffice(7,62));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Тверь", new LendingOffice(7,63));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Томск", new LendingOffice(4,92));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Тула", new LendingOffice(7,64));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Тюмень", new LendingOffice(6,34));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Улан-Удэ", new LendingOffice(4,93));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Ульяновск", new LendingOffice(2,17));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Уфа", new LendingOffice(2,18));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Хабаровск", new LendingOffice(5,45));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Ханты-Мансийск", new LendingOffice(6,35));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Чебоксары", new LendingOffice(2,19));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Челябинск", new LendingOffice(6,36));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Черкесск", new LendingOffice(3,107));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Чита", new LendingOffice(4,94));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Южно-Сахалинск", new LendingOffice(5,46));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Якутск", new LendingOffice(5,47));
        data.put("Кредитно-кассовый офис ОАО «ОТП Банк» в г. Ярославль", new LendingOffice(7,65));
        data.put("Московский ККО", new LendingOffice(7,57));
    }

    public static void main(String[] args) throws Exception {
        final List<Point> points = loadObjectList(Point.class, "./POS1.txt");
        final CsvSchema schema = CSV_MAPPER.schemaFor(Point.class)
                .withHeader()
                .withColumnSeparator(';');

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SequenceWriter sequenceWriter = CSV_MAPPER.writerFor(Point.class)
                .with(schema)
                .writeValues(new File("./orderLines.csv"));

        points.stream()
                .filter(p -> p.TRADE_OUTLET_CODE != null && p.LATITUDE != null && p.LONGITUDE != null && data.containsKey(p.INTERNAL_ORG_ID))
                .map(point -> {

                    String lowercaseName = point.TRADE_OUTLET_NM.toLowerCase();
                    if (lowercaseName.contains("ipos")) {
                        point.TRADE_OUTLET_NM = "Стойка оформления ОТП Банка";
                        return point;
                    }

                    if (lowercaseName.contains("днс")) {
                        point.TRADE_OUTLET_NM = "Магазин ДНС";
                        return point;
                    }

                    if (!point.TRADING_NETWORK_CD.isBlank()) {
                        point.TRADE_OUTLET_NM = point.TRADING_NETWORK_CD;
                        return point;
                    }

                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replace("14-09", "");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replace("_", " ");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll("\\.\\.", ".");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll(",,", ",");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll("\\)", ") ");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll("\\(", " (");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll("\\.", ". ");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll("\\s+\\.", ". ");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll(",", ", ");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll("\\s+,", ", ");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll("\\s+\\)", ") ");
                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.replaceAll("\\(\\s+", " (");

                    int caseCount = 1;
                    char[] chars = point.TRADE_OUTLET_NM.toCharArray();
                    for (int i = 0; i < chars.length; i++) {
                        if (chars[i] == '"') {
                            chars[i] = caseCount % 2 == 0 ? '»' : '«';
                            caseCount++;
                        }
                    }
                    point.TRADE_OUTLET_NM = new String(chars);

                    point.TRADE_OUTLET_NM = point.TRADE_OUTLET_NM.trim().replaceAll("\\s+", " ");

                    return point;
                })
//                .forEach(p -> System.out.println(p.TRADE_OUTLET_NM));
                .forEach(value -> {
                    try {
                        sequenceWriter.write(value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

//                .map(old.PosParser::toInsert)
//                .forEach(System.out::println);
    }

    String decodeText(String input, String encoding) throws IOException {
        return
                new BufferedReader(
                        new InputStreamReader(
                                new ByteArrayInputStream(input.getBytes()),
                                Charset.forName(encoding)))
                        .readLine();
    }

    private static String toInsert(Point point) {
        final LendingOffice lo = data.get(point.INTERNAL_ORG_ID);
        final String type = point.TRADE_OUTLET_CLASSIFICATION.toLowerCase().contains("ipos") ? "IPOS" : "POS";
        final String workingHours = workingHours(mapper, point.WRK_HRS_MON_TO_FRI_START, point.WRK_HRS_MON_TO_FRI_END,
                point.WRK_HRS_SAT_START, point.WRK_HRS_SAT_END, point.WRK_HRS_SUN_START, point.WRK_HRS_SUN_END);

        return String.format("INSERT INTO signing_point (regcenter_id, lending_office_id, type, code, name, address, location_message, latitude, longitude, working_hours) VALUES (%s, %s, '%s', '%s', '%s', '%s', '%s', %s, %s, '%s');",
                lo.regCenterId, lo.lendingOfficeId, type, point.TRADE_OUTLET_CODE, point.TRADE_OUTLET_NM,
                Optional.ofNullable(point.ADDRESS_LINE_TXT).orElse(""),
                Optional.ofNullable(point.ROUTE_TXT).orElse(""), point.LATITUDE, point.LONGITUDE, workingHours);
    }

    @SneakyThrows
    private static String workingHours(ObjectMapper mapper, String WRK_HRS_MON_TO_FRI_START, String WRK_HRS_MON_TO_FRI_END,
                                       String WRK_HRS_SAT_START, String WRK_HRS_SAT_END,
                                       String WRK_HRS_SUN_START, String WRK_HRS_SUN_END) {
        final List<WorkingHours> hours = new ArrayList<>();

        WRK_HRS_MON_TO_FRI_START = WRK_HRS_MON_TO_FRI_START.replaceFirst("01.01.0001", "").trim();
        WRK_HRS_MON_TO_FRI_END = WRK_HRS_MON_TO_FRI_END.replaceFirst("01.01.0001", "").trim();
        if (!WRK_HRS_MON_TO_FRI_START.isEmpty() && !WRK_HRS_MON_TO_FRI_END.isEmpty()) {
            hours.add(new WorkingHours(DayOfWeek.MONDAY, WRK_HRS_MON_TO_FRI_START, WRK_HRS_MON_TO_FRI_END));
            hours.add(new WorkingHours(DayOfWeek.TUESDAY, WRK_HRS_MON_TO_FRI_START, WRK_HRS_MON_TO_FRI_END));
            hours.add(new WorkingHours(DayOfWeek.WEDNESDAY, WRK_HRS_MON_TO_FRI_START, WRK_HRS_MON_TO_FRI_END));
            hours.add(new WorkingHours(DayOfWeek.THURSDAY, WRK_HRS_MON_TO_FRI_START, WRK_HRS_MON_TO_FRI_END));
            hours.add(new WorkingHours(DayOfWeek.FRIDAY, WRK_HRS_MON_TO_FRI_START, WRK_HRS_MON_TO_FRI_END));
        }

        WRK_HRS_SAT_START = WRK_HRS_SAT_START.replaceFirst("01.01.0001", "").trim();
        WRK_HRS_SAT_END = WRK_HRS_SAT_END.replaceFirst("01.01.0001", "").trim();
        if (!WRK_HRS_SAT_START.isEmpty() && !WRK_HRS_SAT_END.isEmpty()) {
            hours.add(new WorkingHours(DayOfWeek.SATURDAY, WRK_HRS_SAT_START, WRK_HRS_SAT_END));
        }

        WRK_HRS_SUN_START = WRK_HRS_SUN_START.replaceFirst("01.01.0001", "").trim();
        WRK_HRS_SUN_END = WRK_HRS_SUN_END.replaceFirst("01.01.0001", "").trim();
        if (!WRK_HRS_SUN_START.isEmpty() && !WRK_HRS_SUN_END.isEmpty()) {
            hours.add(new WorkingHours(DayOfWeek.SUNDAY, WRK_HRS_SUN_START, WRK_HRS_SUN_END));
        }

        return hours.isEmpty() ? null : mapper.writeValueAsString(hours);
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

    @AllArgsConstructor
    public static class LendingOffice {
        public int regCenterId;
        public int lendingOfficeId;
    }

    @JsonPropertyOrder({"TRADE_OUTLET_ID", "TRADE_OUTLET_CODE", "INTERNAL_ORG_ID", "EXTERNAL_ORG_ID", "EXTERNAL_ORG_NM", "TRADING_NETWORK_CD", "BUSH_HOOKUP", "TRADE_OUTLET_NM", "TRADE_OUTLET_DESC", "REGION_NM", "REGION_OFFICE", "PHONE_NO", "EMAIL_TXT", "CONTACT_PERSON_NM", "EMPLOYEE_ID", "MANAGER_PHONE_NO", "ANNUAL_TURNOVER_AMT", "DISCOUNT_RATE_PCT", "REMOTENESS_NUM", "TURNOVER_CATEGORY_CD", "CONNECTION_TYPE_CD", "AUHTORISATION_TYPE_CD", "REGISTRATION_DT", "EQUIPMENT_TYPE_CD", "GROSS_AREA", "WEB_CAM_FLG", "ULTRAVIOL_DET_FLG", "DESK_CNT", "TRADE_OUTLET_STATUS_CD", "PROCESSED_DTTM", "LOAD_ID", "RECORD_STATUS", "TT_SPECIES", "RESPONSE_PERSON_NM", "ACTIVITY_TT", "CREATED_BY_PERSON_NM", "CREATED_DTTM", "MDSE_CATEGORY_NM", "COMMON_AGREEMENT_FLG", "TRADE_ORGANIZATION_ID", "CRM_KKO_INTERNAL_ORG_ID", "CRM_KKO_INTERNAL_CODE", "ONLINE_STORE_FLG", "PARENT_TRADE_OUTLET_NM", "PARENT_TRADE_OUTLET_CODE", "TRADE_OUTLET_CLASSIFICATION", "RESP_AGENT_SALES", "RESP_PERSON", "RESP_PARTNER_SALES", "RESP_AGENT_SALES_POSITION", "RESP_PERSON_POSITION", "RESP_PARTNER_SALES_POSITION", "RESP_AGENT_SALES_CODE", "RESP_PERSON_CODE", "RESP_PARTNER_SALES_CODE", "RESP_AGENT_SALES_LOGIN", "RESP_PERSON_LOGIN", "RESP_PARTNER_SALES_LOGIN", "EXT_SYSTEM_TRADE_OUTLET_ID", "CITY", "POSTCODE", "STREET", "HOUSE", "CORPUS", "OFFICE", "CHAIRS_CNT", "NOTEBOOKS_CNT", "PRINTERS_CNT", "RACKS_CNT", "ACTIVITY_DATE", "CREATED_BY_PERSON_NO", "MANAGER_EMAIL_TXT", "PRODUCT_LINES_TXT", "KLADR", "ADDRESS_LINE_TXT", "STATE_TYPE_DESC", "DISTRICT_TYPE_DESC", "DISTRICT_NM", "CITY_TYPE_DESC", "SETTLEMENT_TYPE_DESC", "SETTLEMENT_NM", "STREET_TYPE_DESC", "STATE_NM", "TRADE_OUTLET_CODE_PRV", "SMS_CODE", "SMS_CODE_CANCEL_REASON", "WRK_HRS_MON_TO_FRI_START", "WRK_HRS_MON_TO_FRI_END", "WRK_HRS_SAT_START", "WRK_HRS_SAT_END", "WRK_HRS_SUN_START", "WRK_HRS_SUN_END", "TRADING_NETWORK_CODE", "SAFE_FLG", "CABINET_FLG", "LATITUDE", "LONGITUDE", "LOCATION_ACCURACY", "FIAS_CODE", "PAPERLESS11_REJECT", "ROUTE_TXT", "CLIENT_NAV_FLG"})
    public static class Point {
        public String TRADE_OUTLET_ID;
        public String TRADE_OUTLET_CODE;
        public String INTERNAL_ORG_ID;
        public String EXTERNAL_ORG_ID;
        public String EXTERNAL_ORG_NM;
        public String TRADING_NETWORK_CD;
        public String BUSH_HOOKUP;
        public String TRADE_OUTLET_NM;
        public String TRADE_OUTLET_DESC;
        public String REGION_NM;
        public String REGION_OFFICE;
        public String PHONE_NO;
        public String EMAIL_TXT;
        public String CONTACT_PERSON_NM;
        public String EMPLOYEE_ID;
        public String MANAGER_PHONE_NO;
        public String ANNUAL_TURNOVER_AMT;
        public String DISCOUNT_RATE_PCT;
        public String REMOTENESS_NUM;
        public String TURNOVER_CATEGORY_CD;
        public String CONNECTION_TYPE_CD;
        public String AUHTORISATION_TYPE_CD;
        public String REGISTRATION_DT;
        public String EQUIPMENT_TYPE_CD;
        public String GROSS_AREA;
        public String WEB_CAM_FLG;
        public String ULTRAVIOL_DET_FLG;
        public String DESK_CNT;
        public String TRADE_OUTLET_STATUS_CD;
        public String PROCESSED_DTTM;
        public String LOAD_ID;
        public String RECORD_STATUS;
        public String TT_SPECIES;
        public String RESPONSE_PERSON_NM;
        public String ACTIVITY_TT;
        public String CREATED_BY_PERSON_NM;
        public String CREATED_DTTM;
        public String MDSE_CATEGORY_NM;
        public String COMMON_AGREEMENT_FLG;
        public String TRADE_ORGANIZATION_ID;
        public String CRM_KKO_INTERNAL_ORG_ID;
        public String CRM_KKO_INTERNAL_CODE;
        public String ONLINE_STORE_FLG;
        public String PARENT_TRADE_OUTLET_NM;
        public String PARENT_TRADE_OUTLET_CODE;
        public String TRADE_OUTLET_CLASSIFICATION;
        public String RESP_AGENT_SALES;
        public String RESP_PERSON;
        public String RESP_PARTNER_SALES;
        public String RESP_AGENT_SALES_POSITION;
        public String RESP_PERSON_POSITION;
        public String RESP_PARTNER_SALES_POSITION;
        public String RESP_AGENT_SALES_CODE;
        public String RESP_PERSON_CODE;
        public String RESP_PARTNER_SALES_CODE;
        public String RESP_AGENT_SALES_LOGIN;
        public String RESP_PERSON_LOGIN;
        public String RESP_PARTNER_SALES_LOGIN;
        public String EXT_SYSTEM_TRADE_OUTLET_ID;
        public String CITY;
        public String POSTCODE;
        public String STREET;
        public String HOUSE;
        public String CORPUS;
        public String OFFICE;
        public String CHAIRS_CNT;
        public String NOTEBOOKS_CNT;
        public String PRINTERS_CNT;
        public String RACKS_CNT;
        public String ACTIVITY_DATE;
        public String CREATED_BY_PERSON_NO;
        public String MANAGER_EMAIL_TXT;
        public String PRODUCT_LINES_TXT;
        public String KLADR;
        public String ADDRESS_LINE_TXT;
        public String STATE_TYPE_DESC;
        public String DISTRICT_TYPE_DESC;
        public String DISTRICT_NM;
        public String CITY_TYPE_DESC;
        public String SETTLEMENT_TYPE_DESC;
        public String SETTLEMENT_NM;
        public String STREET_TYPE_DESC;
        public String STATE_NM;
        public String TRADE_OUTLET_CODE_PRV;
        public String SMS_CODE;
        public String SMS_CODE_CANCEL_REASON;
        public String WRK_HRS_MON_TO_FRI_START;
        public String WRK_HRS_MON_TO_FRI_END;
        public String WRK_HRS_SAT_START;
        public String WRK_HRS_SAT_END;
        public String WRK_HRS_SUN_START;
        public String WRK_HRS_SUN_END;
        public String TRADING_NETWORK_CODE;
        public String SAFE_FLG;
        public String CABINET_FLG;
        public Double LATITUDE;
        public Double LONGITUDE;
        public String LOCATION_ACCURACY;
        public String FIAS_CODE;
        public String PAPERLESS11_REJECT;
        public String ROUTE_TXT;
        public String CLIENT_NAV_FLG;
    }

    @Data
    public static class WorkingHours {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");

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
