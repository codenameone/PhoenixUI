/*
 * Copyright (c) 2016, Codename One
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions 
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package com.codename1.uikit.pheonixui;

import com.codename1.progress.ArcProgress;
import com.codename1.progress.CircleProgress;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.List;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.list.ListCellRenderer;
import com.codename1.ui.list.ListModel;
import com.codename1.ui.plaf.Border;
import com.codename1.ui.plaf.RoundBorder;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.util.Resources;
import java.util.Calendar;
import java.util.Date;

/**
 * Simple form for the stats, hand coded
 *
 * @author Shai Almog
 */
public class StatsForm extends BaseForm {
    public StatsForm(Resources res) {
        setLayout(new BorderLayout());
        setUIID("StatsForm");
        installSidemenu(res);
        
        getToolbar().addCommandToRightBar("", res.getImage("toolbar-profile-pic.png"), e -> {});
        
        Button toggle = new Button("");
        toggle.setUIID("CenterWhite");
        FontImage.setMaterialIcon(toggle, FontImage.MATERIAL_ACCESS_TIME);
        toggle.getAllStyles().setMargin(0, 0, 0, 0);
        toggle.getAllStyles().setBorder(RoundBorder.create().
                rectangle(true).
                color(0x9b4c3f));
        Button placeholder = new Button("");
        placeholder.setUIID("CenterWhite");
        Container buttonGrid = GridLayout.encloseIn(2, toggle, placeholder);
        Label leftLabel = new Label("", "CenterWhite");
        FontImage.setMaterialIcon(leftLabel, FontImage.MATERIAL_ACCESS_TIME);
        Label rightLabel = new Label("", "CenterWhite");
        FontImage.setMaterialIcon(rightLabel, FontImage.MATERIAL_DIRECTIONS_RUN);
        Container labelGrid = GridLayout.encloseIn(2, leftLabel, rightLabel);
        labelGrid.getAllStyles().setBorder(RoundBorder.create().
                rectangle(true).
                color(0xd27d61));
        
        getToolbar().setTitleComponent(
                FlowLayout.encloseCenterMiddle(
                    LayeredLayout.encloseIn(labelGrid, buttonGrid)
                )
        );
        
        ActionListener al = e -> {
            if(buttonGrid.getComponentAt(0) == toggle) {
                toggle.remove();
                buttonGrid.add(toggle);
                buttonGrid.animateLayoutAndWait(150);
                FontImage.setMaterialIcon(toggle, FontImage.MATERIAL_DIRECTIONS_RUN);
            } else {
                placeholder.remove();
                buttonGrid.add(placeholder);
                buttonGrid.animateLayoutAndWait(150);
                FontImage.setMaterialIcon(toggle, FontImage.MATERIAL_ACCESS_TIME);
            }
        };
        toggle.addActionListener(al);
        placeholder.addActionListener(al);
        
        add(BorderLayout.NORTH, createTopGrid(res));
        add(BorderLayout.SOUTH, createBottomList(res));
        add(BorderLayout.CENTER, circleContent(res));
    }

    Container circleContent(Resources res) {
        Label today = new Label("Today", "LargeWhileLabel");
        Label time = new Label("2:56:", "HugeWhileLabel");
        Label seconds = new Label("01", "HugeDarkLabel");
        Label darkRect = new Label(res.getImage("dark-rect.png"), "StatsLabel");
        darkRect.setShowEvenIfBlank(true);
        Label active = new Label("ACTIVE", "StatsLabel");
        ArcProgress ap = new ArcProgress();
        ap.setProgress(70);
        ap.setRenderPercentageOnTop(false);
        Container box = BoxLayout.encloseY(
                        today,
                        FlowLayout.encloseCenter(time, seconds),
                        FlowLayout.encloseCenter(darkRect),
                        active
                    );
        box.getUnselectedStyle().setPaddingUnit(Style.UNIT_TYPE_DIPS);
        box.getUnselectedStyle().setPadding(4, 4, 4, 4);
        Container scroll = BoxLayout.encloseY(LayeredLayout.encloseIn(
                    ap,
                    box
                ));
        scroll.setScrollableY(true);
        return scroll;
        
    }
    
    
    Container gridElement(Resources res, String time, String label, boolean last) {
        Container c = BorderLayout.centerAbsolute(
                BoxLayout.encloseY(
                    FlowLayout.encloseCenter(new Label(time, "LargeWhileLabel"),
                            new Label("min", "SmallWhileLabel")
                    ),
                    new Label(res.getImage("welcome-separator.png"), "CenterNoPadd"),
                    new Label(label, "StatsLabel")
                )
        );
        if(last) {
            c.getAllStyles().setBorder(Border.createCompoundBorder(null, 
                    Border.createLineBorder(1, 0x5b636b), null, null));
        } else {
            c.getAllStyles().setBorder(Border.createCompoundBorder(null, 
                    Border.createLineBorder(1, 0x5b636b), null, 
                    Border.createLineBorder(1, 0x5b636b)));
        }
        return c;
    }
    
    Container createTopGrid(Resources res) {
        return GridLayout.encloseIn(3, 
                gridElement(res, "1:37", "Walking", false),
                gridElement(res, "0:47", "Running", false),
                gridElement(res, "2:56", "Cycling", true));
    }
    
    Component createBottomList(Resources res) {
        final int DAY = 24 * 60 * 60000;
        List<Date> dayPicker = new List(new ListModel<Date>() {
            int selection = (int)(System.currentTimeMillis() / DAY);
            @Override
            public Date getItemAt(int index) {
                return new Date(index * DAY);
            }

            @Override
            public int getSize() {
                return 50000;
            }

            @Override
            public int getSelectedIndex() {
                return selection;
            }

            @Override
            public void setSelectedIndex(int index) {
                selection = index;
            }

            @Override
            public void addDataChangedListener(DataChangedListener l) {
            }

            @Override
            public void removeDataChangedListener(DataChangedListener l) {
            }

            @Override
            public void addSelectionListener(SelectionListener l) {
            }

            @Override
            public void removeSelectionListener(SelectionListener l) {
            }

            @Override
            public void addItem(Date item) {
            }

            @Override
            public void removeItem(int index) {
            }            
        });
        final String[] WEEKDAYS = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        dayPicker.setOrientation(List.HORIZONTAL);
        dayPicker.setFixedSelection(List.FIXED_CENTER);
        dayPicker.setRenderingPrototype(new Date());
        dayPicker.setRenderer(new ListCellRenderer() {
            Label focus = new Label();
            Label day;
            Label label;
            Container cnt;
            {
                day = new Label("00", "LargeWhileLabel");
                label = new Label("WED", "StatsLabel");
                cnt = 
                        BoxLayout.encloseY(
                                FlowLayout.encloseCenter(day), 
                                FlowLayout.encloseCenter(label)
                        );
                cnt.setCellRenderer(true);
            }
            
            @Override
            public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
                Date d = (Date)value;
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                int dd = c.get(Calendar.DAY_OF_MONTH);
                if(dd < 10) {
                    day.setText("0" + dd);
                } else {
                    day.setText("" + dd);
                }
                label.setText(WEEKDAYS[c.get(Calendar.DAY_OF_WEEK) - 1]);
                return cnt;
            }

            @Override
            public Component getListFocusComponent(List list) {
                return focus;
            }
        });
        return BoxLayout.encloseY(
                new Label(res.getImage("welcome-separator.png"), "CenterNoPadd"),
                dayPicker);
    }
    
    @Override
    protected boolean isCurrentStats() {
        return true;
    }
}
