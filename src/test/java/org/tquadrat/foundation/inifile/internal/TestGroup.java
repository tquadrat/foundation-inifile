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

package org.tquadrat.foundation.inifile.internal;

import static java.lang.System.out;
import static org.apiguardian.api.API.Status.STABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.util.StringUtils.format;

import org.apiguardian.api.API;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.EmptyArgumentException;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Test for the class
 *  {@link Group}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@ClassVersion( sourceVersion = "$Id: TestGroup.java 940 2021-12-16 13:55:52Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
@DisplayName( "org.tquadrat.foundation.inifile.internal.TestGroup" )
public class TestGroup extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Test for the constructors of
     *  {@link Group}.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testConstructor() throws Exception
    {
        skipThreadTest();

        final var candidate = new Group( "name" );
        assertNotNull( candidate );
    }   //  testConstructor()

    /**
     *  Test for the constructors of
     *  {@link Group}.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testConstructorWithEmptyArgument() throws Exception
    {
        skipThreadTest();

        final Class<? extends Throwable> expectedException = EmptyArgumentException.class;
        try
        {
            final var candidate = new Group( EMPTY_STRING );
            assertNotNull( candidate );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testConstructorWithEmptyArgument()

    /**
     *  Test for the constructors of
     *  {@link Group}.
     *
     *  @throws Exception   Something went wrong unexpectedly.
     */
    @Test
    final void testConstructorWithNullArgument() throws Exception
    {
        skipThreadTest();

        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            final var candidate = new Group( null );
            assertNotNull( candidate );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e )
        {
            throw e;
        }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            if( !isExpectedException )
            { t.printStackTrace( out ); }
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
    }   //  testConstructorWithNullArgument()

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

        String actual, expected;

        var candidate = new Group( "name" );
        assertNotNull( candidate );
        expected =
            """
            
            [name]
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
            [name]
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate.setValue( "key", "value").addComment( commentTextShort );
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
            [name]

            # Comment
            key = value
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );

        candidate = new Group( "name" );
        candidate.setValue( "key", "value" );
        expected =
            """
            
            [name]
            key = value
            """;
        actual = candidate.toString();
        assertEquals( expected, actual );
    }   //  testToString()
}
//  class TestGroup

/*
 *  End of File
 */