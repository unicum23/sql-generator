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
import java.util.List;

public class App2 {

    public static void main(String[] args) {
        List<MigrationConfig> migrations = loadObjectList(MigrationConfig.class, "./migration.csv");

        migrations.stream()
                .map(o -> String.format("UPDATE organization SET lending_office_id_backup = lending_office_id, lending_office_id = %s WHERE lending_office_id = %s;",
                        o.getId(), o.getOldId()))
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MigrationConfig {
        private String oldName;
        private String oldCode;
        private String oldSibelId;
        private String oldId;
        private String name;
        private String code;
        private String sibelId;
        private String id;
    }
}
