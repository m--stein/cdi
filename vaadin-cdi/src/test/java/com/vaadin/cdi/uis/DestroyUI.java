package com.vaadin.cdi.uis;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.UIScoped;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@CDIUI("")
public class DestroyUI extends UI {
    private final static AtomicInteger COUNTER = new AtomicInteger(0);

    public static final String CLOSE_BTN_ID = "close";
    public static final String QUERYCOUNT_BTN_ID = "guerycount";
    public static final String NAVIGATE_BTN_ID = "navigate";
    public static final String LABEL_ID = "label";
    public static final String UICOUNT_ID = "uicount";
    public static final String UIBEANCOUNT_ID = "uibeancount";

    @Inject
    CDIViewProvider viewProvider;

    @Inject
    UIScopedBean bean;

    @PostConstruct
    public void initialize() {
        COUNTER.incrementAndGet();
    }

    @PreDestroy
    public void destroy() {
        COUNTER.decrementAndGet();
    }

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        final Label uicount = new Label();
        uicount.setId(UICOUNT_ID);
        layout.addComponent(uicount);

        final Label uibeancount = new Label();
        uibeancount.setId(UIBEANCOUNT_ID);
        layout.addComponent(uibeancount);

        Button closeBtn = new Button("close UI");
        closeBtn.setId(CLOSE_BTN_ID);
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        layout.addComponent(closeBtn);

        Button queryCountBtn = new Button("query count");
        queryCountBtn.setId(QUERYCOUNT_BTN_ID);
        queryCountBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                updateCounts(uicount, uibeancount);
            }
        });
        layout.addComponent(queryCountBtn);

        Button viewNavigateBtn = new Button("navigate");
        viewNavigateBtn.setId(NAVIGATE_BTN_ID);
        viewNavigateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final Navigator navigator = new Navigator(DestroyUI.this, new ViewDisplay() {
                    @Override
                    public void showView(View view) {
                    }
                });
                navigator.addProvider(viewProvider);
                navigator.navigateTo("scopedInstrumentedView");
            }
        });
        layout.addComponent(viewNavigateBtn);

        setContent(layout);

    }

    private void updateCounts(Label uicount, Label uibeancount) {
        uicount.setValue(Integer.toString(getNumberOfInstances()));
        uibeancount.setValue(Integer.toString(UIScopedBean.getNumberOfInstances()));
    }

    public static int getNumberOfInstances() {
        return COUNTER.get();
    }

    @UIScoped
    public static class UIScopedBean implements Serializable {
        private final static AtomicInteger COUNTER = new AtomicInteger(0);

        @PostConstruct
        public void initialize() {
            COUNTER.incrementAndGet();
        }

        @PreDestroy
        public void destroy() {
            COUNTER.decrementAndGet();
        }

        public static int getNumberOfInstances() {
            return COUNTER.get();
        }

    }

}
