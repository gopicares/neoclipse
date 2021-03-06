/**
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.neo4j.neoclipse.connection;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.neoclipse.util.ApplicationUtil;

/**
 * Our Neo4JConnection, which adds the connection to our GraphDatabaseService
 * object
 * 
 * @author Radhakrishna Kalyan
 * 
 */
public class Alias
{

    /*package*/static final String ALIASES = "aliases";
    /*package*/static final String ALIAS = "alias";
    /*package*/static final String NAME = "name";
    /*package*/static final String URI = "uri";
    /*package*/static final String USER_NAME = "user-name";
    /*package*/static final String PASSWORD = "password";
    /*package*/static final String CONFIGURATIONS = "configurations";
    /*package*/static final String CONFIG = "config";
    /*package*/static final String CONFIG_NAME = "name";
    /*package*/static final String CONFIG_VALUE = "value";

    private final String name;
    private String uri;
    private String userName;
    private String password;
    private long createdTime;
    private ConnectionMode connectionMode;
    private final Map<String, String> configurationMap = new HashMap<String, String>();

    public Alias( String aliasName, String dbPath, String user, String pass )

    {

        name = aliasName;
        uri = dbPath;
        connectionMode = ConnectionMode.getValue( dbPath );

        if ( connectionMode == ConnectionMode.LOCAL )
        {
            File dir = new File( uri );
            if ( !dir.exists() )
            {
                if ( !dir.mkdirs() )
                {
                    throw new IllegalArgumentException( "Could not create directory: " + dir );
                }
                uri = dir.getAbsolutePath();
            }
            if ( !dir.isDirectory() )
            {
                throw new IllegalArgumentException( "The database location is not a directory." );
            }
            if ( !dir.canWrite() )
            {
                throw new IllegalAccessError( "Permission Denied for write to the database location." );
            }
        }

        if ( !ApplicationUtil.isBlank( user ) )
        {
            userName = user;
        }
        if ( !ApplicationUtil.isBlank( user ) )
        {
            password = pass;
        }

        createdTime = System.currentTimeMillis();
    }

    /**
     * Constructs an Alias from XML, previously obtained from describeAsXml()
     * 
     * @param root
     */
    public Alias( Map root )
    {
        name = (String) root.get( NAME );
        uri = (String) root.get( URI );
        connectionMode = ConnectionMode.getValue( uri );
        String user = (String) root.get( USER_NAME );
        String pass = (String) root.get( PASSWORD );
        if ( !ApplicationUtil.isBlank( user ) )
        {
            userName = user;
        }
        if ( !ApplicationUtil.isBlank( pass ) )
        {
            password = pass;
        }

        Map configurationsElement = (Map) root.get( CONFIGURATIONS );
        if ( configurationsElement != null )
        {
            Set elements = configurationsElement.keySet();
            for ( Object key : elements )
            {
                addConfiguration( (String)key, (String)configurationsElement.get(key) );
            }
        }
    }

    public long getCreatedTime()
    {
        return createdTime;
    }

    public String getName()
    {
        return name;
    }

    public String getUri()
    {
        return uri;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }

    public ConnectionMode getConnectionMode()
    {
        return connectionMode;
    }

    /**
     * Describes this alias in XML; the result can be passed to the
     * Alias(Element) constructor to refabricate it
     * 
     * @return
     */
    public Map describeAsXml()
    {
        Map root = new HashMap();
        root.put( NAME,  ApplicationUtil.returnEmptyIfBlank( name ) );
        root.put( URI,  ApplicationUtil.returnEmptyIfBlank( uri ) );
        root.put( USER_NAME , ApplicationUtil.returnEmptyIfBlank( userName ) );
        root.put( PASSWORD, ApplicationUtil.returnEmptyIfBlank( password ) );

        if ( !configurationMap.isEmpty() )
        {
            Map configElement = new HashMap();
            Set<Entry<String, String>> entrySet = configurationMap.entrySet();
            for ( Entry<String, String> entry : entrySet )
            {
            	configElement.put(ApplicationUtil.returnEmptyIfBlank( entry.getKey() ), ApplicationUtil.returnEmptyIfBlank( entry.getValue() ));
            }
            root.put( CONFIGURATIONS, configElement );
            
        }
        return root;
    }

    public Map<String, String> getConfigurationMap()
    {
        return configurationMap;
    }

    public void addConfiguration( String key, String value )
    {
        configurationMap.put( key, value );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( uri == null ) ? 0 : uri.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        Alias other = (Alias) obj;
        if ( name == null )
        {
            if ( other.name != null )
            {
                return false;
            }
        }
        else if ( !name.equals( other.name ) )
        {
            return false;
        }
        if ( uri == null )
        {
            if ( other.uri != null )
            {
                return false;
            }
        }
        else if ( !uri.equals( other.uri ) )
        {
            return false;
        }
        return true;
    }

    public String getConfigurationByKey( String key )
    {
        return configurationMap.get( key );
    }
}
