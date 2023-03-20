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

public class App {

    public static void main(String[] args) {
        List<TO> offices = loadObjectList(TO.class, "./tt.csv");
        List<String> codes = offices.stream()
                .map(TO::getCode)
                .collect(Collectors.toList());


        Iterator<String> iterator = codes.iterator();
        for (int i = 1; iterator.hasNext(); i++) {
            System.out.print("'" + iterator.next() + "'");
            if (iterator.hasNext()) {
                System.out.print(", ");
            }
            if (i % 8 == 0) {
                System.out.println("");
            }
        }
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
    public static class TO {
        private String code;
    }
}