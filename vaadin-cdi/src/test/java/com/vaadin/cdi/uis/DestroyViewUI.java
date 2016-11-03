package com.vaadin.cdi.uis;

import com.vaadin.cdi.*;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
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

@CDIUI("viewDestroy")
public class DestroyViewUI extends UI {
    public static final String CLOSE_BTN_ID = "close";
    public static final String QUERYCOUNT_BTN_ID = "guerycount";
    public static final String LABEL_ID = "label";
    public static final String VIEWCOUNT_ID = "viewcount";
    public static final String VIEWBEANCOUNT_ID = "viewbeancount";

    @Inject
    CDIViewProvider viewProvider;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        Button closeBtn = new Button("close UI");
        closeBtn.setId(CLOSE_BTN_ID);
        closeBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        layout.addComponent(closeBtn);

        final Label viewcount = new Label();
        viewcount.setId(VIEWCOUNT_ID);
        layout.addComponent(viewcount);

        final Label viewbeancount = new Label();
        viewbeancount.setId(VIEWBEANCOUNT_ID);
        layout.addComponent(viewbeancount);

        Button queryCountBtn = new Button("query count");
        queryCountBtn.setId(QUERYCOUNT_BTN_ID);
        queryCountBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                viewcount.setValue(Integer.toString(HomeView.getNumberOfInstances()));
                viewbeancount.setValue(Integer.toString(ViewScopedBean.getNumberOfInstances()));
            }
        });
        layout.addComponent(queryCountBtn);

        final Navigator navigator = new Navigator(this, new ViewDisplay() {
            @Override
            public void showView(View view) {
            }
        });
        navigator.addProvider(viewProvider);

        setContent(layout);
    }

    @CDIView(value = "home")
    public static class HomeView extends VerticalLayout implements View {
        private final static AtomicInteger COUNTER = new AtomicInteger(0);

        @Inject
        ViewScopedBean viewScopedBean;

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

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

        }
    }

    @ViewScoped
    public static class ViewScopedBean implements Serializable {
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
