package old;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.List;

public class RcMigrationApp {

    public static void main(String[] args) {
        loadObjectList(MigrationData.class, "./rc_migration.csv").stream()
                .map(i ->
                        "UPDATE signing_point SET regcenter_id = " + i.newRegcenterId + " WHERE regcenter_id = " + i.prevRegcenterId + " AND lending_office_id = " + i.lendingOfficeId + ";")
                .forEach(System.out::println);
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
    public static class MigrationData {
        private String lendingOfficeName;
        private Long lendingOfficeId;
        private String prevRegcenterName;
        private Long prevRegcenterId;
        private String newRegcenterName;
        private Long newRegcenterId;
    }
}
