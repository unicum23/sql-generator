package shudn;

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
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class UsersImport {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new JSR310Module())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static void main(String[] args) {
        List<User> migrations = loadObjectList(User.class, "shudn/users.csv")
                .stream()
                .map(UsersImport::normalizeShopName)
                .collect(Collectors.toList());

        System.out.println(migrations.size());
        migrations.stream()
                .map(UsersImport::toInsert)
                .skip(0)
                .limit(10000)
                .forEach(System.out::println);
    }

    private static User normalizeShopName(User user) {
        if (user.birthday.length() > 10) user.birthday = user.birthday.substring(0, 10);

        return user;
    }

    private static String toInsert(User user) {
        return String.format("update users set birthdate = TO_DATE('%s', 'DD.MM.YYYY') where login = lower('%s');",
                user.birthday, user.login);
    }

    @SneakyThrows
    public static <T> List<T> loadObjectList(Class<T> type, String fileName) {

        CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';');
        CsvMapper mapper = new CsvMapper();
        File file = new ClassPathResource(fileName, UsersImport.class.getClassLoader()).getFile();
        MappingIterator<T> readValues =
                mapper.reader(type)
                        .with(bootstrapSchema)
                        .readValues(file);
        return readValues.readAll();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        String login;
        String birthday;
    }

}
