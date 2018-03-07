package city.gotham.security.services;

import city.gotham.security.ApplicationProperties;
import city.gotham.security.models.MailData;
import city.gotham.security.models.MailProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocalMailServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalMailServiceTest.class);
    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    @Before
    public void setUp() throws Exception { }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void _0001_sendMail() {
        LOGGER.info("Running LocalMailServiceTest._0001_sendMail() ---> ");

        MailProperties mailProperties = new MailProperties(
                APPLICATION_PROPERTIES.getSmtpAuth(),
                APPLICATION_PROPERTIES.getSmtpStartTlsEnable(),
                APPLICATION_PROPERTIES.getSmtpHost(),
                APPLICATION_PROPERTIES.getSmtpPort(),
                APPLICATION_PROPERTIES.getSmtpUsername(),
                APPLICATION_PROPERTIES.getSmtpPassword()
        );

        MailData mailData = new MailData(
                APPLICATION_PROPERTIES.getSmtpUsername(),
                APPLICATION_PROPERTIES.getTargetEmail()
        );

        assertEquals(true, LocalMailService.sendMail(
                "<span>Test!</span>",
                mailProperties,
                mailData
        ));

        LOGGER.info("Passed");
    }

}
