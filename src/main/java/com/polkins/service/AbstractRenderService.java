package com.polkins.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRExporterContext;
import net.sf.jasperreports.export.ExporterConfiguration;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Common logic for rendering services.
 * <p>
 * 1. Find template by templateId in resource directory
 * 2. Generate file by specified exporter and dataModel
 */
@Slf4j
@AllArgsConstructor
abstract class AbstractRenderService<RC extends ReportExportConfiguration, C extends ExporterConfiguration, O extends ExporterOutput, E extends JRExporterContext> implements RenderService {
    private static final String JASPER_REPORT_FORMAT = ".jrxml";

    private final String templatePath;

    /**
     * Exports a file depending on the selected class child [net.sf.jasperreports.engine.JRAbstractExporter]
     *
     * @param templateId name of the document template in directory source with extension .jrxml
     * @param dataModel  any pojo object represented as json string
     * @param exporter   is used for calling [net.sf.jasperreports.engine.JRAbstractExporter.exportReport]
     */
    protected void export(String templateId, String dataModel, JRAbstractExporter<RC, C, O, E> exporter) {
        log.info("Start export method with templateId: $templateId and exporter: $exporter");
        try (var paths = Files.walk(Paths.get(templatePath));
             var byteArrayInputStream = new ByteArrayInputStream(dataModel.getBytes())
        ) {
            var path = paths
                    .filter(p -> p.getFileName().toString().endsWith(templateId + JASPER_REPORT_FORMAT))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Can not found render template: " + templateId + JASPER_REPORT_FORMAT));
            log.info("Found file: " + path);
            var jasperReport = JasperCompileManager.compileReport(path.toString());
            log.info("Report successfully compiled " + jasperReport);
            var jsonDataSource = new JsonDataSource(byteArrayInputStream);
            var jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), jsonDataSource);
            log.info("Report successfully filled" + jasperPrint);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.exportReport();
            log.info("End export method with templateId: $templateId and exporter: $exporter");
        } catch (IOException | JRException e) {
            log.error("Error occurred during export report", e);
        }
    }
}