package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.DestroyUI;
import com.vaadin.cdi.views.TestView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UIDestroyTest extends AbstractManagedCDIIntegrationTest {

    private String uri;
    private String uiId;

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("uiDestroy",
                DestroyUI.class,
                TestView.class);
    }

    @Before
    public void setUp() throws IOException {
        resetCounts();
        uri = Conventions.deriveMappingForUI(DestroyUI.class);
        openWindow(uri);
        uiId = findElement(DestroyUI.UIID_ID).getText();
        assertDestroyCount(0);
    }

    @Test
    public void testViewChangeTriggersClosedUIDestroy() throws Exception {
        //close first UI
        clickAndWait(DestroyUI.CLOSE_BTN_ID);

        //open new UI
        openWindow(uri);
        assertDestroyCount(0);

        Thread.sleep(5000); //AbstractVaadinContext.CLEANUP_DELAY

        //ViewChange event triggers a cleanup
        clickAndWait(DestroyUI.NAVIGATE_BTN_ID);

        //first UI cleaned up
        assertDestroyCount(1);
    }

    @Test
    public void testSessionCloseDestroysUIContext() throws Exception {
        clickAndWait(DestroyUI.CLOSE_SESSION_BTN_ID);
        assertDestroyCount(1);
    }

    private void assertDestroyCount(int count) throws IOException {
        assertThat(getCount(DestroyUI.DESTROY_COUNT + uiId), is(count));
        assertThat(getCount(DestroyUI.UIScopedBean.DESTROY_COUNT + uiId), is(count));
    }

}
