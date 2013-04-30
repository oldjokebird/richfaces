/*
 * JBoss, Home of Professional Open Source
 * Copyright ${year}, Red Hat, Inc. and individual contributors
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
package org.richfaces.ui.output;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.richfaces.ui.toggle.togglePanel.UITogglePanelItem;

/**
 * @author akolonitsky
 * @since 2010-08-24
 */
public class UITogglePanelItemTest {
    private UITogglePanelItem togglePanelItem;

    @Before
    public void setUp() {
        togglePanelItem = new UITogglePanelItem();
    }

    @Test
    public void testGetName() {
//        Assert.assertNull(togglePanelItem.getName());

        String id = "id";
        togglePanelItem.setId(id);
        Assert.assertEquals(togglePanelItem.getId(), id);
/*  Assertion commented out, as getName now requires an active FacesContext
        Assert.assertEquals(togglePanelItem.getName(), id);
*/
        String name = "name";
        togglePanelItem.setName(name);
/*  Assertion commented out, as getName now requires an active FacesContext
        Assert.assertEquals(togglePanelItem.getName(), name);
 */
    }
}
