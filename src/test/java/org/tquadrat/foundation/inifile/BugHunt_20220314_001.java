/*
 * ============================================================================
 *  Copyright © 2002-2022 by Thomas Thrien.
 *  All Rights Reserved.
 * ============================================================================
 *  Licensed to the public under the agreements of the GNU Lesser General Public
 *  License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *       http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package org.tquadrat.foundation.inifile;

import static java.lang.String.format;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.exists;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tquadrat.foundation.util.StringUtils.capitalize;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.inifile.internal.INIFileImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;
import org.tquadrat.foundation.util.stringconverter.InstantStringConverter;

/**
 *  The Blood Pressure Statistics application threw an OutOfMemoryError because
 *  the file comment for its ini files grew to a level of more than 400 MB.
 *
 *  @version $Id: BugHunt_20220314_001.java 1076 2023-10-03 18:36:07Z tquadrat $
 *  @author Thomas Thrien - thomas.thrien@tquadrat.org
 */
@ClassVersion( sourceVersion = "$Id: BugHunt_20220314_001.java 1076 2023-10-03 18:36:07Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.inifile.BugHunt_20220314_001" )
public class BugHunt_20220314_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Returns the name for the INI file.
     *
     *  @return The path for the INI file.
     */
    private final Path getPath()
    {
        final var retValue = Path.of( ".", "data", "scratch","test.ini" );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getPath()

    /**
     *  Ensure that there is no artifact from a previous test.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @BeforeEach
    final void beforeEachTest() throws Exception
    {
        deleteIfExists( getPath() );
    }       //  beforeEachTest()

    /**
     *  Create an ini file, save it, close it, reopen it and save again.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @Test
    final void testCreationSaveReopen() throws Exception
    {
        skipThreadTest();

        final var clock = Clock.fixed( Instant.now(), ZoneId.systemDefault() );

        final var file = getPath();

        var isNew = !exists( file );
        assertTrue( isNew );

        var candidate = INIFile.open( file );
        assertNotNull( candidate );
        ((INIFileImpl) candidate).setClock( clock );

        if( isNew )
        {
            candidate.addComment( "File comment" );
        }
        for( final var group : List.of( "group1", "group2", "group3" ) )
        {
            if( !candidate.hasGroup( group ) )
            {
                candidate.addComment( group, format( "%1$s comment", capitalize( group ) ) );
            }

            for( final var value : List.of( "value1", "value2", "value3" ) )
            {
                if( !candidate.hasValue( group, value ) )
                {
                    candidate.addComment( group, value, format( "%1$s/%2$s comment", capitalize( group ), capitalize( value ) ) );
                }
            }
        }

        var expected = ((INIFileImpl) candidate).dumpContents();
        candidate.save();
        candidate.refresh();
        var actual = ((INIFileImpl) candidate).dumpContents();
        candidate.save();

        assertEquals( expected, actual );

        //---* Reopen *--------------------------------------------------------
        isNew = !exists( file );
        assertFalse( isNew );

        candidate = INIFile.open( file );
        assertNotNull( candidate );
        ((INIFileImpl) candidate).setClock( clock );

        actual = ((INIFileImpl) candidate).dumpContents();
        assertEquals( expected, actual );

        if( isNew )
        {
            candidate.addComment( "File comment" );
        }
        for( final var group : List.of( "group1", "group2", "group3" ) )
        {
            if( !candidate.hasGroup( group ) )
            {
                candidate.addComment( group, format( "%1$s comment", capitalize( group ) ) );
            }

            for( final var value : List.of( "value1", "value2", "value3" ) )
            {
                if( !candidate.hasValue( group, value ) )
                {
                    candidate.addComment( group, value, format( "%1$s/%2$s comment", capitalize( group ), capitalize( value ) ) );
                }
                candidate.setValue( group, value, Instant.now(), InstantStringConverter.INSTANCE );
            }
        }

        expected = ((INIFileImpl) candidate).dumpContents();
        candidate.save();
        candidate.refresh();
        actual = ((INIFileImpl) candidate).dumpContents();
        assertEquals( expected, actual );
        candidate.save();
        actual = ((INIFileImpl) candidate).dumpContents();
        assertEquals( expected, actual );
    }   //  testCreationSaveReopen()
}
//  class BugHunt_20220314_001

/*
 *  End of File
 */