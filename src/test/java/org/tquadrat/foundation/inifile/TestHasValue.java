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

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests for
 *  {@link INIFile#hasValue(String, String)}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@DisplayName( "org.tquadrat.foundation.inifile.TestHasGroup" )
public class TestHasValue extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for
     *  {@link INIFile#hasValue(String, String)}
     *  with an invalid group name.
     *
     *  @param  name The name candidate.
     *  @throws Exception   Some went wrong unexpectedly.
     */
    @ParameterizedTest
    @MethodSource( "org.tquadrat.foundation.inifile.internal.TestGroup#invalidNameFactory" )
    @ValueSource( strings = {" "} )
    @NullAndEmptySource
    final void testHasValueWithInvalidGroup( final String name ) throws Exception
    {
        skipThreadTest();

        final var file = createTempFile( "Test", ".test" );
        file.deleteOnExit();
        final var path = file.toPath();

        final var key = "key";

        final var candidate = INIFile.create( path );
        assertFalse( candidate.hasValue( name, key ) ); // NO EXCEPTION!
    }   //  testHasValueWithInvalidGroup()

    /**
     *  Tests for
     *  {@link INIFile#hasValue(String, String)}
     *  with a missing group.
     *
     *  @param  key The key candidate.
     *  @throws Exception   Some went wrong unexpectedly.
     */
    @ParameterizedTest
    @MethodSource( "org.tquadrat.foundation.inifile.internal.TestValue#invalidKeyFactory" )
    @ValueSource( strings = {" "} )
    @NullAndEmptySource
    final void testHasValueWithMissingGroup( final String key ) throws Exception
    {
        skipThreadTest();

        final var file = createTempFile( "Test", ".test" );
        file.deleteOnExit();
        final var path = file.toPath();

        final var name = "name";

        final var candidate = INIFile.create( path );
        assertFalse( candidate.hasValue( name, key ) ); // NO EXCEPTION!
    }   //  testHasValueWithMissingGroup()

    /**
     *  Tests for
     *  {@link INIFile#hasValue(String, String)}
     *  with a missing group.
     *
     *  @param  key The key candidate.
     *  @throws Exception   Some went wrong unexpectedly.
     */
    @ParameterizedTest
    @MethodSource( "org.tquadrat.foundation.inifile.internal.TestValue#invalidKeyFactory" )
    @ValueSource( strings = {" "} )
    @NullAndEmptySource
    final void testHasValueWithValidGroup( final String key ) throws Exception
    {
        skipThreadTest();

        final var file = createTempFile( "Test", ".test" );
        file.deleteOnExit();
        final var path = file.toPath();

        final var name = "name";

        final var candidate = INIFile.create( path );
        candidate.setValue( name, "key", "value" );
        assertTrue( candidate.hasGroup( name ) );
        assertFalse( candidate.hasValue( name, key ) ); // NO EXCEPTION!
    }   //  testHasValueWithValidGroup()
}
//  class TestHasGroup

/*
 *  End of File
 */