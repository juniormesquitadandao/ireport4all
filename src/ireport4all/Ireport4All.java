package ireport4all;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class Ireport4All {

    public static void main(String[] args) throws JRException, UnsupportedEncodingException, FileNotFoundException {
        String json = "{\"data\":[{\"name\":\"Name\"}]}";
        InputStream inputStream = new ByteArrayInputStream(json.getBytes("UTF-8"));
        JsonDataSource jRDataSource = new JsonDataSource(inputStream, "data");

        Map<String, Object> params = new HashMap<>();

        File jasper = new File("demo/report1.jasper");
        String sourceFileName = jasper.getAbsolutePath();
        System.out.println(sourceFileName);
        JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, jRDataSource);
        JasperViewer jasperViewer = new JasperViewer(jasperPrint);
        jasperViewer.setVisible(true);
    }

}