package juniormesquitadandao.github.io.report4all;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;
import net.sf.jasperreports.engine.util.AbstractSampleApp;

public class ReportJson extends AbstractSampleApp {

    public void compile() throws JRException {
        long start = System.currentTimeMillis();
        JasperReportsContext jasperReportsContext = new SimpleJasperReportsContext();
        JasperCompileManager jasperCompileManager = JasperCompileManager.getInstance(jasperReportsContext);
        File folder = new File("reports");
        File[] jrxmls = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".jrxml");
            }
        });

        for (File jrxml : jrxmls) {
            jasperCompileManager.compileToFile(jrxml.getAbsolutePath());
        }
        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }

    public void fill() throws JRException {
        long start = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(JsonQueryExecuterFactory.JSON_DATE_PATTERN, "yyyy-MM-dd");
        params.put(JsonQueryExecuterFactory.JSON_NUMBER_PATTERN, "#,##0.##");
        params.put(JsonQueryExecuterFactory.JSON_LOCALE, Locale.ENGLISH);
        params.put(JRParameter.REPORT_LOCALE, Locale.US);

        JasperFillManager.fillReportToFile("reports/JsonCustomersReport.jasper", params);
        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }

    public void pdf() throws JRException {
        long start = System.currentTimeMillis();
        JasperExportManager.exportReportToPdfFile("reports/JsonCustomersReport.jrprint");
        System.err.println("PDF creation time : " + (System.currentTimeMillis() - start));
    }

    public void test() throws JRException {
        compile();
        fill();
        pdf();
    }

    public static void main(String[] args) throws JRException {
        main(new ReportJson(), new String[]{"pdf"});
    }
}