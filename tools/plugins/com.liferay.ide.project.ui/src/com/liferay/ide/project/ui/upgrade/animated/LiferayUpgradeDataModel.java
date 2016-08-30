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


import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Simon Jiang
 */

public interface LiferayUpgradeDataModel extends Element
{
    ElementType TYPE = new ElementType( LiferayUpgradeDataModel.class );
    
    
    @Type( base = Path.class )
    @Service( impl = SdkLocationValidationService.class )
    ValueProperty PROP_SDK_LOCATION = new ValueProperty( TYPE, "SdkLocation" );
    
    Value<Path> getSdkLocation();
    void setSdkLocation( String sdkLocation );
    void setSdkLocation( Path sdkLocation );
    
    @Service( impl = ProjectNameValidationService.class )
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );
    Value<String> getProjectName();
    void setProjectName( String ProjectName );
    
    ValueProperty PROP_LAYOUT = new ValueProperty( TYPE, "Layout" );
    Value<String> getLayout();
    void setLayout( String Layout );

    ValueProperty PROP_LIFERAY_SERVER_NAME = new ValueProperty( TYPE, "LiferayServerName" );
    Value<String> getLiferayServerName();
    void setLiferayServerName( String value );

    @Type( base = Path.class )
    @AbsolutePath
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    ValueProperty PROP_NewLOCATION = new ValueProperty( TYPE, "NewLocation" );  
    Value<Path> getNewLocation();
    void setNewLocation( String newLocation );
    void setNewLocation( Path newLocation );
    
    
    @XmlBinding( path = "HasHook" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_HOOK = new ValueProperty( TYPE, "HasHook" );

    Value<Boolean> getHasHook();
    void setHasHook( String hasHook );
    void setHasHook( Boolean hasHook );

    @XmlBinding( path = "HasPortlet" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_PORTLET = new ValueProperty( TYPE, "HasPortlet" );

    Value<Boolean> getHasPortlet();
    void setHasPortlet( String hasPortlet );
    void setHasPortlet( Boolean hasPortlet );

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_THEME = new ValueProperty( TYPE, "HasTheme" );

    Value<Boolean> getHasTheme();
    void setHasTheme( String hasTheme );
    void setHasTheme( Boolean hasTheme );

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_EXT = new ValueProperty( TYPE, "HasExt" );

    Value<Boolean> getHasExt();
    void setHasExt( String hasExt );
    void setHasExt( Boolean hasExt );

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_SERVICE_BUILDER = new ValueProperty( TYPE, "HasServiceBuilder" );

    Value<Boolean> getHasServiceBuilder();
    void setHasServiceBuilder( String hasServiceBuilder );
    void setHasServiceBuilder( Boolean hasServiceBuilder );

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_LAYOUT = new ValueProperty( TYPE, "HasLayout" );

    Value<Boolean> getHasLayout();
    void setHasLayout( String hasLayout );
    void setHasLayout( Boolean hasLayout );

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_WEB = new ValueProperty( TYPE, "HasWeb" );

    Value<Boolean> getHasWeb();
    void setHasWeb( String hasWeb );
    void setHasWeb( Boolean hasWeb );
}