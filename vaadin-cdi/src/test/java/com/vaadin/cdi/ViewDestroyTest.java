package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.DestroyViewUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ViewDestroyTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "viewDestroy")
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewDestroy", DestroyViewUI.class);
    }

    @Test
    @OperateOnDeployment("viewDestroy")
    public void testViewDestroyOnUIDestroy() throws Exception {
        String viewUri = Conventions.deriveMappingForUI(DestroyViewUI.class)+ "#!home";

        openWindow(viewUri);
        assertViewInstanceCounts("1");

        clickAndWait(DestroyViewUI.CLOSE_BTN_ID);
        openWindow(viewUri);
        assertViewInstanceCounts("2");

        clickAndWait(DestroyViewUI.CLOSE_BTN_ID);
        Thread.sleep(5000); //AbstractVaadinContext.CLEANUP_DELAY

        //open new UI. Navigating to home view on load triggers cleanup.
        openWindow(viewUri);

        assertViewInstanceCounts("1");
    }

    @Test
    @OperateOnDeployment("viewDestroy")
    public void testViewChangeDestroysViewScope() throws Exception {
        String viewUri = Conventions.deriveMappingForUI(DestroyViewUI.class)+ "#!home";

        openWindow(viewUri);
        assertViewInstanceCounts("1");

        //ViewChange event triggers a cleanup
        clickAndWait(DestroyViewUI.NAVIGATE_BTN_ID);

        assertViewInstanceCounts("0");
    }

    private void assertViewInstanceCounts(String count) {
        clickAndWait(DestroyViewUI.QUERYCOUNT_BTN_ID);
        assertThat(findElement(DestroyViewUI.VIEWCOUNT_ID).getText(), is(count));
        assertThat(findElement(DestroyViewUI.VIEWBEANCOUNT_ID).getText(), is(count));
    }

}
