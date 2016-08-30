/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.project.ui.upgrade.animated;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.ui.util.UIUtil;

/**
 * @author Adny
 * @author Simon Jiang
 * @author Joye Luo
 */
public class WelcomePage extends Page
{

    PageAction[] actions = { new PageFinishAction(), new PageSkipAction() };

    public WelcomePage( Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel );
        GridLayout layout = new GridLayout( 1, false );
        this.setLayout( layout );

        Label title = new Label( this, SWT.LEFT );
        title.setText( "Welcome to Liferay Code Upgrade Tool" );
        title.setFont( new Font( null, "Times New Roman", 16, SWT.NORMAL ) );

        Link link = new Link( this, SWT.MULTI );
        final String WELCOME =
            "This tool will help you to convert Liferay 6.2 projects into Liferay 7.0 projects.\n\n" +
                "The key functions are described below:\n" +
                "       1.Convert Liferay Plugins SDK 6.2 to Liferay Plugins SDK 7.0 or to Liferay Workspace\n" +
                "       2.Find  breaking changes in all projects" + " Update Descriptor files from 6.2 to 7.0\n" +
                "       3.Update Descriptor files from 6.2 to 7.0\n" +
                "       4.Update Layout Template files from 6.2 to 7.0\n" +
                "       5.Convert projects with custom jsp hook to modules or fragments\n\n" + "Note:\n" +
                "       This tool will help you to backup your sdk.\n" +
                "       It is still highly recommended that you make back-up copies of your important files.\n" +
                "       Theme and ext projects are not supported to upgrade in this tool currently.\n" +
                "       For more details, please see <a>From Liferay 6 to Liferay 7</a>.\n\n";
        link.setText( WELCOME );
        link.addListener( SWT.Selection, new Listener()
        {

            @Override
            public void handleEvent( Event event )
            {
                try
                {
                    Runtime.getRuntime().exec(
                        "rundll32 url.dll,FileProtocolHandler https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/from-liferay-6-to-liferay-7" );
                }
                catch( IOException e )
                {
                }

            }
        } );

        Button reRunButton = new Button( this, SWT.CENTER );
        reRunButton.setText( "Rerun" );
        reRunButton.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
                    UIUtil.getActiveShell(), "re-run code upgradle tool?",
                    "The configuration files will be deleted. Do you want to re-run the code upgradle tool?" );

                if( openNewLiferayProjectWizard )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                    IEditorPart editor = page.getActiveEditor();

                    page.closeEditor( editor, false );

                    final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

                    File stateDir = stateLocation.toFile();

                    final File codeUpgradeFile = new File( stateDir, "liferay-code-upgrade.xml" );

                    try
                    {
                        if( codeUpgradeFile.exists() )
                        {
                            codeUpgradeFile.delete();
                        }

                        codeUpgradeFile.createNewFile();
                    }
                    catch( Exception e1 )
                    {
                    }
                }

            }
        } );

        setActions( actions );
    }
}