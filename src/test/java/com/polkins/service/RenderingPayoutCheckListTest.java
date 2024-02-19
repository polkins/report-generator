package com.polkins.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polkins.enumeration.PrintFormat;
import com.polkins.model.RenderParameters;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(classes = {PdfRenderingServiceImpl.class, ObjectMapper.class})
class RenderingPayoutCheckListTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PdfRenderingServiceImpl pdfRenderService;

    @Test
    void testRenderHappyPath() throws IOException {
        var checkList = new CheckList(
                new Agent(
                        "Pets&Food",
                        "1234567890",
                        "Sell pets food"
                ),
                List.of(
                        new Person("polkins", "123"),
                        new Person("polly", "456")
                )
        );

        var parameters = new RenderParameters(
                "example",
                PrintFormat.PDF,
                objectMapper.writeValueAsString(checkList)
        );

        var file = new File("example.pdf");
        var os = new FileOutputStream(file);
        os.write(pdfRenderService.render(parameters.templateId(), parameters.dataModel()));
        os.close();
        Assertions.assertThat(file).exists().isFile();
    }

    private record CheckList(
            Agent agent,
            List<Person> payers
    ) {
    }

    private record Agent(
            String firmName,
            String code,
            String workPlan
    ) {
    }

    record Person(
            String name,
            String code
    ) {
    }
}
