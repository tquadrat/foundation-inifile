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

import static java.lang.Integer.signum;
import static java.lang.String.format;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.inifile.internal.INIFileImpl.breakString;
import static org.tquadrat.foundation.inifile.internal.INIFileImpl.splitComment;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.Objects.hash;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotBlankArgument;
import static org.tquadrat.foundation.lang.Objects.requireValidArgument;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import java.util.StringJoiner;
import java.util.function.Predicate;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.lang.Objects;

/**
 *  The container for the value of an INI file.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: Value.java 1104 2024-02-27 14:48:06Z tquadrat $
 *
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@SuppressWarnings( "NewClassNamingConvention" )
@ClassVersion( sourceVersion = "$Id: Value.java 1104 2024-02-27 14:48:06Z tquadrat $" )
@API( status = INTERNAL, since = "0.1.0" )
public final class Value implements Comparable<Value>
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The comment for this value.
     */
    @SuppressWarnings( "StringBufferField" )
    private final StringBuilder m_Comment = new StringBuilder();

    /**
     *  The reference to the parent group.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final Group m_Group;

    /**
     *  The key.
     */
    private final String m_Key;

    /**
     *  The value itself.
     */
    private String m_Value;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code Value}.
     *
     *  @param  parent  The parent group.
     *  @param  key The key.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public Value( final Group parent, final String key )
    {
        m_Group = requireNonNullArgument( parent, "parent" );
        m_Key = requireValidArgument( key, "key", Value::checkKeyCandidate );
    }   //  Value()

    /**
     *  Creates a new instance of {@code Value}.
     *
     *  @param  parent  The parent group.
     *  @param  key The key.
     *  @param  value   The value.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    public Value( final Group parent, final String key, final String value )
    {
        this( parent, key );
        setValue( value );
    }   //  Value()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Adds a comment to the value.
     *
     *  @param  comment The comment.
     */
    public final void addComment( final String comment )
    {
        if( isNotEmptyOrBlank( comment ) ) m_Comment.append( comment );
    }   //  addComment()

    /**
     *  <p>{@summary An implementation of
     *  {@link Predicate }
     *  to be used with
     *  {@link org.tquadrat.foundation.lang.Objects#requireValidArgument(Object,String,Predicate)}
     *  when checking the keys for new values.}</p>
     *  <p>The candidate may not begin with a hash symbol ('#') or an opening
     *  bracket ('['), and it may not contain newline characters, tab
     *  characters or equal signs.</p>
     *  <p>You should avoid hash symbols and opening brackets completely, even
     *  inside or at the end of the key, as well as closing brackets (']'),
     *  despite they are technically valid.</p>
     *
     *  @param  candidate   The key to check.
     *  @return {@code true} if the value is a valid key, {@code false}
     *      otherwise.
     *  @throws org.tquadrat.foundation.exception.NullArgumentException The
     *      candidate is {@code null}.
     *  @throws org.tquadrat.foundation.exception.EmptyArgumentException    The
     *      candidate is the empty string.
     *  @throws org.tquadrat.foundation.exception.BlankArgumentException    The
     *      candidate consists of whitespace only.
     *
     *  @since 0.4.4
     */
    @API( status = INTERNAL, since = "0.4.4" )
    public static final boolean checkKeyCandidate( final String candidate )
    {
        var retValue = requireNotBlankArgument( candidate, "candidate" ).indexOf( '=' ) < 0;
        if( retValue ) retValue = candidate.indexOf( '\n' ) < 0;
        if( retValue ) retValue = candidate.indexOf( '\t' ) < 0;
        if( retValue ) retValue = !candidate.trim().startsWith( "#" );
        if( retValue ) retValue = !candidate.trim().startsWith( "[" );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  checkKeyCandidate()

    /**
     *  {@inheritDoc}
     *
     *  @since 0.4.2
     */
    @SuppressWarnings( "CompareToUsesNonFinalVariable" )
    @API( status = MAINTAINED, since = "0.4.2" )
    @Override
    public int compareTo( final Value o )
    {
        var retValue = m_Group.compareTo( o.m_Group );
        if( retValue == 0) retValue = signum( m_Key.compareTo( o.m_Key ) );
        if( retValue == 0) retValue = signum( m_Value.compareTo( o.m_Value ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compareTo()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( {"EqualsOnSuspiciousObject", "NonFinalFieldReferenceInEquals"} )
    @Override
    public final boolean equals( final Object o )
    {
        var retValue = (this == o);
        if( !retValue && o instanceof final Value other )
        {
            retValue = m_Comment.equals( other.m_Comment )
                && m_Group.equals( other.m_Group )
                && m_Key.equals( other.m_Key )
                && Objects.equals( m_Value, other.m_Value );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  equals()

    /**
     *  Returns the current value.
     *
     *  @return The current value; can be {@code null}.
     */
    public final String getValue() { return m_Value; }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "NonFinalFieldReferencedInHashCode" )
    @Override
    public final int hashCode() { return hash( m_Comment, m_Group, m_Key, m_Value ); }

    /**
     *  <p>{@summary Sets a comment to the group.}</p>
     *  <p>Any previously existing comment will be overwritten.</p>
     *
     *  @param  comment The comment.
     *
     *  @since 0.4.2
     */
    @API( status = STABLE, since = "0.4.3" )
    public final void setComment( final String comment )
    {
        m_Comment.setLength( 0 );
        addComment( comment );
    }   //  setComment()

    /**
     *  Sets a new value.
     *
     *  @param  value   The new value; can be {@code null}.
     */
    public final void setValue( final String value ) { m_Value = value; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        final var buffer = new StringJoiner( "\n", EMPTY_STRING, "\n" );
        if( !m_Comment.isEmpty() )
        {
            buffer.add( EMPTY_STRING );
            splitComment( m_Comment ).forEach( buffer::add );
        }
        breakString( format( "%s = %s", m_Key, m_Value ) ).forEach( buffer::add );
        final var retValue = buffer.toString();

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toString()
}
//  class Value

/*
 *  End of File
 */