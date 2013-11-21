package org.richfaces.ui.charts;

import java.net.URL;
import java.util.List;

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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.richfaces.deployment.FrameworkDeployment;
import org.richfaces.shrinkwrap.descriptor.FaceletAsset;

import category.Smoke;

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

@RunAsClient
@RunWith(Arquillian.class)
public class ITChartsInitTests {

    @Drone
    private WebDriver driver;

    @ArquillianResource
    private URL contextPath;

    @Deployment
    public static WebArchive createDeployment() {
        FrameworkDeployment deployment = new FrameworkDeployment(ITChartsInitTests.class);

        deployment.archive().addClasses(ChartsBean.class, Country.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        addIndexPage(deployment);
        return deployment.getFinalArchive();
    }

    private static void addIndexPage(FrameworkDeployment deployment) {
        FaceletAsset p = new FaceletAsset();

        // initialize a line chart
        p.form("<r:chart id='firstChart'>");
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

        // initialize a pie chart
        p.form("<r:chart id='pieChart'>");
        p.form("<r:series data='#{bean.pie}' type='pie'/>");
        p.form("</r:chart>");

        // initialize a bar chart
        p.form("<r:chart id='barChart' >");
        p.form("<r:legend sorting='ascending'/>");
        p.form("<r:repeat value='#{bean.gdp}' var='country'>");
        p.form("<r:series label='#{country.state}' type='bar'>");
        p.form("<r:point x='Agricultural' y='#{country.agricult}'/>");
        p.form("<r:point x='Industrial' y='#{country.industry}'/>");
        p.form("<r:point x='Service' y='#{country.service}'/>");
        p.form("</r:series>");
        p.form("</r:repeat>");
        p.form("</r:chart>");

        deployment.archive().addAsWebResource(p, "index.xhtml");

    }

    /**
     * The idea is to make a basic check whether there are div(s) present and canvases drawn below them. Also can check is axis
     * are present.
     */
    @Test
    @Category(Smoke.class)
    public void testAllRendered() {
        driver.get(contextPath.toExternalForm() + "index.jsf");

        testLineChartRendered();
        testPieChartRendered();
        testBarChartRendered();
    }

    private void testLineChartRendered() {
        // assert the chart is displayed, eg. the canvas is drawn
        WebElement chart = driver.findElement(By.id("firstChart"));
        assertTrue(chart != null);
        WebElement canvas = chart.findElement(By.tagName("canvas"));
        assertTrue(canvas != null);

        // assert there are 2 axis labels
        List<WebElement> axisLabels = driver.findElements(By
            .xpath("//div[@id = 'firstChart']//div[contains(@class, 'axisLabel')]"));
        assertTrue(axisLabels.size() == 2);
        assertEquals("year", axisLabels.get(0).getText());
        assertEquals("metric tons of CO2 per capita", axisLabels.get(1).getText());
    }

    private void testPieChartRendered() {
        // assert canvas is rendered
        WebElement chart = driver.findElement(By.id("pieChart"));
        assertTrue(chart != null);
        WebElement canvas = chart.findElement(By.tagName("canvas"));
        assertTrue(canvas != null);
    }

    private void testBarChartRendered() {
        // assert canvas is rendered
        WebElement chart = driver.findElement(By.id("barChart"));
        assertTrue(chart != null);
        WebElement canvas = chart.findElement(By.tagName("canvas"));
        assertTrue(canvas != null);

        // assert x axis contains all labels
        List<WebElement> xAxisLabels = driver.findElements(By
            .xpath("//div[@id = 'barChart']//div[contains(@class, 'flot-x-axis')]/div"));
        assertTrue(xAxisLabels.size() == 3);
        assertEquals("Service", xAxisLabels.get(0).getText());
        assertEquals("Agricultural", xAxisLabels.get(1).getText());
        assertEquals("Industrial", xAxisLabels.get(2).getText());

        // assert y axis contains all labels
        List<WebElement> yAxisLabels = driver.findElements(By
            .xpath("//div[@id = 'barChart']//div[contains(@class, 'flot-y-axis')]/div"));
        assertTrue(yAxisLabels.size() == 5);
        assertEquals("0", yAxisLabels.get(0).getText());
        assertEquals("5000000", yAxisLabels.get(1).getText());
        assertEquals("10000000", yAxisLabels.get(2).getText());
        assertEquals("15000000", yAxisLabels.get(3).getText());
        assertEquals("20000000", yAxisLabels.get(4).getText());
    }
}