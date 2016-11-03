package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.DestroyUI;
import com.vaadin.cdi.uis.DestroyViewUI;
import com.vaadin.cdi.uis.ScopedInstrumentedView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UIDestroyTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(name = "uiDestroy")
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("uiDestroy",
                DestroyUI.class,
                ScopedInstrumentedView.class,
                DestroyViewUI.class);
    }

    @Test
    @OperateOnDeployment("uiDestroy")
    public void testViewChangeTriggersClosedUIDestroy() throws Exception {
        String uri = Conventions.deriveMappingForUI(DestroyUI.class);

        openWindow(uri);
        assertUiInstanceCounts("1");

        //close first UI
        clickAndWait(DestroyUI.CLOSE_BTN_ID);

        //open new UI
        openWindow(uri);
        assertUiInstanceCounts("2");

        Thread.sleep(5000); //AbstractVaadinContext.CLEANUP_DELAY

        //ViewChange event triggers a cleanup
        clickAndWait(DestroyUI.NAVIGATE_BTN_ID);

        //first UI cleaned up
        assertUiInstanceCounts("1");
    }

    @Test
    @OperateOnDeployment("uiDestroy")
    public void testViewDestroyOnUIDestroy() throws Exception {
        String viewUri = Conventions.deriveMappingForUI(DestroyViewUI.class)+ "#!home";

        openWindow(viewUri);
        clickAndWait(DestroyViewUI.CLOSE_BTN_ID);
        openWindow(viewUri);
        assertViewInstanceCounts("2");

        clickAndWait(DestroyViewUI.CLOSE_BTN_ID);
        Thread.sleep(5000); //AbstractVaadinContext.CLEANUP_DELAY

        //open new UI. Navigating to home view on load triggers cleanup.
        openWindow(viewUri);

        assertViewInstanceCounts("1");
    }

    private void assertViewInstanceCounts(String count) {
        clickAndWait(DestroyViewUI.QUERYCOUNT_BTN_ID);
        assertThat(findElement(DestroyViewUI.VIEWCOUNT_ID).getText(), is(count));
        assertThat(findElement(DestroyViewUI.VIEWBEANCOUNT_ID).getText(), is(count));
    }

    private void assertUiInstanceCounts(String count) {
        clickAndWait(DestroyUI.QUERYCOUNT_BTN_ID);
        assertThat(findElement(DestroyUI.UICOUNT_ID).getText(), is(count));
        assertThat(findElement(DestroyUI.UIBEANCOUNT_ID).getText(), is(count));
    }

}
