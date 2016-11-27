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

public class Report extends AbstractSampleApp {

    private final File file;

    public Report(String path) {
        file = new File(path.replace(".jrxml", ""));
    }

    public void compile() throws JRException {
        long start = System.currentTimeMillis();
        JasperReportsContext jasperReportsContext = new SimpleJasperReportsContext();
        JasperCompileManager jasperCompileManager = JasperCompileManager.getInstance(jasperReportsContext);
        File folder = file.getParentFile();
        File[] jrxmls = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".jrxml");
            }
        });

        for (File jrxml : jrxmls) {
            File jasper = new File(jrxml.getAbsolutePath().replace(".jrxml", ".jasper"));
            if (isChanged(jrxml)) {
                jasper.delete();
                jasperCompileManager.compileToFile(jrxml.getAbsolutePath());
                jrxml.setLastModified(0);
            } else if (isNew(jasper)) {
                jasperCompileManager.compileToFile(jrxml.getAbsolutePath());
            }
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

        File jasper = new File(file.getAbsolutePath() + ".jasper");
        File jrprint = new File(file.getAbsolutePath() + ".jrprint");
        if (isChanged(jasper)) {
            jrprint.delete();
            JasperFillManager.fillReportToFile(jasper.getAbsolutePath(), params);
            jasper.setLastModified(0);
        } else if (isNew(jrprint)) {
            JasperFillManager.fillReportToFile(jasper.getAbsolutePath(), params);
        }

        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }

    public void export() throws JRException {
        long start = System.currentTimeMillis();

        File jrprint = new File(file.getAbsolutePath() + ".jrprint");
        File pdf = new File(file.getAbsolutePath() + ".pdf");
        if (isChanged(jrprint)) {
            pdf.delete();
            JasperExportManager.exportReportToPdfFile(jrprint.getAbsolutePath());
            jrprint.setLastModified(0);
        } else if (isNew(pdf)) {
            JasperExportManager.exportReportToPdfFile(jrprint.getAbsolutePath());
        }

        System.err.println("Exporting time : " + (System.currentTimeMillis() - start));
    }

    public void test() throws JRException {
        compile();
        fill();
        export();
    }

    private boolean isChanged(File file) {
        return file.lastModified() != 0;
    }

    private boolean isNew(File file) {
        return !file.exists();
    }

//    private boolean isChanged(File file) throws IOException, NoSuchAlgorithmException {
//        byte[] bytes = Files.readAllBytes(file.toPath());
//        byte[] digest = MessageDigest.getInstance("MD5").digest(bytes);
//
//        String expected = "b1fa6524a0a9639abdbe91638e6429e7";
//        String actual = DatatypeConverter.printHexBinary(digest).toLowerCase();
//
//        return !expected.equals(actual);
//    }
    public static void main(String[] args) {
        File file = new File("reports/JsonCustomersReport.jrxml");
        args = new String[]{file.getAbsolutePath()};

        main(new Report(args[0]), new String[]{"test"});
    }
}