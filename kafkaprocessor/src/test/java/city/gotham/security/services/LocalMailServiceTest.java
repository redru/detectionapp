package city.gotham.security.services;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LocalMailServiceTest {

    private LocalMailService localMailService;

    @Before
    public void setUp() throws Exception {
        localMailService = LocalMailService.getInstance();
    }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void _0001_getInstance() {
        System.out.print("Running LocalMailServiceTest._0001_getInstance() ---> ");
        assertNotNull(localMailService);
        System.out.println("Passed");
    }

    @Test
    public void _0002_sendMail() {
        System.out.print("Running LocalMailServiceTest._0002_sendMail() ---> ");
        assertEquals(true, LocalMailService.getInstance().sendMail("<html><title>Test!</title></html>"));
        System.out.println("Passed");
    }

}
