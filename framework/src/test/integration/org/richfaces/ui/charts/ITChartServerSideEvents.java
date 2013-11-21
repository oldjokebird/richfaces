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
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.richfaces.deployment.FrameworkDeployment;
import org.richfaces.shrinkwrap.descriptor.FaceletAsset;

@RunAsClient
@RunWith(Arquillian.class)
public class ITChartServerSideEvents {

    private final int offSetX = 35;
    private final int offsetY = 62;
    
    @Drone
    private WebDriver driver;

    @ArquillianResource
    private URL contextPath;

    @FindBy(tagName = "span")
    private WebElement plotClickOutput;

    @FindBy(id = "firstChart")
    private WebElement chart;
    
    @Deployment
    public static WebArchive createDeployment() {
        FrameworkDeployment deployment = new FrameworkDeployment(ITChartServerSideEvents.class);

        deployment.archive().addClasses(ChartEventBean.class, Country.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        addIndexPage(deployment);
        return deployment.getFinalArchive();
    }
    
    private static void addIndexPage(FrameworkDeployment deployment) {
        FaceletAsset p = new FaceletAsset();

        // initiate output text fields, has to be inside form
        p.form("<h:outputText id='msg' value='#{eventbean.msg}'></h:outputText>");
        p.form("<br/>");

        // initialize a chart
        p.form("<r:chart id='firstChart' clickListener='#{eventbean.handler}'>");
        p.form("<r:repeat value='#{eventbean.countries}' var='country' >");
        p.form("<r:series label='#{country.name}' type='line'>");
        p.form("<r:repeat value='#{country.data}' var='record'>");
        p.form("<r:point x='#{record.year}' y='#{record.tons}' />");
        p.form("</r:repeat>");
        p.form("</r:series>");
        p.form("</r:repeat>");
        p.form("<r:ajax event='plotclick' render='msg' execute='msg'/>");
        p.form("<r:xaxis label='year'/>");
        p.form("<r:yaxis label='metric tons of CO2 per capita'/>");
        p.form("</r:chart>");

        deployment.archive().addAsWebResource(p, "serversideevents.xhtml");
    }
    
    /**
     * Testing a server side event after plotclick.
     */
    @Test
    public void testServerSidePlotclick() {
        driver.get(contextPath.toExternalForm() + "serversideevents.jsf");

        assertTrue(plotClickOutput.getText() != "Server's speaking");
        // trigger onplotclick event
        new Actions(driver).moveToElement(chart, offSetX, offsetY).click().build().perform();
        
        // wait till server response        
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.textToBePresentInElement(By.tagName("span"), "Server's speaking"));
        assertEquals("Server's speaking", plotClickOutput.getText());
    }
}
