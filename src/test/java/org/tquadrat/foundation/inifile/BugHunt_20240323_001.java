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
import static java.lang.System.out;
import static java.nio.file.Files.readAllLines;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tquadrat.foundation.inifile.INIFile.create;
import static org.tquadrat.foundation.inifile.INIFile.open;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import java.io.File;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Longer String values got corrupted.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@DisplayName( "org.tquadrat.foundation.inifile.BugHunt_20240323_001" )
public class BugHunt_20240323_001 extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  The test.
     *
     *  @throw  Exception   Something unexpected got wrong.
     */
    @Test
    final void testLongValues() throws Exception
    {
        skipThreadTest();

        File file = createTempFile( "TestINIFile", "ini" );
        file.deleteOnExit();
        try
        {
            final var value = "Märker Schützengemeinschaft in Kooperation mit dem ASC Aplerbeck 09 und der Stadtsparkasse Dortmund";

            final var candidate1 = create( file.toPath() );
            candidate1.setValue( "group", "key", value );
            candidate1.save();

            readAllLines( file.toPath() ).forEach( out::println );

            final var candidate2 = open( file.toPath() );
            final var result = candidate2.getValue( "group", "key", EMPTY_STRING );
            assertEquals( value, result );
        }
        finally
        {
            file.delete();
        }
    }   //  testLongValues()
}
//  class BugHunt_20240323_001

/*
 *  End of File
 */