package shudn.report;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportMain {
    private static final String DB_URL = "jdbc:postgresql://srv-smart-db.isb:5432/core_service?currentSchema=core_service";
    private static final String DB_LOGIN = "db_admin";
    private static final String DB_PASSWORD = "***";
    private static final String WEEK_COLUMN = "week";
    private static final String COUNT_COLUMN = "count";
    private static final String LINE = "------------------";
    public static String FROM_DATE = "'2023-02-06 00:00:00'";
    public static String TO_DATE = "'2023-05-05 00:00:00'";

    public static String CONDITION_APPROVAL = "and bps.uuid in (select bpa.uuid\n" +
            "                   from business_process_audit bpa\n" +
            "                   where bpa.status in ('DECISION_APPROVAL'))";

    public static String CONDITION_PAID = "  and bps.uuid in (select bpa.uuid\n" +
            "                   from business_process_audit bpa\n" +
            "                   where bpa.status in ('AGREEMENT_AUTHORIZED', 'AGREEMENT_PAID'))";
    public static String CONDITION_SIEBEL = " and bps.is_new_conveyor = false ";
    public static String CONDITION_NEW_CONVEYOR = " and bps.is_new_conveyor = true ";
    public static String CONDITION_PAP11 = "  and opt.technology_type = 'PAPERLESS_11'";
    public static String CONDITION_PAP20 = " and opt.technology_type = 'PAPERLESS_20' ";
    public static String CONDITION_PAPERLESS = "  and agr.sign_method = 'PAPERLESS' ";
    public static String CONDITION_PAPER_TECH = " and agr.sign_method = 'PAPER_TECH' ";

    public static String JOIN_AGREEMENT = " join agreement agr on agr.agreement_number = bps.agreement_number ";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_LOGIN, DB_PASSWORD)) {
            System.out.format("Период: с %s по %s\n\n", FROM_DATE, TO_DATE);
            allRequests(conn);

            siebelAllRequests(conn);
            siebelPap11Requests(conn);
            siebelPapTechRequests(conn);

            conveyorAllRequests(conn);
            conveyorPap11Requests(conn);
            conveyorPapTechRequests(conn);
            conveyorPap20Requests(conn);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void allRequests(Connection conn) throws SQLException {
        System.out.println("Все заявки (конвейер и зибель, любые методы подписания)");

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getAllRequestsSql("", ""));
            ResultSet resultSet = preparedStatement.executeQuery();
            printWeeks(resultSet);

            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Принято:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_APPROVAL, ""));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Одобрено:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_PAID, JOIN_AGREEMENT));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Выдано:");

            System.out.println(LINE);
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void siebelAllRequests(Connection conn) throws SQLException {
        System.out.println("Заявки чз зибель (все/с договором и без)");

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_SIEBEL, ""));
            ResultSet resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Принято:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_APPROVAL + CONDITION_SIEBEL,
                    ""));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Одобрено:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_PAID + CONDITION_SIEBEL,
                    JOIN_AGREEMENT));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Выдано:");

            System.out.println(LINE);
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void siebelPap11Requests(Connection conn) throws SQLException {
        System.out.println("Заявки чз зибель (сформирован договор по paperless_11)");

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_APPROVAL + CONDITION_SIEBEL + CONDITION_PAP11 + CONDITION_PAPERLESS,
                    JOIN_AGREEMENT));
            ResultSet resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Одобрено:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_PAID + CONDITION_SIEBEL + CONDITION_PAP11 + CONDITION_PAPERLESS,
                    JOIN_AGREEMENT));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Выдано:");

            System.out.println(LINE);
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void siebelPapTechRequests(Connection conn) throws SQLException {
        System.out.println("Заявки чз зибель (сформирован договор по paper_tech)");

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_APPROVAL + CONDITION_SIEBEL + CONDITION_PAPER_TECH,
                    JOIN_AGREEMENT));
            ResultSet resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Одобрено:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_PAID + CONDITION_SIEBEL + CONDITION_PAPER_TECH,
                    JOIN_AGREEMENT));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Выдано:");

            System.out.println(LINE);
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void conveyorAllRequests(Connection conn) throws SQLException {
        System.out.println("Заявки чз конвейер (все/с договором и без)");

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_NEW_CONVEYOR, ""));
            ResultSet resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Принято:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_APPROVAL + CONDITION_NEW_CONVEYOR,
                    ""));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Одобрено:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_PAID + CONDITION_NEW_CONVEYOR,
                    JOIN_AGREEMENT));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Выдано:");

            System.out.println(LINE);
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void conveyorPap11Requests(Connection conn) throws SQLException {
        System.out.println("Заявки чз конвейер (сформирован договор по paperless_11)");

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_APPROVAL + CONDITION_NEW_CONVEYOR + CONDITION_PAP11 + CONDITION_PAPERLESS,
                    JOIN_AGREEMENT));
            ResultSet resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Одобрено:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_PAID + CONDITION_NEW_CONVEYOR + CONDITION_PAP11 + CONDITION_PAPERLESS,
                    JOIN_AGREEMENT));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Выдано:");

            System.out.println(LINE);
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void conveyorPapTechRequests(Connection conn) throws SQLException {
        System.out.println("Заявки чз конвейер (сформирован договор по paper_tech)");

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_APPROVAL + CONDITION_NEW_CONVEYOR + CONDITION_PAPER_TECH,
                    JOIN_AGREEMENT));
            ResultSet resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Одобрено:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_PAID + CONDITION_NEW_CONVEYOR + CONDITION_PAPER_TECH,
                    JOIN_AGREEMENT));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Выдано:");

            System.out.println(LINE);
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void conveyorPap20Requests(Connection conn) throws SQLException {
        System.out.println("Заявки чз конвейер (сформирован договор по paperless_20)");

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_APPROVAL + CONDITION_NEW_CONVEYOR + CONDITION_PAP20 + CONDITION_PAPERLESS,
                    JOIN_AGREEMENT));
            ResultSet resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Одобрено:");

            preparedStatement = conn.prepareStatement(getAllRequestsSql(CONDITION_PAID + CONDITION_NEW_CONVEYOR + CONDITION_PAP20 + CONDITION_PAPERLESS,
                    JOIN_AGREEMENT));
            resultSet = preparedStatement.executeQuery();
            printResult(resultSet, "Выдано:");

            System.out.println(LINE);
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printResult(ResultSet resultSet, String head) throws SQLException {
        System.out.println(head);
        while (resultSet.next()) {
            System.out.println(resultSet.getInt(COUNT_COLUMN));
        }
        System.out.println();
    }

    private static void printWeeks(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            System.out.println(resultSet.getInt(WEEK_COLUMN));
        }
        System.out.println();
    }

    private static String getAllRequestsSql(String additionCondition, String additionJoin) {
        return "select date_part('week', bps.create_date::date) AS week, count(*)\n" +
                "from business_process_state bps\n" +
                "         join opty_request opt on opt.opty_id = bps.opty_id\n" +
                additionJoin +
                "where bps.create_date between " + FROM_DATE + " and " + TO_DATE + "\n" +
                "  and bps.opty_id IS NOT NULL\n" +
                "  and bps.uuid in (select bpa.uuid\n" +
                "                   from business_process_audit bpa\n" +
                "                   where bpa.status in ('OPTY_CREATED') and user_name = 'anonymous')\n" +
                additionCondition +
                "GROUP BY week\n" +
                "ORDER BY week;";
    }
}
