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

package org.tquadrat.foundation.inifile.internal;

import static org.apiguardian.api.API.Status.STABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;

import java.util.stream.Stream;

import org.apiguardian.api.API;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.BlankArgumentException;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Some test for
 *  {@link Value}
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@SuppressWarnings( "SpellCheckingInspection" )
@ClassVersion( sourceVersion = "$Id: TestValue.java 1105 2024-02-28 12:58:46Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
@DisplayName( "org.tquadrat.foundation.inifile.internal.TestValue" )
public class TestValue extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Returns a series of invalid keys.
     *
     *  @return The invalid key.
     */
    public static final Stream<String> invalidKeyFactory()
    {
        final var retValue = Stream.of( "#key", "[key", "=key", "k=ey", "key=", "key\n", "\nkey", "k\ney", "key\t", "\tkey", "k\tey" );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  invalidKeyFactory()

    /**
     *  Test for the constructors of
     *  {@link Value}.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testConstructor() throws Exception
    {
        skipThreadTest();

        final var group = new Group( "name" );
        final var key = "key";
        final String value = null;

        Value candidate;

        candidate = new Value( group, key );
        assertNotNull( candidate );

        candidate = new Value( group, key, value );
        assertNotNull( candidate );
    }   //  testConstructor()

    /**
     *  Test for the constructors of
     *  {@link Value}.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testConstructorWithInvalidArgument() throws Exception
    {
        skipThreadTest();

        final var group = new Group( "name" );
        final var key = "key";
        final var value = "value";

        assertThrows( NullArgumentException.class, () -> new Value( null, key, value ) );

        assertThrows( NullArgumentException.class, () -> new Value( group, null, value ) );
        assertThrows( EmptyArgumentException.class, () -> new Value( group, EMPTY_STRING, value ) );
        assertThrows( BlankArgumentException.class, () -> new Value( group, " ", value ) );
    }   //  testConstructorWithInvalidArgument()

    /**
     *  Test for the constructors of
     *  {@link Value}.
     *
     *  @param  key The candidate key.
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @ParameterizedTest
    @MethodSource( "invalidKeyFactory" )
    final void testConstructorWithInvalidArgument( final String key ) throws Exception
    {
        skipThreadTest();

        final var group = new Group( "name" );
        final var value = "value";

        assertThrows( ValidationException.class, () -> new Value( group, key, value ) );
    }   //  testConstructorWithInvalidArgument()

    /**
     *  Tests for
     *  {@link Value#toString()}.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testToString() throws Exception
    {
        skipThreadTest();

        final var commentTextShort = "Comment";
        final var commentTextLong =
            """
            Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam \
            nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam \
            erat, sed diam voluptua. At vero eos et accusam et justo duo \
            dolores et ea rebum. Stet clita kasd gubergren, no sea takimata \
            sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit \
            amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor \
            invidunt ut labore et dolore magna aliquyam erat, sed diam \
            voluptua. At vero eos et accusam et justo duo dolores et ea \
            rebum. Stet clita kasd gubergren, no sea takimata sanctus est \
            Lorem ipsum dolor sit amet.""";

        final var key = "key";

        final var valueShort = "value";
        final var valueLong =
            """
            Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam \
            nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam \
            erat, sed diam voluptua. At vero eos et accusam et justo duo \
            dolores et ea rebum. Stet clita kasd gubergren, no sea takimata \
            sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit \
            amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor \
            invidunt ut labore et dolore magna aliquyam erat, sed diam \
            voluptua. At vero eos et accusam et justo duo dolores et ea \
            rebum. Stet clita kasd gubergren, no sea takimata sanctus est \
            Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, \
            consetetur sadipscing elitr, sed diam.""";

        final var group = new Group( "name" );

        Value candidate;
        String actual, expected;

        candidate = new Value( group, key, valueShort );
        assertNotNull( candidate );
        expected =
            """
            key = value
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate.addComment( commentTextShort );
        expected =
            """
            
            # Comment
            key = value
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = new Value( group, key, valueLong );
        assertNotNull( candidate );
        expected =
            """
            key = Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam no\\
            umy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed di\\
            m voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet \\
            lita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit ame\\
            . Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy\\
            eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam v\\
            luptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clit\\
             kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. L\\
            rem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam.
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate.addComment( commentTextLong );
        expected =
            """
                        
            # Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy
            # eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam
            # voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet
            # clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit
            # amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam
            # nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
            # sed diam voluptua. At vero eos et accusam et justo duo dolores et ea
            # rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem
            # ipsum dolor sit amet.
            key = Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam no\\
            umy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed di\\
            m voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet \\
            lita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit ame\\
            . Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy\\
            eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam v\\
            luptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clit\\
             kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. L\\
            rem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam.
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testToString()
}
//  class TestValue

/*
 *  End of File
 */