package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.DestroyUI;
import com.vaadin.cdi.uis.ScopedInstrumentedView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UIDestroyTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "uiDestroy")
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("uiDestroy",
                DestroyUI.class,
                ScopedInstrumentedView.class);
    }

    @Test
    @OperateOnDeployment("uiDestroy")
    public void testViewChangeTriggersCleanup() throws Exception {
        String uri = Conventions.deriveMappingForUI(DestroyUI.class);

        openWindow(uri);
        assertInstanceCounts("1");

        //close first UI
        clickAndWait(DestroyUI.CLOSE_BTN_ID);

        //open new UI
        openWindow(uri);
        assertInstanceCounts("2");

        Thread.sleep(5000); //AbstractVaadinContext.CLEANUP_DELAY

        //ViewChange event triggers a cleanup
        clickAndWait(DestroyUI.NAVIGATE_BTN_ID);

        //first UI cleaned up
        assertInstanceCounts("1");
    }

    private void assertInstanceCounts(String count) {
        clickAndWait(DestroyUI.QUERYCOUNT_BTN_ID);

        String uicount = findElement(DestroyUI.UICOUNT_ID).getText();
        String uibeancount = findElement(DestroyUI.UIBEANCOUNT_ID).getText();
        assertThat(uicount, is(count));
        assertThat(uibeancount, is(count));
    }

}
