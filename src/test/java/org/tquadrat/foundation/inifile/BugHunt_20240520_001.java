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

import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.exists;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import java.util.Currency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.testutil.TestBaseClass;
import org.tquadrat.foundation.util.stringconverter.CurrencyStringConverter;

/**
 *  When a value is set to {@code null}, the String &quot;null&quot; will be
 *  persisted.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@DisplayName( "org.tquadrat.foundation.inifile.BugHunt_20240404_001" )
public class BugHunt_20240520_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  When a value is set to {@code null}, the String &quot;null&quot; will be
     *  persisted.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @SuppressWarnings( "CommentedOutCode" )
    @Test
    final void testSetNull() throws Exception
    {
        skipThreadTest();

        final var group = "Group";
        final var key1 = "Key1";
        final var key2 = "Key2";
        final var key3 = "Key3";
        final var key4 = "Key4";
        final var value1 = Currency.getInstance( "EUR" );
        final var converter = CurrencyStringConverter.INSTANCE;
        final var value2 = "String ending with blank ";
        final var value3 = "String ending with linefeed\n";
        final var value4 = "String ending with tab\n";

        final var path = createTempFile( "File", ".ini" );
        assertTrue( exists( path ) );

        try
        {
            final var candidate = INIFile.open( path );
            assertNotNull( candidate );

            /*
             * Without persisting the INIFile, all is fine!
             */
            candidate.setValue( group, key1, value1, converter );
            candidate.setValue( group, key2, value2 );
            candidate.setValue( group, key3, value3 );
            candidate.setValue( group, key4, value4 );
            assertTrue( candidate.getValue( group, key1, converter ).isPresent() );
            assertTrue( candidate.getValue( group, key2 ).isPresent() );
            assertEquals( value2, candidate.getValue( group, key2, EMPTY_STRING ) );
            assertTrue( candidate.getValue( group, key3 ).isPresent() );
            assertEquals( value3, candidate.getValue( group, key3, EMPTY_STRING ) );
            assertTrue( candidate.getValue( group, key4 ).isPresent() );
            assertEquals( value4, candidate.getValue( group, key4, EMPTY_STRING ) );
            candidate.setValue( group, key1, null, converter );
            assertTrue( candidate.getValue( group, key1, converter ).isEmpty() );

            /*
             * After persisting the INIFile, we have a problem …
             */
            candidate.save();
            assertTrue( candidate.getValue( group, key1, converter ).isEmpty() );
            assertTrue( candidate.getValue( group, key2 ).isPresent() );
            assertEquals( value2, candidate.getValue( group, key2, EMPTY_STRING ) );
            assertTrue( candidate.getValue( group, key3 ).isPresent() );
            assertEquals( value3, candidate.getValue( group, key3, EMPTY_STRING ) );
            assertTrue( candidate.getValue( group, key4 ).isPresent() );
            assertEquals( value4, candidate.getValue( group, key4, EMPTY_STRING ) );
            candidate.refresh();

            //---* Problem validation *----------------------------------------
            //assertThrows( IllegalArgumentException.class, () -> candidate.getValue( group, key, converter ) );
            //assertTrue( candidate.getValue( group, key ).isPresent() );
            //assertEquals( "null", candidate.getValue( group, key ).get() );
            /*
             * After first fix
             */
            //assertEquals( EMPTY_STRING, candidate.getValue( group, key ).get() );

            //---* Fix validation *--------------------------------------------
            assertTrue( candidate.getValue( group, key1 ).isEmpty() );
            assertTrue( candidate.getValue( group, key1, converter ).isEmpty() );
            assertTrue( candidate.getValue( group, key2 ).isPresent() );
            assertEquals( value2, candidate.getValue( group, key2, EMPTY_STRING ) );
            assertTrue( candidate.getValue( group, key3 ).isPresent() );
            assertEquals( value3, candidate.getValue( group, key3, EMPTY_STRING ) );
            assertTrue( candidate.getValue( group, key4 ).isPresent() );
            assertEquals( value4, candidate.getValue( group, key4, EMPTY_STRING ) );
        }
        finally
        {
            //noinspection ThrowFromFinallyBlock
            deleteIfExists( path );
        }
//        final var e = assertThrows( IOException.class, () -> INIFile.open( path ) );
//        assertTrue( e.getMessage().contains( "has invalid structure" ) );
    }   //  testSetNull()

}
//  class BugHunt_20240520_001

/*
 *  End of File
 */