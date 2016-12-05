package juniormesquitadandao.github.io.report4all;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;
import net.sf.jasperreports.engine.util.AbstractSampleApp;

public class Report extends AbstractSampleApp {

    private final String[] args;
    private File jasper;
    private File jrprint;
    private File pdf;
    private File json;
    private File error;
    private Locale outputLanguage;

    public Report(String[] args) {
        this.args = args;
    }

    public void init() throws JRException {
        try {
            jasper = new File(args[0]);
            json = new File(jasper.getAbsolutePath().replace(".jasper", ".json"));
            jrprint = new File(jasper.getAbsolutePath().replace(".jasper", ".jrprint"));
            pdf = new File(jasper.getAbsolutePath().replace(".jasper", ".pdf"));
            error = new File(jasper.getAbsolutePath().replace(".jasper", ".pdf"));

            pdf.delete();

            outputLanguage = new Locale(args[1].split("-")[0], args[1].split("-")[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new JRException("Required Args: jasperPath, outputLanguage");
        } catch (Exception e) {
            throw new JRException(e.toString(), e.getCause());
        }
    }

    public void fill() throws JRException {
        Map<String, Object> params = new HashMap<>();
        params.put("net.sf.jasperreports.json.source", json.getAbsolutePath());
//        params.put(JsonQueryExecuterFactory.JSON_DATE_PATTERN, "yyyy-MM-dd");
//        params.put(JsonQueryExecuterFactory.JSON_NUMBER_PATTERN, "#,##0.##");

        params.put(JsonQueryExecuterFactory.JSON_LOCALE, Locale.ENGLISH);
//        params.put(JsonQueryExecuterFactory.JSON_LOCALE, new Locale("pt", "BR"));
        params.put(JRParameter.REPORT_LOCALE, outputLanguage);
//        params.put(JRParameter.REPORT_LOCALE, Locale.US);

        JasperFillManager.fillReportToFile(jasper.getAbsolutePath(), params);
    }

    public void export() throws JRException {
        JasperExportManager.exportReportToPdfFile(jrprint.getAbsolutePath());
    }

    @Override
    public void test() throws JRException {
        try {
            init();
            fill();
            export();
        } catch (JRException e) {
            try {
                Files.write(error.toPath(), e.toString().getBytes("UFT-8"), StandardOpenOption.CREATE);
            } catch (IOException ex) {
            }
            throw new JRException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("reports/demo.jasper");

        args = new String[]{file.getAbsolutePath(), "pt-BR"};

        main(new Report(args), new String[]{"test"});
    }

//    public static void main(String[] args) {
//        main(new Report(args), new String[]{"test"});
//    }
}
