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
                return file.getName().endsWith(".jrxml") && isChanged(file);
            }
        });

        for (File jrxml : jrxmls) {
            jasperCompileManager.compileToFile(jrxml.getAbsolutePath());
            jrxml.setLastModified(0);
        }
        System.err.println("Compiling time : " + (System.currentTimeMillis() - start));
    }

    public void fill() throws JRException {
        long start = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put(JsonQueryExecuterFactory.JSON_DATE_PATTERN, "yyyy-MM-dd");
        params.put(JsonQueryExecuterFactory.JSON_NUMBER_PATTERN, "#,##0.##");
        params.put(JsonQueryExecuterFactory.JSON_LOCALE, Locale.ENGLISH);
        params.put(JRParameter.REPORT_LOCALE, Locale.US);

        File jasper = new File("reports/JsonCustomersReport.jasper");
        if (isChanged(jasper)) {
            JasperFillManager.fillReportToFile(jasper.getAbsolutePath(), params);
            jasper.setLastModified(0);
        }
        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }

    public void export() throws JRException {
        long start = System.currentTimeMillis();

        File jrprint = new File("reports/JsonCustomersReport.jrprint");
        if (isChanged(jrprint)) {
            JasperExportManager.exportReportToPdfFile(jrprint.getAbsolutePath());
            jrprint.setLastModified(0);
        }
        System.err.println("Exporting time : " + (System.currentTimeMillis() - start));
    }

    @Override
    public void test() throws JRException {
        compile();
        fill();
        export();
    }

    private boolean isChanged(File file) {
        return file.lastModified() != 0;
    }

    public static void main(String[] args) throws JRException {
        main(new ReportJson(), new String[]{"test"});
    }
}
