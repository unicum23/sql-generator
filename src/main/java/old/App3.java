package old;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class App3 {

    public static void main(String[] args) {
        List<OrlRecord> migrations = loadObjectList(OrlRecord.class, "./orl.csv");

        List<String> codes = migrations.stream()
                .filter(r -> "1".equals(r.getIsOrel()))
                .map(r -> r.getCode().startsWith("N") ? r.getCode().substring(1) : r.getCode())
                .collect(Collectors.toList());

        Iterator<String> iterator = codes.iterator();
        for (int i = 1; iterator.hasNext(); i++) {
            System.out.print("'" + iterator.next() + "'");
            if (iterator.hasNext()) {
                System.out.print(", ");
            }
            if (i % 6 == 0) {
                System.out.println("");
            }
        }

//        List<String> toCodes = migrations.stream()
//                .filter(r -> "1".equals(r.getIsOrel()))
//                .map(OrlRecord::getCodeTo)
//                .distinct()
//                .collect(Collectors.toList());
//
//        Iterator<String> iterator = toCodes.iterator();
//        for (int i = 1; iterator.hasNext(); i++) {
//            System.out.print("'" + iterator.next() + "'");
//            if (iterator.hasNext()) {
//                System.out.print(", ");
//            }
//            if (i % 6 == 0) {
//                System.out.println("");
//            }
//        }
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
    public static class OrlRecord {
        private String number;
        private String code;
        private String name;
        private String regcentr;
        private String lendingOffice;
        private String comNet;
        private String status;
        private String order;
        private String clazz;
        private String isEshop;
        private String created;
        private String city;
        private String activity;
        private String registrationDate;
        private String codeTo;
        private String nameTo;
        private String agreementType;
        private String toAgreementStatus;
        private String isBroker;
        private String agreementDate;
        private String toAgreementDate;
        private String isUnionAgreement;
        private String isOrel;
        private String tt;
        private String ttt;
    }
}
