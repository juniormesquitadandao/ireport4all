package juniormesquitadandao.github.io.report4all;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import net.sf.jasperreports.engine.util.JRDataUtils;
import org.codehaus.groovy.util.ListHashMap;

public class Report extends AbstractSampleApp {

    private final String[] args;
    private File jasper;
    private File pdf;
    private Locale reportLocale;
    private final Map<String, Object> params = new ListHashMap<>();
    private JsonDataSource jsonDataSource;
    private JasperPrint jasperPrint;

    public Report(String[] args) {
        this.args = args;
    }

    public void init() throws JRException {
        extractValues();
    }

    public void fill() throws JRException {
        params.put(JRParameter.REPORT_DATA_SOURCE, jsonDataSource);
        params.put(JRParameter.REPORT_LOCALE, reportLocale);
        params.put(JRParameter.REPORT_TIME_ZONE, JRDataUtils.getTimeZone("UTC"));
        params.put("SUBREPORT_DIR", "");

        jasperPrint = JasperFillManager.fillReport(jasper.getAbsolutePath(), params);
    }

    public void export() throws JRException {
        JasperExportManager.exportReportToPdfFile(jasperPrint, pdf.getAbsolutePath());
    }

    @Override
    public void test() throws JRException {
        init();
        fill();
        export();
    }

    public static void main(String[] args) throws IOException, ParseException {
        File jasper = new File("reports/demo.jasper");

        File json = new File("reports/demo.json");
        String data = new String(Files.readAllBytes(json.toPath()), Charset.forName("UTF-8"));

        File pdf = new File("reports/demo-" + Calendar.getInstance().getTimeInMillis() + ".pdf");

        String report = "{\n"
                + "\"jasper\":\"" + jasper.getAbsolutePath() + "\",\n"
                + "\"data\":" + data + ",\n"
                + "\"locale\":\"pt_BR\",\n"
                + "\"pdf\":\"" + pdf.getAbsolutePath() + "\"\n"
                + "}";

        String base64 = DatatypeConverter.printBase64Binary(report.getBytes("UTF-8"));

        args = new String[]{base64};

        main(new Report(args), new String[]{"test"});
    }

    private void extractValues() throws JRException {
        try {
            String decoded = new String(DatatypeConverter.parseBase64Binary(args[0]), "UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(decoded, new TypeReference<Map<String, Object>>() {
            });

            extractJasper(map);
            extractData(map);
            extractLocale(map);
            extractPdf(map);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new JRException("require json in base64");
        } catch (JsonParseException e) {
            throw new JRException("invalid json");
        } catch (Exception e) {
            throw new JRException(e.toString(), e.getCause());
        }
    }

    private void extractJasper(Map<String, Object> map) {
        jasper = new File((String) map.get("jasper"));
    }

    private void extractData(Map<String, Object> map) throws JsonProcessingException, JRException {
        byte[] bytes = new ObjectMapper().writeValueAsBytes(map.get("data"));
        jsonDataSource = new JsonDataSource(new ByteArrayInputStream(bytes));
    }

    private void extractLocale(Map<String, Object> map) {
        String localeString = (String) map.get("locale");
        reportLocale = JRDataUtils.getLocale(localeString);
    }

    private void extractPdf(Map<String, Object> map) {
        String pdfString = (String) map.get("pdf");

        if (pdfString == null) {
            pdf = new File(jasper.getAbsolutePath().replace(".jasper", ".pdf"));
        } else {
            pdf = new File(pdfString);
        }

        pdf.delete();
    }
}