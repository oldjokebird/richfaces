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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.richfaces.deployment.FrameworkDeployment;
import org.richfaces.shrinkwrap.descriptor.FaceletAsset;

@RunAsClient
@RunWith(Arquillian.class)
public class ITChartJSApi {

    private final int offSetX = 35;
    private final int offsetY = 62;

    @Drone
    private WebDriver driver;

    @ArquillianResource
    private URL contextPath;

    @FindBy(id = "firstChart")
    private WebElement chart;

    @FindBy(id = "plotclick")
    private WebElement plotClickOutput;

    @Deployment
    public static WebArchive createDeployment() {
        FrameworkDeployment deployment = new FrameworkDeployment(ITChartJSApi.class);

        File[] deps = Maven.resolver().resolve("org.richfaces.sandbox.ui.charts:charts-ui:5.0.0-SNAPSHOT")
            .withoutTransitivity().asFile();
        deployment.archive().addClasses(ChartsBean.class, Country.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        deployment.archive().addAsLibraries(deps);
        addIndexPage(deployment);
        return deployment.getFinalArchive();
    }

    private static void addIndexPage(FrameworkDeployment deployment) {
        FaceletAsset p = new FaceletAsset();

        // initialize a chart
        p.form("<s:chart id='firstChart' onplotclick=\"$('#plotclick').text('onplotclickevent')\" zoom=\"true\">");
        p.form("<r:repeat value='#{bean.countries}' var='country' >");
        p.form("<s:series label='#{country.name}' type='line'>");
        p.form("<r:repeat value='#{country.data}' var='record'>");
        p.form("<s:point x='#{record.year}' y='#{record.tons}' />");
        p.form("</r:repeat>");
        p.form("</s:series>");
        p.form("</r:repeat>");
        p.form("<s:xaxis label='year'/>");
        p.form("<s:yaxis label='metric tons of CO2 per capita'/>");
        p.form("</s:chart>");

        // initialize output text fields
        p.body("<h:outputText id='plotclick' style='font-weight:bold' value='someValue'></h:outputText>");
        p.body("<br/>");

        deployment.archive().addAsWebResource(p, "jsapi.xhtml");
    }

    @Test
    public void testResetZoom() {
        driver.get(contextPath.toExternalForm() + "jsapi.jsf");

        // assert plot can be clicked on given coordinates which work when NOT zoomed
        new Actions(driver).moveToElement(chart, offSetX, offsetY).click().build().perform();
        assertEquals("onplotclickevent", plotClickOutput.getText());
        //reset text field
        ((JavascriptExecutor)driver).executeScript("$('#plotclick').text('someText')");
        
        // assert no plot is on the same coords when zoomed
        new Actions(driver).moveToElement(chart).clickAndHold().moveByOffset(200, 200).release().build().perform();
        new Actions(driver).moveToElement(chart, offSetX, offsetY).click().build().perform();
        assertNotEquals("onplotclickevent", plotClickOutput.getText());
        
        // reset zoom and assert plot can be clicked again on the same coords
        ((JavascriptExecutor)driver).executeScript("$('#firstChartChart').chart('resetZoom')");
        new Actions(driver).moveToElement(chart, offSetX, offsetY).click().build().perform();
        assertEquals("onplotclickevent", plotClickOutput.getText());
    }
    
    public void testHighlight() {
        //TODO feature not yet implemented
    }
}
