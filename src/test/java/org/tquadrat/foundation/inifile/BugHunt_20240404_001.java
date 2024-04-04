/*
 * ============================================================================
 *  Copyright © 2002-2024 by Thomas Thrien.
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

import static java.lang.System.out;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.lines;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  When a String contains a line break, the inifile gets corrupted.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@DisplayName( "org.tquadrat.foundation.inifile.BugHunt_20240404_001" )
public class BugHunt_20240404_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  When a String contains a line break, the inifile gets corrupted. Here
     *  validate that corruption.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testBrokenFile() throws Exception
    {
        skipThreadTest();

        final var path = Path.of( ".", "data", "scratch", "broken.ini" );
        assertTrue( exists( path ) );
        final var e = assertThrows( IOException.class, () -> INIFile.open( path ) );
        assertTrue( e.getMessage().contains( "has invalid structure" ) );
    }   //  testBrokenFile()

    /**
     *  When a String contains a line break, the inifile gets corrupted.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testLineBreak() throws Exception
    {
        skipThreadTest();

        final var group = "group";
        final var key1 = "key1";
        final var key2 = "key2";
        final var value1 = """
            This is a very long value that even has some line break in it.
            That's the reason for testing.
            Let's see what happens.
            """;
        final var value2 = """
            This is a very very long value that even has some line break in it.
            That's the reason for testing.
            Let's see what happens.
            
            
            """;

        final var path = Path.of( ".", "data", "scratch" );
        assertTrue( exists( path ) );
        final var file = createTempFile( path, "Test", ".ini" );
        assertTrue( exists( file ) );
        try
        {
            var candidate = INIFile.create( file );
            assertNotNull( candidate );
            candidate.setValue( group, key1, value1 );
            assertTrue( candidate.getValue( group, key1 ).isPresent() );
            assertEquals( value1, candidate.getValue( group, key1 ).get() );
            candidate.save();
            assertTrue( candidate.getValue( group, key1 ).isPresent() );
            assertEquals( value1, candidate.getValue( group, key1 ).get() );

            candidate.setValue( group, key2, value2 );
            assertTrue( candidate.getValue( group, key2 ).isPresent() );
            assertEquals( value2, candidate.getValue( group, key2 ).get() );
            candidate.save();
            assertTrue( candidate.getValue( group, key2 ).isPresent() );
            assertEquals( value2, candidate.getValue( group, key2 ).get() );

            lines( file ).forEach( out::println );

            candidate = INIFile.open( file );
            assertTrue( candidate.getValue( group, key1 ).isPresent() );
            assertEquals( value1, candidate.getValue( group, key1 ).get() );
            assertTrue( candidate.getValue( group, key2 ).isPresent() );
            assertEquals( value2, candidate.getValue( group, key2 ).get() );
        }
        finally
        {
            delete( file );
        }
    }   //  testLineBreak()
}
//  class BugHunt_20240404_001

/*
 *  End of File
 */