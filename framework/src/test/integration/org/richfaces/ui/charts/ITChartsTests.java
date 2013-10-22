package org.richfaces.ui.charts;

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
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinates;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencyExclusion;
import org.jboss.shrinkwrap.resolver.impl.maven.coordinate.MavenDependencyImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
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
public class ITChartsTests {

    @Drone
    private WebDriver browser;

    @ArquillianResource
    private URL contextPath;

    @Deployment
    public static WebArchive createDeployment() {
        FrameworkDeployment deployment = new FrameworkDeployment(ITChartsTests.class);

        File[] deps = Maven.resolver().resolve("org.richfaces.sandbox.ui.charts:charts-ui:5.0.0-SNAPSHOT").withoutTransitivity().asFile();
        deployment.archive().addClasses(ChartsBean.class, Country.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsLibraries(deps);;

        //        File[] deps = Maven.resolver().addDependencies(MavenDependencies.createDependency(
//            MavenCoordinates.createCoordinate("org.richfaces.sandbox.ui.charts", "charts-ui", "5.0.0-SNAPSHOT", null, null),
//            ScopeType.TEST, false, new MavenDependencyExclusion[]{})).resolve().withTransitivity().asFile();  
        addIndexPage(deployment);
        return deployment.getFinalArchive();
    }

    private static void addIndexPage(FrameworkDeployment deployment) {
        FaceletAsset p = new FaceletAsset();
        
        // initialize a chart
        p.form("<s:chart>");
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
        deployment.archive().addAsWebResource(p, "index.xhtml");
    }

    @Test
    @Category(Smoke.class)
    public void testSomething() {
        browser.get(contextPath.toExternalForm() + "index.jsf");
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}