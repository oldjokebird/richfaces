package org.richfaces.ui.charts;
/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.richfaces.deployment.FrameworkDeployment;
import org.richfaces.shrinkwrap.descriptor.FaceletAsset;

import category.Smoke;

/**
 * A class testing event handlers for charts. Mouseout, plotclick and plothover.
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class ITChartEvents {
    private final int offSetX = 35;
    private final int offsetY = 62;

    @Drone
    private WebDriver driver;

    @ArquillianResource
    private URL contextPath;

    @FindBy(id = "plotclick")
    private WebElement plotClickOutput;

    @FindBy(id = "hover")
    private WebElement plotHoverOutput;

    @FindBy(id = "mouseout")
    private WebElement mouseoutOutput;

    @FindBy(id = "firstChart")
    private WebElement chart;

    @Deployment
    public static WebArchive createDeployment() {
        FrameworkDeployment deployment = new FrameworkDeployment(ITChartEvents.class);

        
        deployment.archive().addClasses(ChartsBean.class, Country.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        addIndexPage(deployment);
        return deployment.getFinalArchive();
    }

    private static void addIndexPage(FrameworkDeployment deployment) {
        FaceletAsset p = new FaceletAsset();

        // initialize a chart

        p.form("<r:chart id='firstChart' onplotclick=\"$('#plotclick').text('onplotclickevent')\" "
            + "onplothover=\"$('#hover').text('onplothoverevent')\" "
            + "onmouseout=\"$('#mouseout').text('onmouseoutevent')\">");
        p.form("<r:repeat value='#{bean.countries}' var='country' >");
        p.form("<r:series label='#{country.name}' type='line'>");
        p.form("<r:repeat value='#{country.data}' var='record'>");
        p.form("<r:point x='#{record.year}' y='#{record.tons}' />");
        p.form("</r:repeat>");
        p.form("</r:series>");
        p.form("</r:repeat>");
        p.form("<r:xaxis label='year'/>");
        p.form("<r:yaxis label='metric tons of CO2 per capita'/>");
        p.form("</r:chart>");

        // initiate output text fields
        p.body("<h:outputText id='plotclick' style='font-weight:bold' value='someValue'></h:outputText>");
        p.body("<br/>");
        p.body("<h:outputText id='hover' style='font-weight:bold' value='hoverValue'></h:outputText>");
        p.body("<br/>");
        p.body("<h:outputText id='mouseout' style='font-weight:bold' value='mouseValue'></h:outputText>");

        deployment.archive().addAsWebResource(p, "events.xhtml");
    }

    @Test
    @Category(Smoke.class)
    public void testOnplotclick() {
        driver.get(contextPath.toExternalForm() + "events.jsf");

        assertTrue(plotClickOutput.getText() != "onplotclickevent");
        // trigger onplotclick event
        new Actions(driver).moveToElement(chart, offSetX, offsetY).click().build().perform();
        assertEquals("onplotclickevent", plotClickOutput.getText());
    }

    @Test
    @Category(Smoke.class)
    public void testOnplothover() {
        driver.get(contextPath.toExternalForm() + "events.jsf");

        assertTrue(plotHoverOutput.getText() != "onplothoverevent");
        // trigger event
        new Actions(driver).moveToElement(chart, offSetX, offsetY).build().perform();
        assertEquals("onplothoverevent", plotHoverOutput.getText());
    }

    @Test
    @Category(Smoke.class)
    public void testOnmouseout() {
        driver.get(contextPath.toExternalForm() + "events.jsf");

        assertTrue(mouseoutOutput.getText() != "onmouseoutevent");
        //trigger event
        new Actions(driver).moveToElement(chart).moveToElement(plotClickOutput).build().perform();
        assertEquals("onmouseoutevent", mouseoutOutput.getText());
    }
}
