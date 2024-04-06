/*
 * ============================================================================
 *  Copyright © 2002-2021 by Thomas Thrien.
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

package org.tquadrat.foundation.inifile.internal.inifileimpl;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tquadrat.foundation.util.StringUtils.splitString;

import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.inifile.internal.INIFileImpl;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Tests for
 *  {@link INIFileImpl#}
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@ClassVersion( sourceVersion = "$Id: TestParse.java 940 2021-12-16 13:55:52Z tquadrat $" )
@DisplayName( "org.tquadrat.foundation.inifile.internal.inifileimpl.TestParse" )
public class TestParse extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Test for
     *  {@link INIFileImpl#}.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @Test
    final void testParse() throws Exception
    {
        skipThreadTest();

        final var candidateConstructor = INIFileImpl.class.getDeclaredConstructor( Path.class );
        candidateConstructor.setAccessible( true );
        final var candidate = candidateConstructor.newInstance( (Path) null );

        final String actual, expected;

        expected =
            """
            # A test file for the configuration.
            # Last Update: 2021-05-01T19:29:37.779194450Z
            
            # The Global parameters.
            [Global]
            
            # Variable Number 1
            var1 = value1
            """;
        candidate.parse( asList( splitString( expected, "\n" ) ) );
        actual = candidate.dumpContents();
        assertEquals( expected, actual );
    }   //  testParse()
}
//  class TestParse

/*
 *  End of File
 */