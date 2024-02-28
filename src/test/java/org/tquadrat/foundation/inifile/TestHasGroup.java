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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some tests for
 *  {@link INIFile#hasGroup(String)}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@DisplayName( "org.tquadrat.foundation.inifile.TestHasGroup" )
public class TestHasGroup extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Tests for
     *  {@link INIFile#hasGroup(String)}.
     *
     *  @param name The name candidate.
     *  @throws Exception   Some went wrong unexpectedly.
     */
    @ParameterizedTest
    @MethodSource( "org.tquadrat.foundation.inifile.internal.TestGroup#invalidNameFactory" )
    @ValueSource( strings = {" "} )
    @NullAndEmptySource
    final void testHasGroup( final String name ) throws Exception
    {
        skipThreadTest();

        final var file = createTempFile( "Test", ".test" );
        file.deleteOnExit();
        final var path = file.toPath();

        final var candidate = INIFile.create( path );
        assertFalse( candidate.hasGroup( name ) ); // NO EXCEPTION!
    }   //  testHasGroup()
}
//  class TestHasGroup

/*
 *  End of File
 */