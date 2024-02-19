package com.polkins.service;

import com.polkins.enumeration.PrintFormat;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterContext;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.PdfExporterConfiguration;
import net.sf.jasperreports.export.PdfReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class PdfRenderingServiceImpl extends AbstractRenderService<PdfReportConfiguration, PdfExporterConfiguration, OutputStreamExporterOutput, JRPdfExporterContext> {
    public PdfRenderingServiceImpl(@Value("${render.jasperreports.template-path}") String templatePath) {
        super(templatePath);
    }

    @Override
    public byte[] render(String templateId, String dataModel) {
        log.info("Start rendering document with templateId: {}", templateId);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            log.info("Assigned output: {}", exporter);

            export(templateId, dataModel, exporter);

            log.info("End rendering document with templateId: {}", templateId);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during render the report", e);
        }
    }

    @Override
    public PrintFormat getFormat() {
        return PrintFormat.PDF;
    }
}
