package juniormesquitadandao.github.io.report4all;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
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

    private final String[] args;

    private File file;
    private File temp;
    private long approxTempSize;
    private String checksum;

    public Report(String[] args) {
        this.args = args;
    }

    public void init() throws JRException {
        try {
            file = new File(args[0].replace(".jrxml", ""));
            temp = new File(args[1] + File.separator + "report4all");
            approxTempSize = Long.valueOf(args[2]);

            temp.mkdirs();
            cleanTemp();
        } catch(ArrayIndexOutOfBoundsException e){
            throw new JRException("Required Args: filePath, tempPath, approxTempSize.");
        } catch (Exception e) {
            throw new JRException(e.toString(), e.getCause());
        }
    }

    public void checksum() throws JRException {
        try {
            File json = toTempFile(new File(file.getAbsolutePath() + ".json"));
            byte[] bytes = Files.readAllBytes(json.toPath());
            byte[] digest = MessageDigest.getInstance("MD5").digest(bytes);
            checksum = DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (Exception e) {
            throw new JRException(e.toString(), e.getCause());
        }
    }

    public void compile() throws JRException {
        JasperReportsContext jasperReportsContext = new SimpleJasperReportsContext();
        JasperCompileManager jasperCompileManager = JasperCompileManager.getInstance(jasperReportsContext);

        for (File jrxml : getSourceFiles("jrxml")) {
            File jasper = new File(toTempFile(jrxml).getAbsolutePath().replace(".jrxml", ".jasper"));
            if (isChanged(jrxml)) {
                jasper.delete();
                jasperCompileManager.compileToFile(jrxml.getAbsolutePath(), jasper.getAbsolutePath());
                jrxml.setLastModified(0);
                deleteJrprintsAndPdfs();
            } else if (isNew(jasper)) {
                jasperCompileManager.compileToFile(jrxml.getAbsolutePath(), jasper.getAbsolutePath());
                deleteJrprintsAndPdfs();
            }
        }
    }

    public void fill() throws JRException {
        Map<String, Object> params = new HashMap<>();
        params.put(JsonQueryExecuterFactory.JSON_DATE_PATTERN, "yyyy-MM-dd");
        params.put(JsonQueryExecuterFactory.JSON_NUMBER_PATTERN, "#,##0.##");
        params.put(JsonQueryExecuterFactory.JSON_LOCALE, Locale.ENGLISH);
        params.put(JRParameter.REPORT_LOCALE, Locale.US);

        File jasper = new File(toTempFile(file).getAbsolutePath() + ".jasper");
        File jrprint = new File(toTempFile(file).getAbsolutePath() + "-" + checksum + ".jrprint");
        if (isChanged(jasper)) {
            JasperFillManager.fillReportToFile(jasper.getAbsolutePath(), jrprint.getAbsolutePath(), params);
            jasper.setLastModified(0);
        } else if (isNew(jrprint)) {
            JasperFillManager.fillReportToFile(jasper.getAbsolutePath(), jrprint.getAbsolutePath(), params);
        }
    }

    public void export() throws JRException {
        File jrprint = new File(toTempFile(file).getAbsolutePath() + "-" + checksum + ".jrprint");
        File pdf = new File(toTempFile(file).getAbsolutePath() + "-" + checksum + ".pdf");
        if (isNew(pdf)) {
            JasperExportManager.exportReportToPdfFile(jrprint.getAbsolutePath(), pdf.getAbsolutePath());
        }

        writeResult(pdf);
    }

    @Override
    public void test() throws JRException {
        init();
        checksum();
        compile();
        fill();
        export();
    }

    private void cleanTemp() {
        if (getTempSize() > approxTempSize) {
            deleteJrprintsAndPdfs();
        }
    }

    private long getTempSize() {
        long size = 0;

        for (File jrprint : getTempFiles("jrprint")) {
            size += jrprint.length();
        }
        for (File pdf : getTempFiles("pdf")) {
            size += pdf.length();
        }

        return size;
    }

    private File[] getSourceFiles(final String type) {
        File folder = file.getParentFile();
        File[] files = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith("." + type);
            }
        });

        return files;
    }

    private File[] getTempFiles(final String type) {
        File folder = temp;
        File[] files = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(file.getName() + "-") && file.getName().endsWith("." + type);
            }
        });

        return files;
    }

    private File toTempFile(File sourceFile) {
        return new File(temp.getAbsolutePath() + File.separator + sourceFile.getName());
    }

    private boolean isChanged(File file) {
        return file.lastModified() != 0;
    }

    private boolean isNew(File file) {
        return !file.exists();
    }

    private void deleteJrprintsAndPdfs() {
        for (File jrprint : getTempFiles("jrprint")) {
            jrprint.delete();
        }

        for (File pdf : getTempFiles("pdf")) {
            pdf.delete();
        }
    }

    private void writeResult(File pdf) throws JRException {
        try {
            byte[] bytes = pdf.getAbsolutePath().getBytes("utf-8");
            File result = toTempFile(new File(file.getAbsolutePath() + ".result"));
            Files.write(result.toPath(), bytes, StandardOpenOption.CREATE);
        } catch (Exception e) {
            throw new JRException(e.toString(), e.getCause());
        }
    }

    public static void main(String[] args) {
//        File file = new File("reports/JsonCustomersReport.jrxml");
//        File temp = new File("temp");
//        long approxTempSize = 1024 * 1024;
//
//        args = new String[]{file.getAbsolutePath(), temp.getAbsolutePath(), approxTempSize + ""};

        main(new Report(args), new String[]{"test"});
    }
}